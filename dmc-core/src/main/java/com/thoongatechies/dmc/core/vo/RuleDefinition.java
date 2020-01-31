package com.thoongatechies.dmc.core.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * Created by mages_000 on 6/1/2016.
 */
@JsonDeserialize(builder = RuleDefinition.RuleBuilder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@EqualsAndHashCode
@ToString
public class RuleDefinition {

    private String name, expression, id, status, createdBy, lastUpdatedBy;
    private Date createdOn, lastUpdatedOn;
    private List<CallbackDefinition> callbacks;
    private long versionNo;

    public RuleDefinition(String name, String expression) {
        this.name = name;
        this.expression = expression;
    }

    public static RuleBuilder newBuilder() {return new RuleBuilder();}

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "with")
    public static class RuleBuilder {

        private String name;
        private String expression;
        private String id;
        private String status;
        private String createdBy;
        private String lastUpdatedBy;
        private Date createdOn;
        private Date lastUpdatedOn;
        private List<CallbackDefinition> callbacks;
        private long versionNo;

        public RuleBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public RuleBuilder withExpression(String expresssion) {
            this.expression = expresssion;
            return this;
        }

        public RuleBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public RuleBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public RuleBuilder withCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public RuleBuilder withLastUpdatedBy(String lastUpdatedBy) {
            this.lastUpdatedBy = lastUpdatedBy;
            return this;
        }

        public RuleBuilder withCreatedOn(Date createdOn) {
            this.createdOn = createdOn;
            return this;
        }

        public RuleBuilder withLastUpdatedOn(Date lastUpdatedOn) {
            this.lastUpdatedOn = lastUpdatedOn;
            return this;
        }

        public RuleBuilder withCallbacks(List<CallbackDefinition> callbacks) {
            this.callbacks = callbacks;
            return this;
        }

        public RuleBuilder withVersionNo(long versionNo) {
            this.versionNo = versionNo;
            return this;
        }

        public RuleDefinition build() {
            RuleDefinition rule = new RuleDefinition(name, expression);
            rule.id = id;
            rule.status = status;
            rule.versionNo = versionNo;
            rule.createdOn = createdOn;
            rule.createdBy = createdBy;
            rule.lastUpdatedBy = lastUpdatedBy;
            rule.lastUpdatedOn = lastUpdatedOn;
            rule.callbacks = callbacks;
            return rule;
        }
    }
}
