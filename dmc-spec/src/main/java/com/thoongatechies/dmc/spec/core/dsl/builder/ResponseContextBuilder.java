package com.thoongatechies.dmc.spec.core.dsl.builder;

import com.thoongatechies.dmc.spec.core.dsl.ResponseContextCreatorImpl;
import com.thoongatechies.dmc.spec.core.dsl.model.VariableSpec;
import com.thoongatechies.dmc.spec.core.ResponseContextCreator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mages_000 on 5/27/2016.
 */
public class ResponseContextBuilder {

    private Map<String, Collection<VariableSpec>> contextPopulators = new HashMap<>();
    private VariableSpec lastSpec;

    public static ResponseContextBuilder newBuilder() {
        return new ResponseContextBuilder();
    }

    public ResponseContextBuilder set(String variableName) {
        lastSpec = new VariableSpec();
        lastSpec.setName(variableName);
        return this;
    }

    public ResponseContextBuilder asConstant(String value) {
        lastSpec.setConstant();
        lastSpec.setConstantVal(value);
        return this;
    }

    public ResponseContextBuilder fromHeader(String eventName, String key){
        lastSpec.setHeaderSource();
        lastSpec.setSourceKey(key);
        contextPopulators.putIfAbsent(eventName, new ArrayList<>());
        contextPopulators.get(eventName).add(lastSpec);
        return this;
    }

    public ResponseContextBuilder fromQualifier(String eventName, String key){
        lastSpec.setQualifierSource();
        lastSpec.setSourceKey(key);
        contextPopulators.putIfAbsent(eventName, new ArrayList<>());
        contextPopulators.get(eventName).add(lastSpec);
        return this;
    }

    public ResponseContextCreator build(){
        return new ResponseContextCreatorImpl(contextPopulators);
    }

}
