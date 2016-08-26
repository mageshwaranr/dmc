package com.thoongatechies.require.dm.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Date;
import java.util.Map;

/**
 * Created by mages_000 on 6/1/2016.
 */
@JsonDeserialize(builder = Event.EventBuilder.class)
public class Event {

    private String name, id, owner, extRef, createdBy;
    private Map<String, Object> qualifier, data;
    private Date occuredAt, createdOn;

    public Event(String name, Map<String, Object> qualifier) {
        this.name = name;
        this.qualifier = qualifier;
    }

    public Event(String name, String id, String owner, String extRef, String createdBy, Map<String, Object> qualifier, Map<String, Object> data, Date occuredAt, Date createdOn) {
        this.name = name;
        this.id = id;
        this.owner = owner;
        this.extRef = extRef;
        this.createdBy = createdBy;
        this.qualifier = qualifier;
        this.data = data;
        this.occuredAt = occuredAt;
        this.createdOn = createdOn;
    }

    public static EventBuilder newBuilder() {
        return new EventBuilder();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getExtRef() {
        return extRef;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Map<String, Object> getQualifier() {
        return qualifier;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Date getOccuredAt() {
        return occuredAt;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", qualifier=" + qualifier +
                ", extRef='" + extRef + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    @JsonPOJOBuilder
    public static class EventBuilder {

        private String name;
        private Map<String, Object> qualifier;
        private String id;
        private String owner;
        private String extRef;
        private String createdBy;
        private Map<String, Object> data;
        private Date occuredAt;
        private Date createdOn;

        public EventBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public EventBuilder withQualifier(Map<String, Object> qualifier) {
            this.qualifier = qualifier;
            return this;
        }

        public EventBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public EventBuilder withOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public EventBuilder withExtRef(String extRef) {
            this.extRef = extRef;
            return this;
        }

        public EventBuilder withCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public EventBuilder withData(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        public EventBuilder withOccuredAt(Date occuredAt) {
            this.occuredAt = occuredAt;
            return this;
        }

        public EventBuilder withCreatedOn(Date createdOn) {
            this.createdOn = createdOn;
            return this;
        }

        public Event build() {
            Event event = new Event(name, qualifier);
            event.id = id;
            event.extRef = extRef;
            event.occuredAt = occuredAt;
            event.owner = owner;
            event.extRef = extRef;
            event.createdBy = createdBy;
            event.qualifier = qualifier;
            event.data = data;
            event.occuredAt = occuredAt;
            event.createdOn = createdOn;
            return event;
        }
    }
}
