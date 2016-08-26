package com.thoongatechies.require.dm.dao;

import com.thoongatechies.require.dm.entity.DMEvent;
import com.thoongatechies.require.dm.entity.DMEventInstance;

import java.util.Collection;
import java.util.List;

/**
 * Created by mages_000 on 6/2/2016.
 */
public interface EventDao {

    void newEvent(DMEvent evt);
    void newEventInstance(DMEventInstance ei);

    void setProcessed(DMEvent evt);
    void setProcessed(DMEventInstance ei);

    List<DMEvent> eventToBeProcessed();
    List<DMEventInstance> instancesToBeProcessed();

    DMEvent eventById(long id);
    DMEventInstance instanceById(String id);

    List<DMEvent> eventByExtRef(String extRef);
    List<DMEvent> minimalEventDataByExtRefs(Collection<String> extRefs);

}
