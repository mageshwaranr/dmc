package com.thoongatechies.dmc.core.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Data
public class RuleDefinitionEntity {

//    @Id
    private String id;

    private Long versionNo;

//    @NotNull(message="expression can not be null")
    private String expression,name;

    private String status;
//    @LastModifiedBy
    private String lastUpdatedBy;
//    @LastModifiedDate
    private Date lastUpdatedOn;
//    @CreatedBy
    private String createdBy;
//    @CreatedDate
    private Date createdOn;

    private List<CallbackDefinitionEntity> callbacks;

}
