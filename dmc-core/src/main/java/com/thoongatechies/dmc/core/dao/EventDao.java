package com.thoongatechies.dmc.core.dao;

import com.thoongatechies.require.dm.entity.EventEntity;
import com.thoongatechies.require.dm.entity.EventInstanceEntity;

import java.util.Collection;
import java.util.List;

/**
 * Created by mages_000 on 6/2/2016.
 */
public interface EventDao {

    void newEvent(EventEntity evt);
    void newEventInstance(EventInstanceEntity ei);

    void setProcessed(EventEntity evt);
    void setProcessed(EventInstanceEntity ei);

    List<EventEntity> eventToBeProcessed();
    List<EventInstanceEntity> instancesToBeProcessed();

    EventEntity eventById(long id);
    EventInstanceEntity instanceById(String id);

    List<EventEntity> eventByExtRef(String extRef);
    List<EventEntity> minimalEventDataByExtRefs(Collection<String> extRefs);

}
