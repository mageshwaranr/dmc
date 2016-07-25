package com.thoongatechies.dmc.spec.vo;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by mageshwaranr on 7/24/2016.
 */
public class Trigger {

    public  static final String NAME_COLUMN = "name", QUALIFIER_COLUMN = "qualifier.", HEADER_COLUMN="headers.";

    private long id;
    private String name;
    private Map<String,Object> qualifier = new HashMap<>(), headers;
    private Map<String, List<String>> partitions;

    /**Mutators*/
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getQualifier() {
        return qualifier;
    }

    public void setQualifier(Map<String, Object> qualifier) {
        this.qualifier = qualifier;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public Map<String, List<String>> getPartitions() {
        return partitions;
    }

    public void setPartitions(Map<String, List<String>> partitions) {
        this.partitions = partitions;
    }


    /** equals & hashcode based on name and qualifier*/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trigger that = (Trigger) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MutableEvent{" +
                "qualifier=" + qualifier +
                ", name='" + name + '\'' +
                '}';
    }
}
