package com.thoongatechies.require.dm.vo;

import com.thoongatechies.require.dm.entity.Sender;

import java.util.Map;
import java.util.Set;

/**
 * Created by mages_000 on 6/1/2016.
 */
public class CallbackRequest {

    private Sender sender;
    private RuleDefinition rule;
    private Set<Event> events;
    private Map<String,Object> data, qualifier;

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public RuleDefinition getRule() {
        return rule;
    }

    public void setRule(RuleDefinition rule) {
        this.rule = rule;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getQualifier() {
        return qualifier;
    }

    public void setQualifier(Map<String, Object> qualifier) {
        this.qualifier = qualifier;
    }
}
