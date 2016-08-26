package com.thoongatechies.require.dm.dao;

import com.thoongatechies.require.dm.entity.RuleInstanceStateEntity;

/**
 * Created by mages_000 on 6/2/2016.
 */
public interface RuleInstanceStateDao {

    RuleInstanceStateEntity newBarrierState(String ruleId);

    void update(RuleInstanceStateEntity state);

    RuleInstanceStateEntity findByRuleId(String ruleId);
}
