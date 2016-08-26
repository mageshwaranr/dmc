package com.thoongatechies.dmc.core.service;

import com.thoongatechies.require.dm.entity.RuleInstanceStateEntity;
import com.thoongatechies.require.dm.entity.Sender;

import java.util.List;

/**
 * Created by mages_000 on 6/1/2016.
 */
public interface DependencyInstanceService {

    Event registerNewEvent(Event event, Sender sender);

    List<Event> findEventByExternalRef(String externalRef);

    Event findEventById(String id);

    List<CallbackData> findCallbackDataByEventId(String eventId);
    List<CallbackData> findCallbackDataByEventExtRef(String extRef);
    List<CallbackData> findCallbackDataByRuleId(String ruleId, int limit, int page);
    List<CallbackData> findCallbackDataByRuleName(String ruleName, int limit, int page);
    CallbackData findCallbackDataById(String id);
    CallbackData retryCallbackDataById(String id, String user);

    RuleInstanceStateEntity findCurrentState(String ruleId);

    Relationship findForwardFlow(String extRef);
    Relationship findBackwardFlow(String extRef);


}
