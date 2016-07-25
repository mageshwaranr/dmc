package com.thoongatechies.dmc.spec.core.dsl;

import com.thoongatechies.dmc.spec.vo.Trigger;

import java.util.function.Predicate;

/**
 * Created by mages_000 on 5/27/2016.
 */
public class EventSelectors {

    public static Predicate<Trigger> hasEventName(String name){
        return mutableEvent -> name.contains(mutableEvent.getName());
    }

    public static Predicate<Trigger> havingKeyValue(String key, String value){
        return mutableEvent -> value.equals(mutableEvent.getQualifier().get(key));
    }

}
