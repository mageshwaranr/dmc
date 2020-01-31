package com.thoongatechies.dmc.core.vo;

import com.thoongatechies.dmc.core.entity.Sender;
import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Data
public class CallbackRequest {

    private Sender sender;
    private RuleDefinition rule;
    private Set<Event> events;
    private Map<String,Object> data, qualifier;
}
