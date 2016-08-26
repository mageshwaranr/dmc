package com.thoongatechies.dmc.spec.def;

import java.io.Serializable;

/**
 * Created by mages_000 on 3/26/2016.
 */
public interface Scoper extends Serializable{

    String WAIT_FOR_SINGLE_MATCH = "WAIT_FOR_SINGLE_MATCH", RETAIN_LATEST = "RETAIN_LATEST";

    String scopeOf(String evtName);

    boolean hasRetainLatestForAny();

    default boolean isRetainLatest(String evtName){
        return RETAIN_LATEST.equals(scopeOf(evtName));
    }

    default boolean isWaitForSingleMatch(String evtName){
        return WAIT_FOR_SINGLE_MATCH.equals(scopeOf(evtName));
    }
}
