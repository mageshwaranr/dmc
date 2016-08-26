package com.thoongatechies.require.dm.entity;

import com.thoongatechies.dmc.spec.vo.Trigger;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Document(collection="CallbackData")
public class CallbackDataEntity {

    public static final String ID_COLUMN="_id", STATUS_COLUMN="status", EVENTS_COLUMN="events",
            RULE_ID_COLUMN="rule._id", RULE_NAME_COLUMN = "rule.name", COLLECTION_NAME="CallbackData",
            SENDER_COLUMN="sender", UUID_COL = SENDER_COLUMN+".uuid", EXT_REF_COL = "events."+ EventEntity.EXT_REF_COLUMN;

    private String id, status, createdBy , lastUpdatedBy;

    private Collection<Trigger> events;
    private Map<String,Object> qualifier, data;
    private RuleDefinitionEntity rule;
    private CallbackDefinitionEntity callback;
    private Date createdOn, lastUpdatedOn;
    private Sender sender;
    private Integer errorCode;
    private String transformedPayload;

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

    public Collection<Trigger> getEvents() {
        return events;
    }

    public void setEvents(Collection<Trigger> events) {
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

    public String getTransformedPayload() {
        return transformedPayload;
    }

    public void setTransformedPayload(String transformedPayload) {
        this.transformedPayload = transformedPayload;
    }
}
