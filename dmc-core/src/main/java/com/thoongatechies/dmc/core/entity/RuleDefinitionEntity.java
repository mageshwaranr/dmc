package com.thoongatechies.dmc.core.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Document(collection="RuleDefinition")
public class RuleDefinitionEntity {

    @Id
    private String id;

    private Long versionNo;

    @NotNull(message="expression can not be null")
    private String expression,name;

    private String status;
    @LastModifiedBy
    private String lastUpdatedBy;
    @LastModifiedDate
    private Date lastUpdatedOn;
    @CreatedBy
    private String createdBy;
    @CreatedDate
    private Date createdOn;

    private List<CallbackDefinitionEntity> callbacks;

    public Long getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(Date lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public List<CallbackDefinitionEntity> getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(List<CallbackDefinitionEntity> callbacks) {
        this.callbacks = callbacks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RuleDefinitionEntity that = (RuleDefinitionEntity) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(status != null ? !status.equals(that.status) : that.status != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
