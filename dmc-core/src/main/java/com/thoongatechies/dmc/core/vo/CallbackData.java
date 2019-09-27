package com.thoongatechies.dmc.core.vo;

import lombok.Data;

import java.util.Date;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Data
public class CallbackData {

    private String callbackName, status, id, uuid, payload, errorCode, lastUpdatedBy;
    private Date lastUpdatedOn;


}
