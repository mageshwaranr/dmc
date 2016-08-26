package com.thoongatechies.dmc.spec.def.dsl;

import com.thoongatechies.dmc.spec.def.QualifierFinder;
import com.thoongatechies.dmc.spec.def.Spec;
import com.thoongatechies.dmc.spec.def.dsl.model.VariableSpec;
import com.thoongatechies.dmc.spec.vo.Trigger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mages_000 on 3/27/2016.
 */
public class QualifierFinderImpl implements QualifierFinder {
    private final Map<String, Collection<VariableSpec>> determiners;
    private DataConversionUtils conversionUtils;
//    public QualifierFinderImpl(Map<String, Collection<VariableSpec>> determiners, RestTemplate template) {
//        this.determiners = determiners;
//        this.conversionUtils = new DataConversionUtils(template);
//    }

    public QualifierFinderImpl(Map<String, Collection<VariableSpec>> determiners) {
        this.determiners = determiners;
        this.conversionUtils = new DataConversionUtils();
    }

    @Override
    public Map<String,Object>  findQualifier(Trigger evt, Spec spec) {
        Map<String,Object> qualifier = new HashMap<>();
        determiners
                .getOrDefault(evt.getName(), Collections.<VariableSpec>emptyList())
                .stream()
//                .filter(variableSpec -> !variableSpec.isRestSource())
                .forEach( variableSpec -> {
                    if(variableSpec.isHeaderSource()){
                        conversionUtils.populateFromHeaders(evt,qualifier,variableSpec);
                    } else {
                        qualifier.put(variableSpec.getName(), variableSpec.getConstantVal());
                    }
                });

//        determiners.get(evt.getName()).forEach(variableSpec -> {
//            if(variableSpec.isRestSource()){
//                conversionUtils.populateFromRest(evt,qualifier,variableSpec,spec);
//            }
//        });
        return qualifier;
    }
}
