package com.thoongatechies.dmc.core.entity;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by mages_000 on 6/1/2016.
 */
public class CallbackDataViewEntity {

    private String id, status, createdBy , lastUpdatedBy;

    private Collection<EventEntity> events;
    private Map<String,Object> qualifier, data;
    private RuleDefinitionEntity rule;
    private CallbackDefinitionEntity callback;
    private Date createdOn, lastUpdatedOn;
    private Sender sender;
    private Integer errorCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Collection<EventEntity> getEvents() {
        return events;
    }

    public void setEvents(Collection<EventEntity> events) {
        this.events = events;
    }

    public Map<String, Object> getQualifier() {
        return qualifier;
    }

    public void setQualifier(Map<String, Object> qualifier) {
        this.qualifier = qualifier;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public RuleDefinitionEntity getRule() {
        return rule;
    }

    public void setRule(RuleDefinitionEntity rule) {
        this.rule = rule;
    }

    public CallbackDefinitionEntity getCallback() {
        return callback;
    }

    public void setCallback(CallbackDefinitionEntity callback) {
        this.callback = callback;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(Date lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
