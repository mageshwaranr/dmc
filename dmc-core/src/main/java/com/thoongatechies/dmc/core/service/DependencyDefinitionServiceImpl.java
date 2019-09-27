package com.thoongatechies.dmc.core.service;

import com.thoongatechies.dmc.core.dao.RuleDefinitionDao;
import com.thoongatechies.dmc.core.entity.CallbackDefinitionEntity;
import com.thoongatechies.dmc.core.entity.RuleDefinitionEntity;
import com.thoongatechies.dmc.core.entity.Sender;
import com.thoongatechies.dmc.core.vo.CallbackDefinition;
import com.thoongatechies.dmc.core.vo.RuleDefinition;
import com.thoongatechies.dmc.spec.def.Spec;
import com.thoongatechies.dmc.spec.service.SpecService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.thoongatechies.dmc.core.entity.Constants.RULE_STATUS_ACTIVE;

/**
 * Created by mages_000 on 09-Jun-16.
 */
@Service
public class DependencyDefinitionServiceImpl implements DependencyDefinitionService {

    @Inject
    private ModelMapper mapper;

    @Inject
    private RuleDefinitionDao ruleDefinitionDao;

    @Inject
    private EventRuleRelation ruleRelation;

    @Inject
    private SpecService specService;

    @Override
    public RuleDefinition registerNewRule(RuleDefinition rule, Sender sender) {
        Spec spec = specService.parseSpec(rule.getExpression(), Collections.emptyMap());
        if (spec == null)
            throw new BadRequestException("Rule Expression is invalid");
        RuleDefinitionEntity def = mapper.toDMRuleDefinition(rule, sender);
        RuleDefinitionEntity created = ruleDefinitionDao.create(def);
        ruleRelation.addRule(created, spec);
        return mapper.toRule(created);
    }

    @Override
    public boolean validateRule(String expression) {
        return specService.parseSpec(expression, Collections.emptyMap()) != null;
    }

    @Override
    public RuleDefinition findRuleByName(String ruleName) {
        Optional<RuleDefinitionEntity> latestByName = ruleDefinitionDao.findLatestByName(ruleName.trim());
        if (latestByName.isPresent())
            return mapper.toRule(latestByName.get());
        else
            throw new BadRequestException("No rule with name " + ruleName + " found");
    }

    @Override
    public RuleDefinition findByNameAndStatus(String ruleName, String status) {
        Optional<RuleDefinitionEntity> latest = ruleDefinitionDao.findLatestByNameAndStatus(ruleName, status);
        if (latest.isPresent())
            return mapper.toRule(latest.get());
        else
            throw new BadRequestException("No rule with name " + ruleName + "and status " + status + " found");
    }

    @Override
    public RuleDefinition findRuleById(String id) {
        Optional<RuleDefinitionEntity> latest = ruleDefinitionDao.findById(id);
        if (latest.isPresent())
            return mapper.toRule(latest.get());
        else
            throw new BadRequestException("No rule with id " + id + " found");
    }

    @Override
    public List<RuleDefinition> findActiveRules() {
        return this.findRuleByStatus(RULE_STATUS_ACTIVE);
    }

    @Override
    public List<RuleDefinition> findRuleByStatus(String status) {
        List<RuleDefinitionEntity> byStatus = ruleDefinitionDao.findByStatus(status);
        return byStatus.stream().map(mapper::toRule).collect(Collectors.toList());
    }

    @Override
    public RuleDefinition deleteRuleByName(String name, Sender sender) {
        Optional<RuleDefinitionEntity> dmRuleDefinition = ruleDefinitionDao.deleteRuleByName(name, sender.getName());
        if (dmRuleDefinition.isPresent())
            return mapper.toRule(dmRuleDefinition.get());
        else
            throw new BadRequestException("No rule with name " + name + " found");
    }

    @Override
    public RuleDefinition deleteRuleById(String id, Sender sender) {
        Optional<RuleDefinitionEntity> dmRuleDefinition = ruleDefinitionDao.deleteRuleById(id, sender.getName());
        if (dmRuleDefinition.isPresent())
            return mapper.toRule(dmRuleDefinition.get());
        else
            throw new BadRequestException("No rule with id " + id + " found");
    }

    @Override
    public CallbackDefinition addCallbackToRuleById(String ruleId, CallbackDefinition callback, Sender sender) {
        Optional<RuleDefinitionEntity> latest = ruleDefinitionDao.findById(ruleId);
        Optional<CallbackDefinition> callbackOptional = latest.map(ruleDef -> {
            CallbackDefinitionEntity callbackDef = mapper.toCallbackDef(callback);
            CallbackDefinitionEntity definition = ruleDefinitionDao.addCallbackToRule(callbackDef, ruleDef, sender.getName());
            return mapper.toCallback(definition);
        });
        return callbackOptional.<BadRequestException>orElseThrow(() -> {
            throw new BadRequestException("No rule with id " + ruleId + " found");
        });
    }

    @Override
    public CallbackDefinition addCallbackToRuleByName(String ruleName, CallbackDefinition callback, Sender sender) {
        Optional<RuleDefinitionEntity> latest = ruleDefinitionDao.findLatestByName(ruleName);
        Optional<CallbackDefinition> callbackOptional = latest.map(ruleDef -> {
            CallbackDefinitionEntity callbackDef = mapper.toCallbackDef(callback);
            CallbackDefinitionEntity definition = ruleDefinitionDao.addCallbackToRule(callbackDef, ruleDef, sender.getName());
            return mapper.toCallback(definition);
        });
        return callbackOptional.<BadRequestException>orElseThrow(() -> {
            throw new BadRequestException("No rule with name " + ruleName + " found");
        });
    }

    @Override
    public List<CallbackDefinition> findCallbackByRuleId(String ruleId) {
        return ruleDefinitionDao.findById(ruleId)
                .map( rule -> rule.getCallbacks().stream().map(mapper::toCallback))
                .<BadRequestException>orElseThrow(() -> {
                    throw new BadRequestException("No rule with id " + ruleId + " found");
                }).collect(Collectors.toList());
    }

    @Override
    public List<CallbackDefinition> findCallbackByRuleName(String name) {
        return ruleDefinitionDao.findLatestByName(name)
                .map( rule -> rule.getCallbacks().stream().map(mapper::toCallback))
                .<BadRequestException>orElseThrow(() -> {
                    throw new BadRequestException("No rule with name " + name + " found");
                }).collect(Collectors.toList());
    }

    @Override
    public List<CallbackDefinition> findCallbackByRuleIdAndCallbackName(String ruleId, String callbackName) {
        return ruleDefinitionDao.findById(ruleId)
                .map(rule -> rule.getCallbacks().stream()
                        .filter(dmCallbackDefinition -> dmCallbackDefinition.getName().equals(callbackName))
                        .map(mapper::toCallback))
                .<BadRequestException>orElseThrow(() -> {
                    throw new BadRequestException("No rule with id " + ruleId + " found");
                }).collect(Collectors.toList());
    }

    @Override
    public List<CallbackDefinition> findCallbackByRuleNameAndCallbackName(String ruleName, String callbackName) {
        return ruleDefinitionDao.findLatestByName(ruleName)
                .map( rule -> rule.getCallbacks().stream()
                        .filter(dmCallbackDefinition -> dmCallbackDefinition.getName().equals(callbackName))
                        .map(mapper::toCallback))
                .<BadRequestException>orElseThrow(() -> {
                    throw new BadRequestException("No rule with name " + ruleName + " found");
                }).collect(Collectors.toList());
    }

    @Override
    public void deleteCallbackByRuleIdAndCallbackName(String ruleId, String callbackName, Sender sender) {
        ruleDefinitionDao.deleteCallbackByRuleIdAndCallbackName(ruleId, callbackName, sender.getName());
    }

    @Override
    public void deleteCallbackByRuleNameAndCallbackName(String ruleName, String callbackName, Sender sender) {
        ruleDefinitionDao.findLatestByName(ruleName)
                .ifPresent(ruleDefinition -> {
                    ruleDefinitionDao.deleteCallbackByRuleIdAndCallbackName(ruleDefinition.getId(), callbackName, sender.getName());
                });
    }

    @Override
    public List<String> findAllEventsOfARule(String ruleId) {
        return ruleRelation.getRelatedEvents(ruleId);
    }
}
