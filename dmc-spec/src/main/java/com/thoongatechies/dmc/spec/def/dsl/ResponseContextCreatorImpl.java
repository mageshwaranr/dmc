package com.thoongatechies.dmc.spec.def.dsl;

import com.thoongatechies.dmc.spec.def.ResponseContextCreator;
import com.thoongatechies.dmc.spec.def.dsl.model.VariableSpec;
import com.thoongatechies.dmc.spec.vo.Trigger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;

/**
 * Created by mages_000 on 5/27/2016.
 */
public class ResponseContextCreatorImpl implements ResponseContextCreator {

    private DataConversionUtils conversionUtils;
    private Map<String, Collection<VariableSpec>> variableSpec;

    public ResponseContextCreatorImpl(Map<String, Collection<VariableSpec>> variableSpec) {
        this.conversionUtils = new DataConversionUtils();
        this.variableSpec = variableSpec;
    }

    @Override
    public Map<String, Object> apply(Collection<Trigger> mutableEvents) {
        Map<String, Object> outVar = new HashMap<>();
        mutableEvents.forEach(evt -> {
            Collection<VariableSpec> specs = variableSpec.getOrDefault(evt.getName(), emptyList());
            specs.forEach(spec -> {
                if (spec == null) return;
                if (spec.isHeaderSource()) {
                    conversionUtils.populateFromHeaders(evt, outVar, spec);
//                } else if (spec.isIngestSource()) {
//                    conversionUtils.populateFromIngest(evt, outVar, spec);
                } else if (spec.isQualifierSource()) {
                    conversionUtils.populateFromQualifier(evt, outVar, spec);
                } else if (spec.isConstant()) {
                    outVar.put(spec.getName(), spec.getConstantVal());
                }
            });
        });
        return outVar;
    }
}
