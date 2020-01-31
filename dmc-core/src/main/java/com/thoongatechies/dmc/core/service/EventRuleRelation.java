package com.thoongatechies.dmc.core.service;

import com.thoongatechies.dmc.core.entity.RuleDefinitionEntity;
import com.thoongatechies.dmc.spec.def.Spec;

import java.util.List;

/**
 * Created by mages_000 on 6/2/2016.
 */
public interface EventRuleRelation {

    void initializeCache();

    void addRule(RuleDefinitionEntity ruleDefinition, Spec spec);

    void removeRule(RuleDefinitionEntity rule);

    List<String> getRelatedEvents(String ruleId);

    List<String> getRelatedRuleId(String eventName);

}
