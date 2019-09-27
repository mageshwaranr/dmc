package com.thoongatechies.dmc.core.entity;

import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Data
public class CallbackDataViewEntity {

    private String id, status, createdBy , lastUpdatedBy;

    private Collection<EventEntity> events;
    private Map<String,Object> qualifier, data;
    private RuleDefinitionEntity rule;
    private CallbackDefinitionEntity callback;
    private Date createdOn, lastUpdatedOn;
    private Sender sender;
    private Integer errorCode;


}
