package com.thoongatechies.require.dm.reactive.handlers;

import com.thoongatechies.dmc.spec.def.Spec;
import com.thoongatechies.dmc.spec.service.SpecService;
import com.thoongatechies.dmc.spec.vo.Trigger;
import com.thoongatechies.dmc.spec.vo.TriggerGroup;
import com.thoongatechies.require.dm.dao.CallbackDataDao;
import com.thoongatechies.require.dm.dao.EventDao;
import com.thoongatechies.require.dm.dao.RuleDefinitionDao;
import com.thoongatechies.require.dm.dao.RuleInstanceStateDao;
import com.thoongatechies.require.dm.dao.mongo.RuleDefinitionDaoImpl;
import com.thoongatechies.require.dm.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mageshwaranr on 7/24/2016.
 */
@Named
public class EventInstanceHandler {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private EventDao eventRepo;
    @Inject
    private RuleDefinitionDaoImpl ruleDao;
    @Inject
    private SpecService specService;
    @Inject
    private CallbackDataDao callbacksRepository;
    @Inject
    private RuleInstanceStateDao stateRepo;
    @Inject
    private RuleDefinitionDao ruleDefinitionDao;


    /**
     * Evaluate the Event Instance (event : rule combination)
     * For each instance,
     * <pre>
     *     1. Find the Rule Instance State for the given rule Id
     *     2. With the latest state, add the incoming event and evaluate rule
     *     3. Save the latest rule instance state
     *     4. If matched, create necessary callbacks
     *     5. Mark Event Instance as processed
     * </pre>
     *
     * @param evtInstances
     * @return
     */
    public Collection<CallbackDataEntity> handle(EventEntity evt, Collection<EventInstanceEntity> evtInstances) {
       return evtInstances.stream()
                .flatMap(evtInstance -> evaluateSpec(evt, evtInstance))
                .collect(Collectors.toList());
    }

    private Stream<CallbackDataEntity> evaluateSpec(EventEntity evt, EventInstanceEntity evtInstance) {
        String ruleId = evtInstance.getRuleId();
        RuleInstanceStateEntity state = stateRepo.findByRuleId(ruleId);
        RuleDefinitionEntity ruleDef = ruleDefinitionDao.findById(ruleId).get();
        Spec spec = specService.parseSpec(ruleDef.getExpression(), Collections.emptyMap());
        Collection<TriggerGroup> matchedGroups = specService.evaluateSpec(evt, state, spec);
        stateRepo.update(state);
        log.debug("Processing event instance {} resulted in {} matched groups", evtInstance.getId(), matchedGroups.size());
        Stream<CallbackDataEntity> callbackDataStream = Stream.empty();
        if (matchedGroups.size() > 0) {
            List<CallbackDefinitionEntity> callbackDefs = ruleDef.getCallbacks();
            callbackDataStream = matchedGroups.stream()
                    .flatMap(matchedGroup -> toSavedCallbackData(matchedGroup, ruleDef, callbackDefs));
        }
        eventRepo.setProcessed(evtInstance);
        return callbackDataStream;
    }

    private Stream<CallbackDataEntity> toSavedCallbackData(TriggerGroup matchedGroup, RuleDefinitionEntity ruleDef, List<CallbackDefinitionEntity> callbackDefs) {
        return callbackDefs.stream().map(callbackDef -> {
            CallbackDataEntity data = new CallbackDataEntity();
            data.setQualifier(matchedGroup.getResponseContext());
            List<Trigger> events = new ArrayList<>();
            matchedGroup.getAllEventsByName().values().forEach(events::addAll);
            data.setEvents(events);
            data.setData(callbackDef.getData());
            data.setCallback(callbackDef);
            data.setRule(ruleDef);
            CallbackDataEntity callbackDataEntity = callbacksRepository.newCallbackDate(data);
            log.debug("Created a callback data for rule {} : with id {} ",ruleDef.getId(),callbackDataEntity.getId() );
            return callbackDataEntity;
        });

    }



}
