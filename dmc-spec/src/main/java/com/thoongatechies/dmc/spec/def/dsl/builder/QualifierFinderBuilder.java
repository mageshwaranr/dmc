package com.thoongatechies.dmc.spec.def.dsl.builder;

import com.thoongatechies.dmc.spec.def.QualifierFinder;
import com.thoongatechies.dmc.spec.def.dsl.QualifierFinderImpl;
import com.thoongatechies.dmc.spec.def.dsl.model.VariableSpec;
//import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mages_000 on 3/26/2016.
 */
public class QualifierFinderBuilder {
    private Map<String, Collection<VariableSpec>> determiners = new HashMap<>();
    private String evtName, lastParam;
    private VariableSpec lastSpec;

    private QualifierFinderBuilder() {
    }

    public static QualifierFinderBuilder newBuilder() {
        return new QualifierFinderBuilder();
    }

    public QualifierFinderBuilder event(String name) {
        this.evtName = name;
        determiners.putIfAbsent(evtName, new ArrayList<>());
        return this;
    }

    public QualifierFinderBuilder set(String var) {
        lastSpec = new VariableSpec();
        determiners.getOrDefault(evtName, new ArrayList<>()).add(lastSpec);
        lastSpec.setName(var);
        return this;
    }

    public QualifierFinderBuilder fromHeader(String var){
        lastSpec.setHeaderSource();
        lastSpec.setSourceKey(var);
        return this;
    }

    public QualifierFinderBuilder asConstant(String val){
        lastSpec.setConstant();
        lastSpec.setConstantVal(val);
        return this;
    }

//    public QualifierFinderBuilder fromRest(String url, String key){
//        lastSpec.setRestSource();
//        lastSpec.setSourceKey(key);
//        lastSpec.setServiceUrl(url);
//        return this;
//    }
//
//    public QualifierFinderBuilder param(String param){
//        lastParam = param;
//        return this;
//    }
//
//    public QualifierFinderBuilder valueOf(String key){
//        if(lastParam != null){
//            lastSpec.addParam(lastParam,key);
//            lastParam = null;
//        }
//        return this;
//    }

    public QualifierFinderBuilder toDate(String format){
        lastSpec.setFormat(format);
        return this;
    }
//
//    public QualifierFinder build(RestTemplate template){
//        return new QualifierFinderImpl(determiners,template);
//    }

    public QualifierFinder build(){
        return new QualifierFinderImpl(determiners);
    }
}
