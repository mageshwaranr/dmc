package com.thoongatechies.dmc.core.entity;

import com.thoongatechies.dmc.spec.vo.Trigger;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.Map;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Data
@ToString(exclude = {"createdBy","lastUpdatedBy","createdOn","lastUpdatedOn","occuredAt","data"},callSuper = true)
public class EventEntity extends Trigger {

    public static final String STATUS_COLUMN="status" , ID_COLUMN="_id", EXT_REF_COLUMN = "extRef";

    private String extRef, owner, status, createdBy, lastUpdatedBy;
    private Map<String,Object> data;
    private Date createdOn, lastUpdatedOn, occuredAt;

}
