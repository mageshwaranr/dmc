package com.thoongatechies.dmc.spec.def.dsl.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mages_000 on 3/26/2016.
 */
public class VariableSpec {

    public enum DataSourceType {
        HEADERS,
        CONSTANT,
        QUALIFIER;
    }

    private String name, format, sourceKey, constantVal, serviceUrl;
    private DataSourceType sourceType;
    private Map<String, String> params = new HashMap<>();

    /*glorified mutators*/

    public void setHeaderSource() {
        this.sourceType = DataSourceType.HEADERS;
    }
//    public void setIngestSource() {
//        this.sourceType = DataSourceType.INGEST;
//    }
    public void setQualifierSource() {
        this.sourceType = DataSourceType.QUALIFIER;
    }
//    public void setRestSource() {
//        this.sourceType = DataSourceType.REST;
//    }
    public void setConstant() {
        this.sourceType = DataSourceType.CONSTANT;
    }
    public boolean isHeaderSource() {
        return this.sourceType == DataSourceType.HEADERS;
    }
//    public boolean isIngestSource() {
//        return this.sourceType == DataSourceType.INGEST;
//    }
    public boolean isQualifierSource() {
        return this.sourceType == DataSourceType.QUALIFIER;
    }
//    public boolean isRestSource() {
//        return this.sourceType == DataSourceType.REST;
//    }
    public boolean isConstant() {
        return this.sourceType == DataSourceType.HEADERS;
    }
    public void addParam(String var, String val) {
        params.put(var, val);
    }

    /*simple mutators*/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getConstantVal() {
        return constantVal;
    }

    public void setConstantVal(String constantVal) {
        this.constantVal = constantVal;
    }

//    public String getServiceUrl() {
//        return serviceUrl;
//    }

//    public void setServiceUrl(String serviceUrl) {
//        this.serviceUrl = serviceUrl;
//    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

//    public Map<String, String> getParams() {
//        return params;
//    }
}
