package com.thoongatechies.require.dm.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import com.thoongatechies.dmc.spec.def.Spec;
import com.thoongatechies.dmc.spec.service.SpecService;
import com.thoongatechies.require.dm.dao.mongo.RuleDefinitionDaoImpl;
import com.thoongatechies.require.dm.entity.RuleDefinitionEntity;
import com.thoongatechies.require.dm.reactive.config.EntityServiceURLConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static com.thoongatechies.require.dm.entity.Constants.RULE_STATUS_ACTIVE;

/**
 * Created by mages_000 on 6/2/2016.
 */
@Named
public class EventRuleRelationImpl implements EventRuleRelation {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private SpecService specService;
    @Inject
    private RuleDefinitionDaoImpl ruleDao;
    @Inject
    private HazelcastInstance hz;
    @Inject
    private EntityServiceURLConfig urlConfig;

    private MultiMap<String, String> eventNameToRuleIdMapping;
    private MultiMap<String, String> ruleIdToEventNameMapping;

    @PostConstruct
    public void init() {
        eventNameToRuleIdMapping = hz.getMultiMap("eventNameToRuleIdMapping");
        ruleIdToEventNameMapping = hz.getMultiMap("ruleIdToEventNameMapping");
    }

    @Override
    public void initializeCache() {
        List<RuleDefinitionEntity> byStatus = ruleDao.findByStatus(RULE_STATUS_ACTIVE);
        byStatus.stream().forEach(ruleDefinition -> {
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
        ruleIdToEventNameMapping.remove(ruleId);
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
