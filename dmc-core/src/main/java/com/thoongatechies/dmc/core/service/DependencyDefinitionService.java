package com.thoongatechies.dmc.core.service;

import com.thoongatechies.require.dm.entity.Sender;
import com.thoongatechies.require.dm.vo.CallbackDefinition;
import com.thoongatechies.require.dm.vo.RuleDefinition;

import java.util.List;

/**
 * Created by mages_000 on 6/1/2016.
 */
public interface DependencyDefinitionService {


    RuleDefinition registerNewRule(RuleDefinition rule, Sender sender);

    boolean validateRule(String expression);

    RuleDefinition findRuleByName(String ruleName);

    RuleDefinition findByNameAndStatus(String ruleName, String status);

    RuleDefinition findRuleById(String id);

    List<RuleDefinition> findActiveRules();

    List<RuleDefinition> findRuleByStatus(String status);

    RuleDefinition deleteRuleByName(String name, Sender sender);

    RuleDefinition deleteRuleById(String id, Sender sender);

    CallbackDefinition addCallbackToRuleById(String ruleId, CallbackDefinition callback, Sender sender);
    CallbackDefinition addCallbackToRuleByName(String ruleName, CallbackDefinition callback, Sender sender);

    List<CallbackDefinition> findCallbackByRuleId(String ruleId);
    List<CallbackDefinition> findCallbackByRuleName(String name);
    List<CallbackDefinition> findCallbackByRuleIdAndCallbackName(String ruleId, String callbackName);
    List<CallbackDefinition> findCallbackByRuleNameAndCallbackName(String ruleName, String callbackName);

    void deleteCallbackByRuleIdAndCallbackName(String ruleId, String callbackName, Sender sender);
    void deleteCallbackByRuleNameAndCallbackName(String ruleName, String callbackName, Sender sender);

    List<String> findAllEventsOfARule(String ruleId);
}
