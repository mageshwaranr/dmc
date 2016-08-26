package com.thoongatechies.dmc.core.dao;

import com.thoongatechies.require.dm.entity.CallbackDataEntity;

import java.util.List;

/**
 * Created by mages_000 on 6/2/2016.
 */
public interface CallbackDataDao {

    CallbackDataEntity newCallbackDate(CallbackDataEntity data);
    void update(CallbackDataEntity data);
    CallbackDataEntity callbackById(String id);
    List<CallbackDataEntity> callbacksByEventId(Long eventId);
    List<CallbackDataEntity> callbacksByEventExtRef(String eventExtRef);
    List<CallbackDataEntity> callbacksByEventIds(List<Long> eventId);
    List<CallbackDataEntity> callbacksByUuids(List<String> uuids);
    List<CallbackDataEntity> callbacksByRuleId(String ruleId, int limit, int page);
    List<CallbackDataEntity> callbacksByRuleName(String ruleName, int limit, int page);
    List<CallbackDataEntity> callbacksToBeProcessed();
}
