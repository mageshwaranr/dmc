package com.thoongatechies.dmc.spec.core.dsl.builder;


import com.thoongatechies.dmc.spec.core.TriggerGrouper;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Created by mages_000 on 3/26/2016.
 */
public class GroupBuilder {
    public Collection<String> fieldNames = Collections.emptyList();

    public static GroupBuilder newBuilder(){
        return new GroupBuilder();
    }

    public GroupBuilder groupBy(String... fieldNames){
        this.fieldNames = asList(fieldNames);
        return this;
    }

    public TriggerGrouper build(){
        return evt ->
          evt.getQualifier()
                  .entrySet()
                  .stream()
                  .filter(entry -> this.fieldNames.contains(entry.getKey()))
                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
