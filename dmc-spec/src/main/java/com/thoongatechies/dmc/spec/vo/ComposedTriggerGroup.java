package com.thoongatechies.dmc.spec.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mageshwaranr on 7/24/2016.
 */
public class ComposedTriggerGroup  extends TriggerGroup {

    private TriggerGroup globalGroup, TriggerGroup;

    public ComposedTriggerGroup(TriggerGroup globalGroup, TriggerGroup TriggerGroup) {
        this.globalGroup = globalGroup;
        this.TriggerGroup = TriggerGroup;
    }

    @Override
    public boolean hasEvent(String eventName) {
        return globalGroup.hasEvent(eventName) || TriggerGroup.hasEvent(eventName);
    }

    @Override
    public Map<String, Object> qualifier(String eventName) {
        return globalGroup.qualifier(eventName).isEmpty() ? TriggerGroup.qualifier(eventName) : globalGroup.qualifier(eventName);
    }

    @Override
    public Map<String, Trigger> getEventsByName() {
        Map<String,Trigger> events = new HashMap<>();
        events.putAll(globalGroup.getEventsByName());
        events.putAll(TriggerGroup.getEventsByName());
        return events;
    }

    @Override
    public Map<String,Object> getGroupedData() {
        return TriggerGroup.getGroupedData();
    }
}
