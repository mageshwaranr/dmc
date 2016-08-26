package com.thoongatechies.require.dm.entity;

import com.thoongatechies.dmc.spec.vo.SpecExecutionState;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Document(collection="RuleInstanceState")
public class RuleInstanceStateEntity extends SpecExecutionState {
    public static final String ID_COLUMN = "_id";

    private String id, createdBy, lastUpdatedBy;
    private Date createdOn, lastUpdatedOn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(Date lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }
}
