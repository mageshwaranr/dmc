package com.thoongatechies.require.dm.entity;

import java.util.Map;

/**
 * Created by mages_000 on 6/1/2016.
 */
public class CallbackDefinitionEntity {

    private String name, url, status, transformation;

    private Map<String,Object> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransformation() {
        return transformation;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallbackDefinitionEntity that = (CallbackDefinitionEntity) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(status != null ? !status.equals(that.status) : that.status != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DMCallbackDefinition{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
