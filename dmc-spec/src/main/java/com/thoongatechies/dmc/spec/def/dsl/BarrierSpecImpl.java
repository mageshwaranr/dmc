package com.thoongatechies.dmc.spec.def.dsl;


import com.thoongatechies.dmc.spec.def.*;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import static java.util.Collections.emptyList;

/**
 * Created by mages_000 on 5/27/2016.
 */
public class BarrierSpecImpl implements Spec {

    private String userProvidedSpec, expression, name;
    private Serializable executable;
    private Selector eventSelector;
    private QualifierFinder qualifierFinder;
    private TriggerGrouper grouper;
    private Scoper scope;
    private Map<String, URL> urlBindingMap;
    private ResponseContextCreator responseContextCreator;
    private Collection<String> relatedEventNames = emptyList();

    @Override
    public String rawSpec() {
        return userProvidedSpec;
    }

    @Override
    public String generatedExpression() {
        return expression;
    }

    @Override
    public Serializable executable() {
        return executable;
    }

    @Override
    public Selector selector() {
        return eventSelector;
    }

    @Override
    public QualifierFinder qualifierFinder() {
        return qualifierFinder;
    }

    @Override
    public TriggerGrouper grouper() {
        return grouper;
    }

    @Override
    public Scoper scoper() {
        return scope;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public URL getURL(String key) {
        return urlBindingMap.get(key);
    }

    @Override
    public ResponseContextCreator responseCreator() {
        return responseContextCreator;
    }

    @Override
    public Collection<String> eventNames() {
        return relatedEventNames;
    }

    public void setUserProvidedSpec(String userProvidedSpec) {
        this.userProvidedSpec = userProvidedSpec;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExecutable(Serializable executable) {
        this.executable = executable;
    }

    public void setEventSelector(Selector eventSelector) {
        this.eventSelector = eventSelector;
    }

    public void setQualifierFinder(QualifierFinder qualifierFinder) {
        this.qualifierFinder = qualifierFinder;
    }

    public void setGrouper(TriggerGrouper grouper) {
        this.grouper = grouper;
    }

    public void setScope(Scoper scope) {
        this.scope = scope;
    }

    public void setUrlBindingMap(Map<String, URL> urlBindingMap) {
        this.urlBindingMap = urlBindingMap;
    }

    public void setResponseContextCreator(ResponseContextCreator responseContextCreator) {
        this.responseContextCreator = responseContextCreator;
    }

    public void setRelatedEventNames(Collection<String> relatedEventNames) {
        this.relatedEventNames = relatedEventNames;
    }
}
