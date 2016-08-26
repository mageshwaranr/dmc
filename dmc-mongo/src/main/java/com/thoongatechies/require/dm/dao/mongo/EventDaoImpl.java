package com.thoongatechies.require.dm.dao.mongo;

import com.thoongatechies.dmc.spec.vo.Trigger;
import com.thoongatechies.require.dm.dao.EventDao;
import com.thoongatechies.require.dm.entity.EventEntity;
import com.thoongatechies.require.dm.entity.EventInstanceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.thoongatechies.require.dm.entity.Constants.*;
import static com.thoongatechies.require.dm.entity.EventEntity.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by mages_000 on 6/3/2016.
 */
@Named
public class EventDaoImpl implements EventDao {
    @Inject
    private NamedSequenceDao sequence;
    @Inject
    private MongoTemplate template;
    private Logger log = LoggerFactory.getLogger(getClass());
    private final Sort sort = new Sort(Sort.Direction.DESC, "lastUpdatedOn");

    @Override
    public void newEvent(EventEntity evt) {
        evt.setId(sequence.getNextEventSequence());
        evt.setCreatedOn(new Date());
        if (evt.getCreatedBy() == null)
            evt.setCreatedBy(DEPENDENCY_SYSTEM_USER);
        evt.setStatus(EVENT_STATUS_ACTIVE);
        template.save(evt);
        log.debug("Saved incoming event {} with id {}", evt.getName(), evt.getId());
    }

    @Override
    public void newEventInstance(EventInstanceEntity ei) {
        ei.setId(ei.getRuleId() + ":" + ei.getEventId());
        ei.setStatus(EVENT_INSTANCE_STATUS_FAN_OUT);
        ei.setCreatedOn(new Date());
        if (ei.getCreatedBy() == null)
            ei.setCreatedBy(DEPENDENCY_SYSTEM_USER);
        log.debug("Saving new event instance {} ", ei.getId());
        template.save(ei);
    }

    @Override
    public void setProcessed(EventEntity evt) {
        evt.setStatus(EVENT_STATUS_PROCESSED);
        evt.setLastUpdatedOn(new Date());
        if (evt.getLastUpdatedBy() == null)
            evt.setLastUpdatedBy(DEPENDENCY_SYSTEM_USER);
        template.save(evt);
    }

    @Override
    public void setProcessed(EventInstanceEntity ei) {
        ei.setLastUpdatedOn(new Date());
        if (ei.getLastUpdatedBy() == null)
            ei.setLastUpdatedBy(DEPENDENCY_SYSTEM_USER);
        ei.setStatus(EVENT_INSTANCE_STATUS_PROCESSED);
        template.save(ei);
    }

    @Override
    public List<EventEntity> eventToBeProcessed() {
        Query query = query(where(STATUS_COLUMN).is(EVENT_STATUS_ACTIVE)).with(sort);
        log.info("Find events to be processed using {}", query);
        return template.find(query, EventEntity.class);
    }

    @Override
    public List<EventInstanceEntity> instancesToBeProcessed() {
        Query query = query(where(EventInstanceEntity.STATUS_COLUMN).is(EVENT_INSTANCE_STATUS_FAN_OUT)).with(sort);
        log.info("Find event instances to be processed using {}", query);
        return template.find(query, EventInstanceEntity.class);
    }

    @Override
    public EventEntity eventById(long id) {
        Query query = query(where(ID_COLUMN).is(id));
        log.info("Find event by {}", query);
        return template.findOne(query, EventEntity.class);
    }

    @Override
    public EventInstanceEntity instanceById(String id) {
        Query query = query(where(EventInstanceEntity.ID_COLUMN).is(id));
        log.info("Find event by {}", query);
        return template.findOne(query, EventInstanceEntity.class);
    }

    @Override
    public List<EventEntity> eventByExtRef(String extRef) {
        Query query = query(where(EXT_REF_COLUMN).is(extRef));
        log.info("Find event by {}", query);
        return template.find(query, EventEntity.class);
    }

    @Override
    public List<EventEntity> minimalEventDataByExtRefs(Collection<String> extRefs) {
        Query query = query(where(EXT_REF_COLUMN).in(extRefs));
        query.fields().include(EXT_REF_COLUMN).include(Trigger.NAME_COLUMN);
        log.info("Find event by {}", query);
        return template.find(query, EventEntity.class);
    }

    @PostConstruct
    public void ensureIndexes() {
        template.indexOps(EventEntity.class).ensureIndex(new Index().on(EXT_REF_COLUMN, Sort.Direction.ASC));
        template.indexOps(EventEntity.class).ensureIndex(new Index().on(STATUS_COLUMN, Sort.Direction.ASC));
        template.indexOps(EventInstanceEntity.class).ensureIndex(new Index().on(EventInstanceEntity.STATUS_COLUMN, Sort.Direction.ASC));
    }
}
