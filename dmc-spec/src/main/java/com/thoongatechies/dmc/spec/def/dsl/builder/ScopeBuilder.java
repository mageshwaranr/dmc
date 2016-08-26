package com.thoongatechies.dmc.spec.def.dsl.builder;

import com.thoongatechies.dmc.spec.def.Scoper;
import com.thoongatechies.dmc.spec.def.dsl.ScoperImpl;

import java.util.HashMap;
import java.util.Map;

import static com.thoongatechies.dmc.spec.def.Scoper.RETAIN_LATEST;
import static com.thoongatechies.dmc.spec.def.Scoper.WAIT_FOR_SINGLE_MATCH;

/**
 * Created by mages_000 on 3/26/2016.
 */
public class ScopeBuilder {

    private Map<String,String> eventScopes = new HashMap<>();

    private ScopeBuilder(){}

    public static ScopeBuilder newBuilder(){
        return new ScopeBuilder();
    }

    public ScopeBuilder setExactOneMatchFor(String evtName){
        eventScopes.put(evtName, WAIT_FOR_SINGLE_MATCH);
        return this;
    }

    public ScopeBuilder setRetainLatestFor(String evtName){
        eventScopes.put(evtName, RETAIN_LATEST);
        return this;
    }

    public Scoper build(){
        return new ScoperImpl(eventScopes, WAIT_FOR_SINGLE_MATCH);
    }

}
