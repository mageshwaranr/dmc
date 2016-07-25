package com.thoongatechies.dmc.spec.core;

import com.thoongatechies.dmc.spec.vo.Trigger;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by mages_000 on 3/26/2016.
 */
public interface TriggerGrouper extends Serializable, Function<Trigger, Map<String,Object>> {
}
