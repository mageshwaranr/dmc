package com.thoongatechies.dmc.core.service;


import com.thoongatechies.dmc.core.dao.RuleDefinitionDao;
import com.thoongatechies.dmc.core.entity.RuleDefinitionEntity;
import com.thoongatechies.dmc.core.processing.callback.EntityServiceURLConfig;
import com.thoongatechies.dmc.spec.def.Spec;
import com.thoongatechies.dmc.spec.service.SpecService;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.factory.Multimaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static com.thoongatechies.dmc.core.entity.Constants.RULE_STATUS_ACTIVE;


/**
 * Created by mages_000 on 6/2/2016.
 */
@Named
public class EventRuleRelationImpl implements EventRuleRelation {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private SpecService specService;
    @Inject
    private RuleDefinitionDao ruleDao;

    @Inject
    private EntityServiceURLConfig urlConfig;

    private MutableMultimap<String, String> eventNameToRuleIdMapping;
    private MutableMultimap<String, String> ruleIdToEventNameMapping;

    @PostConstruct
    public void init() {
        eventNameToRuleIdMapping = Multimaps.mutable.set.of();
        ruleIdToEventNameMapping = Multimaps.mutable.set.of();
    }

    @Override
    public void initializeCache() {
        List<RuleDefinitionEntity> byStatus = ruleDao.findByStatus(RULE_STATUS_ACTIVE);
        byStatus.forEach(ruleDefinition -> {
            Spec spec = specService.parseSpec(ruleDefinition.getExpression(), urlConfig.getConfig());
            fillCache(spec, ruleDefinition.getId());
        });
    }

    private void fillCache(Spec spec, String id) {
        spec.eventNames().forEach(evtName -> {
            eventNameToRuleIdMapping.put(evtName, id);
            ruleIdToEventNameMapping.put(id, evtName);
        });
    }

    @Override
    public void addRule(RuleDefinitionEntity ruleDefinition, Spec spec) {
        fillCache(spec, ruleDefinition.getId());
    }

    @Override
    public void removeRule(RuleDefinitionEntity rule) {
        String ruleId = rule.getId();
        ruleIdToEventNameMapping.get(ruleId).forEach(evtName -> eventNameToRuleIdMapping.remove(evtName, ruleId));
        ruleIdToEventNameMapping.removeAll(ruleId);
    }

    @Override
    public List<String> getRelatedEvents(String ruleId) {
        return new ArrayList<>(ruleIdToEventNameMapping.get(ruleId));
    }

    @Override
    public List<String> getRelatedRuleId(String eventName) {
        return new ArrayList<>(eventNameToRuleIdMapping.get(eventName));
    }

}
