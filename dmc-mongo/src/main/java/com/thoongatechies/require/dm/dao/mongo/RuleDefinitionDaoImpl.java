package com.thoongatechies.require.dm.dao.mongo;

import com.thoongatechies.require.dm.dao.RuleDefinitionDao;
import com.thoongatechies.require.dm.entity.CallbackDefinitionEntity;
import com.thoongatechies.require.dm.entity.RuleDefinitionEntity;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static com.thoongatechies.require.dm.dao.mongo.RuleDefinitionRepository.FETCH_LATEST_ONE_BY_VERSION_NO;
import static com.thoongatechies.require.dm.dao.mongo.RuleDefinitionRepository.SORT_BY_VERSION_NO;
import static com.thoongatechies.require.dm.entity.Constants.*;

/**
 * Created by mages_000 on 6/3/2016.
 */
@Named
public class RuleDefinitionDaoImpl implements RuleDefinitionDao {

    @Inject
    private RuleDefinitionRepository repo;

    @Override
    public RuleDefinitionEntity create(RuleDefinitionEntity ruleDef) {
        Optional<RuleDefinitionEntity> existing = findLatestByName(ruleDef.getName());
        ruleDef.setVersionNo(1L);
        existing.ifPresent(oldDef -> {
            if (RULE_STATUS_ACTIVE.equals(oldDef.getStatus())) {
                throw new RuntimeException("A rule with same name is active already");
            } else {
                ruleDef.setVersionNo(oldDef.getVersionNo() + 1);
            }
        });
        ruleDef.setStatus(RULE_STATUS_ACTIVE);
        ruleDef.setId(ruleDef.getName() + ":" + ruleDef.getVersionNo());
        ruleDef.setCreatedOn(new Date());
        ruleDef.getCallbacks().forEach(callback -> callback.setStatus(CALLBACK_STATUS_ACTIVE));
        return repo.save(ruleDef);
    }

    @Override
    public CallbackDefinitionEntity addCallbackToRule(CallbackDefinitionEntity newCallback, RuleDefinitionEntity ruleDef, String senderName) {
        boolean alreadyPresent = ruleDef.getCallbacks().stream().anyMatch(existing -> existing.getName().equals(newCallback.getName()));
        if (alreadyPresent) {
            throw new RuntimeException("Callback name is already present. Please try with different name");
        }
        newCallback.setStatus(CALLBACK_STATUS_ACTIVE);
        ruleDef.getCallbacks().add(newCallback);
        ruleDef.setLastUpdatedBy(senderName);
        ruleDef.setLastUpdatedOn(new Date());
        repo.save(ruleDef);
        return newCallback;
    }

    @Override
    public Optional<RuleDefinitionEntity> deleteRuleById(String ruleId, String sender){
        Optional<RuleDefinitionEntity> ruleDefinition = findById(ruleId);
        ruleDefinition.ifPresent(toBeDeleted -> {
            toBeDeleted.setStatus(RULE_STATUS_INACTIVE);
            toBeDeleted.setLastUpdatedOn(new Date());
            toBeDeleted.setLastUpdatedBy(sender);
            repo.save(toBeDeleted);
        });
        return ruleDefinition;
    }
    @Override
    public Optional<RuleDefinitionEntity> deleteRuleByName(String name, String sender){
        Optional<RuleDefinitionEntity> ruleDefinition = findLatestByName(name);
        ruleDefinition.ifPresent(toBeDeleted -> {
            toBeDeleted.setStatus(RULE_STATUS_INACTIVE);
            toBeDeleted.setLastUpdatedOn(new Date());
            toBeDeleted.setLastUpdatedBy(sender);
            repo.save(toBeDeleted);
        });
        return ruleDefinition;
    }

    @Override
    public void deleteCallbackByRuleIdAndCallbackName(String ruleId, String callbackName, String sender){
        Optional<RuleDefinitionEntity> ruleDefinition = findById(ruleId);
        ruleDefinition.ifPresent(rule -> rule.getCallbacks().stream()
                .filter( existing -> existing.getName().equals(callbackName))
                .findFirst()
                .ifPresent(toBeDeleted -> {
                    toBeDeleted.setStatus(CALLBACK_STATUS_IN_ACTIVE);
                    rule.setLastUpdatedOn(new Date());
                    rule.setLastUpdatedBy(sender);
                    repo.save(rule);
                }));
    }

    @Override
    public List<RuleDefinitionEntity> findByStatus(String status) {
        List<RuleDefinitionEntity> rules = repo.ruleDefinitionByStatus(status, SORT_BY_VERSION_NO);
        return checkAndGetNonDuplicates(rules);
    }

    @Override
    public Optional<RuleDefinitionEntity> findLatestByName(String name) {
        List<RuleDefinitionEntity> rules = findByName(name);
        return rules.stream().findFirst();
    }

    @Override
    public Optional<RuleDefinitionEntity> findById(String id) {
        List<RuleDefinitionEntity> rules = repo.ruleDefinitionById(id, FETCH_LATEST_ONE_BY_VERSION_NO);
        return rules.stream().findFirst();
    }

    @Override
    public List<RuleDefinitionEntity> findByName(String name) {
        return repo.ruleDefinitionByName(name, FETCH_LATEST_ONE_BY_VERSION_NO);
    }

    @Override
    public List<RuleDefinitionEntity> findByNameAndStatus(String name, String status) {
        return repo.ruleDefinitionByNameAndStatus(name, status, FETCH_LATEST_ONE_BY_VERSION_NO);
    }

    @Override
    public Optional<RuleDefinitionEntity> findLatestByNameAndStatus(String name, String status) {
        return repo.ruleDefinitionByNameAndStatus(name, status, FETCH_LATEST_ONE_BY_VERSION_NO).stream().findFirst();
    }


    private List<RuleDefinitionEntity> checkAndGetNonDuplicates(List<RuleDefinitionEntity> rules) {
        return new ArrayList<>(new HashSet<>(rules));
    }
}
