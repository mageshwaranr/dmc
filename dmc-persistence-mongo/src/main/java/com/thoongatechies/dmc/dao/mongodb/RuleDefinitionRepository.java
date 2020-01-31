package com.thoongatechies.dmc.dao.mongodb;

import com.thoongatechies.dmc.core.entity.RuleDefinitionEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by mages_000 on 6/1/2016.
 */
public interface RuleDefinitionRepository extends MongoRepository<RuleDefinitionEntity, String> {
    Pageable FETCH_LATEST_ONE_BY_VERSION_NO = new PageRequest(0, 1, Sort.Direction.DESC, "versionNo", "lastUpdatedOn");
    Sort SORT_BY_VERSION_NO = new Sort(Sort.Direction.DESC, "versionNo", "lastUpdatedOn");

    @Query("{'status' : ?0 }")
    List<RuleDefinitionEntity> ruleDefinitionByStatus(String status, Sort sort);

    @Query("{'name' : ?0 , 'expression' : ?1 }")
    List<RuleDefinitionEntity> ruleDefinitionByNameAndExpression(String name, String expression, Pageable pageable);

    @Query("{'name' : ?0 }")
    List<RuleDefinitionEntity> ruleDefinitionByName(String name, Pageable pageable);

    @Query("{'name' : ?0 , 'status' : ?1 }")
    List<RuleDefinitionEntity> ruleDefinitionByNameAndStatus(String name, String status, Pageable pageable);

    @Query("{'id' : ?0 }")
    List<RuleDefinitionEntity> ruleDefinitionById(String id, Pageable pageable);



}
