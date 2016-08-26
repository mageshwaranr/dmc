package com.thoongatechies.dmc.spec.service;

import com.thoongatechies.dmc.spec.def.ResponseContextCreator;
import com.thoongatechies.dmc.spec.def.Spec;
import com.thoongatechies.dmc.spec.def.SpecGenerator;
import com.thoongatechies.dmc.spec.vo.*;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by mageshwaranr on 7/24/2016.
 */
@Named
public class SpecServiceImpl implements SpecService {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private SpecGenerator<String> specGenerator;

    @Override
    public Spec parseSpec(String spec, Map<String, URL> bindingMap) {
        log.info("Generating Spec for {} ", spec);
        return specGenerator.generate(spec, bindingMap);
    }

    @Override
    public Collection<TriggerGroup> evaluateSpec(Trigger trigger, SpecExecutionState state, Spec spec) {
        Map<String, Object> qualifier = spec.qualifierFinder().findQualifier(trigger, spec);
        trigger.setQualifier(qualifier);

        boolean isRelatedEvent = spec.selector().test(trigger);
        if (!isRelatedEvent) {
            log.info("Trigger event {} with qualifier {} doesn't qualifies for spec {}", trigger.getName(), trigger.getQualifier(), spec.name());
            return Collections.emptyList();
        }

        Collection<TriggerGroup> processedGroups;

        if(spec.scoper().hasRetainLatestForAny()){
            processedGroups = evaluateSpecWithGlobalEvents(trigger, state, spec);
        } else {
            processedGroups = evaluateSpecWithOutGlobalEvents(trigger, state, spec);
        }
        populateResponseContext(processedGroups, spec);
        return processedGroups;
    }

    private Collection<TriggerGroup> evaluateSpecWithGlobalEvents(Trigger trigger, SpecExecutionState state, Spec spec) {
        Map<String,Object> groupedQualifier = spec.grouper().apply(trigger);

        //select global event group
        TriggerGroup globalTriggerGroup = state.getGlobalTriggerGroups().stream().findFirst().orElseGet(() -> {
            GlobalTriggerGroup newGroup = new GlobalTriggerGroup();
            newGroup.setGroupedData(groupedQualifier);
            state.addTriggerGroup(newGroup);
            return newGroup;
        });

        Collection<TriggerGroup> toBeEvaluated = new ArrayList<>();

        //if trigger is a global event & has no grouping criteria, add to global event group
        if(spec.scoper().isRetainLatest(trigger.getName())){
            globalTriggerGroup.addEvent(trigger);
            toBeEvaluated.addAll(selectByMatchingPartialContents(state.getPendingTriggerGroups(), groupedQualifier));
        } else {
            // identify the trigger event group
            TriggerGroup triggerTriggerGroup = selectByMatchingAllContents(state.getPendingTriggerGroups(),groupedQualifier);
            state.addTriggerGroup(triggerTriggerGroup);
            triggerTriggerGroup.addEvent(trigger);
            toBeEvaluated.add(triggerTriggerGroup);
        }

        if(state.getGlobalTriggerGroups().isEmpty()){
            return  toBeEvaluated.stream()
                    .filter(eGroup -> execExpression(spec,eGroup))
                    .map( TriggerGroup -> collectProcessedTriggerGroups(state, TriggerGroup, Optional.<TriggerGroup>empty()))
                    .collect(toList());
        } else {
            return state.getGlobalTriggerGroups().stream()
                    .flatMap(gGroup -> toBeEvaluated.stream()
                            .filter(eGroup -> execExpression(spec, new ComposedTriggerGroup(gGroup,eGroup)))
                            .map(TriggerGroup -> collectProcessedTriggerGroups(state,TriggerGroup, Optional.of(gGroup)))
                    ).collect(toList());

        }
    }

    private boolean execExpression(Spec spec, TriggerGroup eGroup) {
        log.debug("Evaluating spec {} for {} events grouped by {}", spec.rawSpec(), eGroup.getEventsByName(), eGroup.getGroupedData());
        try{
            Object result = MVEL.executeExpression(spec.executable(), eGroup);
            if(result instanceof Boolean)
                return (Boolean) result;
        } catch (Exception ex){
            log.info("Unable to execute expression ",ex);
            throw new RuntimeException("Expression can't be evaluated using "+eGroup,ex);
        }
        return false;
    }

    private Collection<TriggerGroup> evaluateSpecWithOutGlobalEvents(Trigger trigger, SpecExecutionState state, Spec spec) {
        Map<String,Object> groupedQualifier = spec.grouper().apply(trigger);
        TriggerGroup triggerTriggerGroup = selectByMatchingAllContents(state.getPendingTriggerGroups(), groupedQualifier);
        state.addTriggerGroup(triggerTriggerGroup);
        triggerTriggerGroup.addEvent(trigger);
        if(execExpression(spec,triggerTriggerGroup)){
            return Collections.singleton(collectProcessedTriggerGroups(state,triggerTriggerGroup, Optional.empty()));
        } else {
            log.info("Condition didnt met yet. Trigger event {} is added to pending events",trigger.getName());
        }
        return Collections.emptyList();
    }

    private TriggerGroup collectProcessedTriggerGroups(SpecExecutionState state, TriggerGroup triggerTriggerGroup, Optional<TriggerGroup> globalTriggerGroup) {
        state.getPendingTriggerGroups().remove(triggerTriggerGroup);
        globalTriggerGroup.ifPresent(gGroup -> gGroup.getEventsByName().forEach((evtName, mutableEvt) -> triggerTriggerGroup.addEvent(mutableEvt)));
        log.info("Condition met for {}. Grouped events are marked as processed");
        return triggerTriggerGroup;
    }

    private Collection<TriggerGroup> selectByMatchingPartialContents(Set<TriggerGroup> TriggerGroups, Map<String, Object> partialData) {
        Set<Map.Entry<String,Object>> partialQualifiers = partialData.entrySet();
        return TriggerGroups.stream().filter(each -> {
            Map<String, Object> groupedData = each.getGroupedData();
            return partialQualifiers.stream().allMatch(qualifierKv -> isSame(qualifierKv.getValue(), groupedData.get(qualifierKv.getKey())));
        }).collect(Collectors.toList());
    }

    private TriggerGroup selectByMatchingAllContents(Set<TriggerGroup> pendingTriggerGroups, Map<String, Object> allData) {
        Set<Map.Entry<String, Object>> allQualifiers = allData.entrySet();
        return pendingTriggerGroups.stream().filter(TriggerGroup -> {
            Map<String, Object> groupedData = TriggerGroup.getGroupedData();
            return groupedData.size() == allData.size()
                    && allQualifiers.stream().allMatch(qualifierKv -> isSame(qualifierKv.getValue(), groupedData.get(qualifierKv.getKey())));
        }).findFirst().orElseGet( () ->{
            TriggerGroup newGroup = new TriggerGroup();
            newGroup.setGroupedData(allData);
            return  newGroup;
        });
    }

    private <T> boolean isSame(T src, T tgt) {
        if(src == tgt) return true;
        else if (src == null || tgt == null) return false;
        else if (Spec.SPECIAL_VALUE_MATCH_ANY.equals(src) || Spec.SPECIAL_VALUE_MATCH_ANY.equals(tgt))
            return true;
        else if (tgt instanceof Collection && src instanceof Collection)
            return new HashSet((Collection)src).equals(new HashSet((Collection)tgt));
        else if ( src instanceof Number && tgt instanceof Number)
            return new BigDecimal(src.toString()).equals(new BigDecimal(tgt.toString()));
        return tgt.equals(src);
    }


    private void populateResponseContext(Collection<TriggerGroup> processedGroups, Spec spec) {
        ResponseContextCreator contextCreator = spec.responseCreator();
        processedGroups.forEach(group -> group.setResponseContext(contextCreator.apply(group.getEventsByName().values())));
    }
}
