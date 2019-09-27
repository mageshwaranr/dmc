package com.thoongatechies.dmc.core.entity;

import lombok.Data;

import java.util.Map;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Data
public class CallbackDefinitionEntity {

    private String name, url, status, transformation;

    private Map<String,Object> data;

}
