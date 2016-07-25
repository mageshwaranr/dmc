package com.thoongatechies.dmc.spec.core.dsl;


import com.thoongatechies.dmc.spec.core.Selector;
import com.thoongatechies.dmc.spec.vo.Trigger;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by mages_000 on 5/27/2016.
 */
public class SelectorImpl implements Selector {

    final Map<String,Predicate<Trigger>> nameToPredicate;

    public SelectorImpl(Map<String, Predicate<Trigger>> nameToPredicate) {
        this.nameToPredicate = nameToPredicate;
    }

    @Override
    public boolean test(Trigger mutableEvent) {
        Predicate<Trigger> predicate = nameToPredicate.get(mutableEvent.getName());
        return predicate != null && predicate.test(mutableEvent);
    }
}
