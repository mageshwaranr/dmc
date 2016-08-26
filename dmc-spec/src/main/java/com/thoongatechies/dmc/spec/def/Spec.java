package com.thoongatechies.dmc.spec.def;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;

/**
 * Created by mages_000 on 3/26/2016.
 */
public interface Spec extends Serializable{

    String SPECIAL_VALUE_MATCH_ANY = "__MatchAny";

    String rawSpec();
    String generatedExpression();
    Serializable executable();
    Selector selector();
    QualifierFinder qualifierFinder();
    TriggerGrouper grouper();
    ResponseContextCreator responseCreator();
    Scoper scoper();
    String name();
    Collection<String> eventNames();
    URL getURL(String key);


}
