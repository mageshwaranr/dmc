package com.thoongatechies.dmc.core.entity;

import com.thoongatechies.dmc.spec.vo.Trigger;
import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Data
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

}
