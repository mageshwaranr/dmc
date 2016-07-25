package com.thoongatechies.dmc.spec.core.dsl;

import com.thoongatechies.dmc.spec.core.Scoper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mages_000 on 3/26/2016.
 */
public class ScoperImpl implements Scoper{

   private final Map<String,String> eventScopes;
    private final String defaultVal;

    public ScoperImpl(String defaultVal) {
        this(new HashMap<>(), defaultVal);
    }

    public ScoperImpl(Map<String, String> eventScopes, String defaultVal) {
        this.eventScopes = eventScopes;
        this.defaultVal = defaultVal;
    }

    @Override
    public String scopeOf(String evtName) {
        return eventScopes.getOrDefault(evtName,defaultVal);
    }

    @Override
    public boolean hasRetainLatestForAny() {
        return eventScopes.containsValue(RETAIN_LATEST);
    }
}
