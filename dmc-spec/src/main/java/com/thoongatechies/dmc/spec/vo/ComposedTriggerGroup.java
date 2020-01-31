package com.thoongatechies.dmc.spec.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mageshwaranr on 7/24/2016.
 */
public class ComposedTriggerGroup  extends TriggerGroup {

    private TriggerGroup globalGroup, triggerGroup;

    public ComposedTriggerGroup(TriggerGroup globalGroup, TriggerGroup TriggerGroup) {
        this.globalGroup = globalGroup;
        this.triggerGroup = TriggerGroup;
    }

    @Override
    public boolean hasEvent(String eventName) {
        return globalGroup.hasEvent(eventName) || triggerGroup.hasEvent(eventName);
    }

    @Override
    public Map<String, Object> qualifier(String eventName) {
        return globalGroup.qualifier(eventName).isEmpty() ? triggerGroup.qualifier(eventName) : globalGroup.qualifier(eventName);
    }

    @Override
    public Map<String, Trigger> getEventsByName() {
        Map<String,Trigger> events = new HashMap<>();
        events.putAll(globalGroup.getEventsByName());
        events.putAll(triggerGroup.getEventsByName());
        return events;
    }

    @Override
    public Map<String,Object> getGroupedData() {
        return triggerGroup.getGroupedData();
    }
}
