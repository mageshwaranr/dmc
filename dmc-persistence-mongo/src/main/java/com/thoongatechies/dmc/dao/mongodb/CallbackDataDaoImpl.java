package com.thoongatechies.dmc.dao.mongodb;

import com.thoongatechies.dmc.core.dao.CallbackDataDao;
import com.thoongatechies.dmc.core.entity.CallbackDataEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;

import static com.thoongatechies.dmc.core.entity.CallbackDataEntity.*;
import static com.thoongatechies.dmc.core.entity.Constants.CALLBACK_STATUS_ACTIVE;
import static com.thoongatechies.dmc.core.entity.Constants.DEPENDENCY_SYSTEM_USER;
import static com.thoongatechies.dmc.spec.vo.Trigger.NAME_COLUMN;
import static com.thoongatechies.dmc.core.entity.Constants.*;
import static com.thoongatechies.dmc.core.entity.EventEntity.EXT_REF_COLUMN;
import static com.thoongatechies.dmc.core.entity.EventEntity.ID_COLUMN;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by mages_000 on 6/3/2016.
 */
@Named
public class CallbackDataDaoImpl implements CallbackDataDao {

    private Logger log = LoggerFactory.getLogger(getClass());
    private Sort sort = new Sort(Sort.Direction.DESC, "lastUpdatedOn");

    @Inject
    private MongoTemplate template;

    @Override
    public CallbackDataEntity newCallbackDate(CallbackDataEntity data) {
        data.setCreatedOn(new Date());
        if (data.getCreatedBy() == null) {
            data.setCreatedBy(DEPENDENCY_SYSTEM_USER);
        }
        data.setStatus(CALLBACK_STATUS_ACTIVE);
        template.save(data);
        return data;
    }

    @Override
    public void update(CallbackDataEntity data) {
        data.setLastUpdatedOn(new Date());
        if (data.getLastUpdatedBy() == null)
            data.setLastUpdatedBy(DEPENDENCY_SYSTEM_USER);
        template.save(data);
    }

    @Override
    public CallbackDataEntity callbackById(String id) {
        Criteria criteria = new Criteria();
        criteria.orOperator(where(ID_COLUMN).is(id), where(UUID_COL).is(id));
        return template.findOne(query(criteria), CallbackDataEntity.class);
    }

    @Override
    public List<CallbackDataEntity> callbacksByEventId(Long eventId) {
        Query query = query(where(EVENTS_COLUMN).elemMatch(where(ID_COLUMN).is(eventId))).with(sort);
        return template.find(query, CallbackDataEntity.class);
    }

    @Override
    public List<CallbackDataEntity> callbacksByEventExtRef(String eventExtRef) {
        Query query = query(where(EVENTS_COLUMN).elemMatch(where(EXT_REF_COL).is(eventExtRef))).with(sort);
        return template.find(query, CallbackDataEntity.class);
    }

    @Override
    public List<CallbackDataEntity> callbacksByEventIds(List<Long> eventId) {
        Query query = query(where(EVENTS_COLUMN).elemMatch(where(ID_COLUMN).in(eventId))).with(sort);
        return template.find(query, CallbackDataEntity.class);
    }

    @Override
    public List<CallbackDataEntity> callbacksByUuids(List<String> uuids) {
        Query query = query(where(UUID_COL).in(uuids));
        projectSelectedColumns(query);
        return template.find(query, CallbackDataEntity.class);
    }

    private void projectSelectedColumns(Query query) {
        query.fields().include(SENDER_COLUMN).include(RULE_NAME_COLUMN).include(RULE_ID_COLUMN)
                .include(EVENTS_COLUMN + "." + EXT_REF_COLUMN)
                .include(EVENTS_COLUMN + "." + NAME_COLUMN)
                .include(EVENTS_COLUMN + "." + ID_COLUMN);
    }

    @Override
    public List<CallbackDataEntity> callbacksByRuleId(String ruleId, int limit, int page) {
        Query query = query(where(RULE_ID_COLUMN).in(ruleId))
                .with(new PageRequest(page, limit))
                .with(sort);
        projectSelectedColumns(query);
        return template.find(query, CallbackDataEntity.class);
    }

    @Override
    public List<CallbackDataEntity> callbacksByRuleName(String ruleName, int limit, int page) {
        Query query = query(where(RULE_NAME_COLUMN).in(ruleName))
                .with(new PageRequest(page, limit))
                .with(sort);
        projectSelectedColumns(query);
        return template.find(query, CallbackDataEntity.class);
    }

    @Override
    public List<CallbackDataEntity> callbacksToBeProcessed() {
        Query query = query(where(STATUS_COLUMN).is(CALLBACK_INSTANCE_STATUS_ACTIVE))
                .with(sort);
        return template.find(query, CallbackDataEntity.class);
    }
}
