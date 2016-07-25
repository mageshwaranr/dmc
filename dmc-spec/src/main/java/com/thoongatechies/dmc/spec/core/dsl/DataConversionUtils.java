package com.thoongatechies.dmc.spec.core.dsl;

import com.thoongatechies.dmc.spec.core.dsl.model.VariableSpec;
import com.thoongatechies.dmc.spec.vo.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by mages_000 on 5/21/2016.
 */
public class DataConversionUtils {

//    private RestTemplate template;

    private Logger log = LoggerFactory.getLogger(getClass());

//    DataConversionUtils(RestTemplate template) {
//        this.template = template;
//    }

    DataConversionUtils() {
    }

    void populateFromHeaders(Trigger evt, Map<String, Object> qualifier, VariableSpec spec) {
        populateFromKvs(qualifier, spec, evt.getHeaders());
    }

    void populateFromQualifier(Trigger evt, Map<String, Object> qualifier, VariableSpec spec) {
        populateFromKvs(qualifier, spec, evt.getQualifier());
    }

    private void populateFromKvs(Map<String, Object> qualifier, VariableSpec spec, Map<String, Object> kvs) {
        Object val = kvs.getOrDefault(spec.getSourceKey(), "__null__");
        if (spec.getFormat() != null) {
            try {
                Date parse = new SimpleDateFormat(spec.getFormat()).parse(val.toString());
                qualifier.put(spec.getName(), parse.getTime());
            } catch (ParseException ex) {
                log.warn("unable to parse {} as date in {} format", val, spec.getFormat());
            }
        } else {
            qualifier.put(spec.getName(), val);
        }
    }
//
//    void populateFromRest(Trigger evt, Map<String, Object> qualifier, VariableSpec spec, Spec barrierSpec) {
//        String serviceUrl = spec.getServiceUrl();
//        if (!isValidUrl(serviceUrl)) {
//            if (barrierSpec.getURL(serviceUrl) == null) {
//                log.error("Invalid URL {}", serviceUrl);
//                throw new RuntimeException("Invalid URL: " + serviceUrl);
//            }
//            serviceUrl = barrierSpec.getURL(serviceUrl).toString();
//        }
//        qualifier.put(spec.getName(), getFromResponse(serviceUrl, createParamMap(spec.getParams(), qualifier), spec.getSourceKey()));
//    }

//    private Object getFromResponse(String serviceUrl, Map<String, String> paramMap, String key) {
//        ResponseEntity<Map> response = template.getForEntity(serviceUrl, Map.class, paramMap);
//        if (response.getStatusCode() != HttpStatus.OK) {
//            String message = "Invalid Resposne: " + serviceUrl + ", Status: " + response.getStatusCode().value();
//            log.error(message);
//            throw new RuntimeException(message);
//        }
//        return response.getBody().get(key);
//    }
//
//    private Map<String, String> createParamMap(Map<String, String> paramMap, Map<String, Object> qualifier) {
//        Map<String, String> returnMap = new HashMap<>();
//        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
//            if (qualifier.get(entry.getKey()) != null) {
//                returnMap.put(entry.getKey(), qualifier.get(entry.getKey()).toString());
//            } else {
//                log.warn("param {} not available, proceeding with out setting any value");
//            }
//        }
//        return returnMap;
//    }
//
//    private boolean isValidUrl(String serviceUrl) {
//        return serviceUrl.startsWith("http://") || serviceUrl.startsWith("https://");
//    }
//
//    void populateFromIngest(Trigger evt, Map<String, Object> qualifier, VariableSpec spec) {
//        populateFromKvs(qualifier, spec, evt.getHeaders());
//    }


}
