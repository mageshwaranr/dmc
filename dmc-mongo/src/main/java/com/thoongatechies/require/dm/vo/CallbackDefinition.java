package com.thoongatechies.require.dm.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Map;

import static org.springframework.util.Assert.notNull;

/**
 * Created by mages_000 on 6/1/2016.
 */
@JsonDeserialize(builder = CallbackDefinition.CallbackBuilder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallbackDefinition {

    private String name, url, status, transformation;
    private Map<String,Object> data;

    public CallbackDefinition(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public CallbackDefinition(String name, String url, String status, String transformation, Map<String, Object> data) {
        this.name = name;
        this.url = url;
        this.status = status;
        this.transformation = transformation;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    public String getTransformation() {
        return transformation;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public static CallbackBuilder newBuilder() { return new CallbackBuilder();}

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "with")
    public static class CallbackBuilder {
        private String name;
        private String url;
        private String status;
        private String transformation;
        private Map<String, Object> data;

        public CallbackBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CallbackBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public CallbackBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public CallbackBuilder withTransformation(String transformation) {
            this.transformation = transformation;
            return this;
        }

        public CallbackBuilder withData(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        public CallbackDefinition build() {
            notNull(name,"name can't be null and should be unique with in a rule");
            notNull(url,"url can't be null ");
            CallbackDefinition callback = new CallbackDefinition(name, url);
            callback.status = status;
            callback.data = data;
            callback.transformation = transformation;
            return callback;
        }
    }

    @Override
    public String toString() {
        return "Callback{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
