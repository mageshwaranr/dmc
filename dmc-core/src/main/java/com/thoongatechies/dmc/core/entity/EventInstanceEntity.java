package com.thoongatechies.dmc.core.entity;

import lombok.Data;

import java.util.Date;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Data
public class EventInstanceEntity {

    public static final String STATUS_COLUMN = "status",ID_COLUMN="_id";

    private String id;

    private long eventId;

    private String ruleId, status, createdBy, lastUpdatedBy;

    private Date createdOn, lastUpdatedOn;

}
