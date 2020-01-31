package com.thoongatechies.dmc.spec.vo;

import lombok.*;

import java.util.*;


/**
 * Created by mageshwaranr on 7/24/2016.
 * <p>
 * Triggers grouped by fields stored in groupedData.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class TriggerGroup {

    private Map<String, Trigger> eventsByName = new HashMap<>();
    private Map<String, List<Trigger>> allEventsByName = new HashMap<>();
    private static Trigger nullEvent = new Trigger();

    private Map<String, Object> groupedData, responseContext;

    private String id ;

    public TriggerGroup() {
        this(UUID.randomUUID().toString());
    }

    public TriggerGroup(String id) {
        this.id = id;
    }

    /*methods used in spec*/
    public void addEvent(Trigger event) {
        allEventsByName.putIfAbsent(event.getName(), new ArrayList<>());
        allEventsByName.get(event.getName()).add(event);
        eventsByName.put(event.getName(), event);
    }

    public boolean hasEvent(String eventName) {
        return allEventsByName.containsKey(eventName);
    }

    public Map<String, Object> qualifier(String eventName) {
        List<Trigger> events = allEventsByName.getOrDefault(eventName, Collections.emptyList());
        if (events.isEmpty())
            return Collections.emptyMap();
        else
            return events.get(events.size() - 1).getQualifier();
    }

    public long currentTime() {
        return System.currentTimeMillis();
    }

    public boolean isGlobalGroup() {
        return false;
    }

}
