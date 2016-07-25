package com.thoongatechies.dmc.spec.vo;

import java.util.*;


/**
 * Created by mageshwaranr on 7/24/2016.
 *
 * Triggers grouped by fields stored in groupedData.
 *
 *
 */
public class TriggerGroup {

    private Map<String, Trigger> eventsByName = new HashMap<>();
    private Map<String, List<Trigger>> allEventsByName = new HashMap<>();
    private static Trigger nullEvent = new Trigger();

    private Map<String, Object> groupedData, responseContext;


    /*methods used in spec*/
    public void addEvent(Trigger event) {
        allEventsByName.putIfAbsent(event.getName(), new ArrayList<>());
        allEventsByName.get(event.getName()).add(event);
        eventsByName.put(event.getName(), event);
    }

    public boolean hasEvent(String eventName) {
        return allEventsByName.containsKey(eventName);
    }

    public Map<String,Object> qualifier(String eventName){
        List<Trigger> events = allEventsByName.getOrDefault(eventName, Collections.emptyList());
        if(events.isEmpty())
            return Collections.emptyMap();
        else
            return events.get(events.size() -1).getQualifier();
    }

    public long currentTime(){
        return System.currentTimeMillis();
    }

    public boolean isGlobalGroup(){
        return false;
    }

    /*Getter and setter*/

    public Map<String, List<Trigger>> getAllEventsByName(){
        return allEventsByName;
    }

    public Map<String, Trigger> getEventsByName() {
        return eventsByName;
    }

    public void setEventsByName(Map<String, Trigger> eventsByName) {
        this.eventsByName = eventsByName;
    }

    public static Trigger getNullEvent() {
        return nullEvent;
    }

    public Map<String, Object> getGroupedData() {
        return groupedData;
    }

    public void setGroupedData(Map<String, Object> groupedData) {
        this.groupedData = groupedData;
    }

    public Map<String, Object> getResponseContext() {
        return responseContext;
    }

    public void setResponseContext(Map<String, Object> responseContext) {
        this.responseContext = responseContext;
    }

}
