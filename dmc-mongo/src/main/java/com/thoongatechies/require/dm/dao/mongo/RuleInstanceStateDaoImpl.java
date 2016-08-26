package com.thoongatechies.require.dm.dao.mongo;

import com.thoongatechies.require.dm.dao.RuleInstanceStateDao;
import com.thoongatechies.require.dm.entity.RuleInstanceStateEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.Objects;

import static com.thoongatechies.require.dm.entity.Constants.DEPENDENCY_SYSTEM_USER;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by mages_000 on 6/3/2016.
 */
@Named
public class RuleInstanceStateDaoImpl implements RuleInstanceStateDao {
    @Inject
    private MongoTemplate template;

    @Override
    public RuleInstanceStateEntity newBarrierState(String ruleId) {
        Objects.requireNonNull(ruleId, "RUle Is is required to create a barrier state");
        RuleInstanceStateEntity state = new RuleInstanceStateEntity();
        state.setId(ruleId);
        state.setCreatedOn(new Date());
        if (state.getCreatedBy() == null) state.setCreatedBy(DEPENDENCY_SYSTEM_USER);
        template.save(state);
        return state;
    }

    @Override
    public void update(RuleInstanceStateEntity state) {
        Objects.requireNonNull(state.getId(), "RUle Is is required to update barrier state");
        state.setLastUpdatedOn(new Date());
        if (state.getLastUpdatedBy() == null) state.setLastUpdatedBy(DEPENDENCY_SYSTEM_USER);
        template.save(state);
    }

    @Override
    public RuleInstanceStateEntity findByRuleId(String ruleId) {
        Query query = query(where(RuleInstanceStateEntity.ID_COLUMN).is(ruleId));
        RuleInstanceStateEntity one = template.findOne(query,RuleInstanceStateEntity.class);
        if(one == null)
            one = newBarrierState(ruleId);
        return one;
    }
}
