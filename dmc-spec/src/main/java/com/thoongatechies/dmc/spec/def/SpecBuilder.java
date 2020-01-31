package com.thoongatechies.dmc.spec.def;

import com.thoongatechies.dmc.spec.def.dsl.builder.DslBuilder;

import java.net.URL;
import java.util.Map;

/**
 * Created by mages_000 on 3/26/2016.
 */
public interface SpecBuilder {

    Spec build();

    //event mutators
    SpecBuilder event(String evtName);
    SpecBuilder latest();
    SpecBuilder set(String variableName);
    SpecBuilder asAny();
    SpecBuilder asConstant(String value);
    SpecBuilder fromHeader(String var);
//    SpecBuilder fromRest(String serviceUrl, String var);
//    SpecBuilder param(String param);
//    SpecBuilder valueOf(String var);
    SpecBuilder toDate(String dateFormatter);

    //conditionals
    SpecBuilder is(String evtName, String fieldName);
    SpecBuilder eq(String evtName, String fieldName);
    SpecBuilder lt(String evtName, String fieldName);
    SpecBuilder gt(String evtName, String fieldName);
    SpecBuilder lte(String evtName, String fieldName);
    SpecBuilder gte(String evtName, String fieldName);
    SpecBuilder and();
    SpecBuilder or();
    SpecBuilder groupBy(String... fieldNames);

    //open & close bracket
    SpecBuilder openBracket();
    SpecBuilder closeBracket();

    //event expires
    SpecBuilder expiresInMin(int minutes);

    //response customizations
    SpecBuilder addToResponse(String var);
    SpecBuilder fromQualifier(String evtName, String val);
    SpecBuilder fromHeader(String evtName, String val);

    //actual spec
    SpecBuilder withUserProvidedSpec(String userProvidedSpec);
    SpecBuilder withUrlBindingMap(Map<String, URL> urlBindingMap);

    static SpecBuilder newBuilder(){
        return new DslBuilder();
    }
}
