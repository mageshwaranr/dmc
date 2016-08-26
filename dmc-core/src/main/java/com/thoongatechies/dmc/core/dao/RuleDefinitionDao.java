package com.thoongatechies.dmc.core.dao;

import com.thoongatechies.dmc.core.entity.CallbackDefinitionEntity;
import com.thoongatechies.dmc.core.entity.RuleDefinitionEntity;

import java.util.List;
import java.util.Optional;

/**
 * Created by mageshwaranr on 7/24/2016.
 */
public interface RuleDefinitionDao {
    RuleDefinitionEntity create(RuleDefinitionEntity ruleDef);

    CallbackDefinitionEntity addCallbackToRule(CallbackDefinitionEntity newCallback, RuleDefinitionEntity ruleDef, String senderName);

    Optional<RuleDefinitionEntity> deleteRuleById(String ruleId, String sender);

    Optional<RuleDefinitionEntity> deleteRuleByName(String name, String sender);

    void deleteCallbackByRuleIdAndCallbackName(String ruleId, String callbackName, String sender);

    List<RuleDefinitionEntity> findByStatus(String status);

    Optional<RuleDefinitionEntity> findLatestByName(String name);

    Optional<RuleDefinitionEntity> findById(String id);

    List<RuleDefinitionEntity> findByName(String name);

    List<RuleDefinitionEntity> findByNameAndStatus(String name, String status);

    Optional<RuleDefinitionEntity> findLatestByNameAndStatus(String name, String status);
}
