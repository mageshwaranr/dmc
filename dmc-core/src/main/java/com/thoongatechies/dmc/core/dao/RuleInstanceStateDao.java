package com.thoongatechies.dmc.core.dao;

import com.thoongatechies.dmc.core.entity.RuleInstanceStateEntity;

/**
 * Created by mages_000 on 6/2/2016.
 */
public interface RuleInstanceStateDao {

    RuleInstanceStateEntity newBarrierState(String ruleId);

    void update(RuleInstanceStateEntity state);

    RuleInstanceStateEntity findByRuleId(String ruleId);
}
