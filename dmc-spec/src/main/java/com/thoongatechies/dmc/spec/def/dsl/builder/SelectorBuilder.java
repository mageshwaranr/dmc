package com.thoongatechies.dmc.spec.def.dsl.builder;


import com.thoongatechies.dmc.spec.def.Selector;
import com.thoongatechies.dmc.spec.vo.Trigger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by mages_000 on 3/26/2016.
 */
public class SelectorBuilder {
    private Map<String, Predicate<Trigger>> predicateByEvent = new HashMap<>();
    private Predicate<Trigger> alwaysTrue = e -> true;
    private SelectorBuilder() {
    }

    public static SelectorBuilder newBuilder() {
        return new SelectorBuilder();
    }
    public SelectorBuilder hasEventWithName(String evtName) {
        predicateByEvent.putIfAbsent(evtName,alwaysTrue);
        predicateByEvent.get(evtName).and(evt -> evt.getName().equals(evtName));
        return this;
    }

    public SelectorBuilder hasKeyValue(String evtName, String key, String value){
        predicateByEvent.putIfAbsent(evtName,alwaysTrue);
        predicateByEvent.get(evtName).and(evt -> value.equals(evt.getQualifier().get(key)));
        return this;
    }

    public Selector build(){
        return evt -> predicateByEvent.getOrDefault(evt.getName(), alwaysTrue).test(evt);
    }

}
