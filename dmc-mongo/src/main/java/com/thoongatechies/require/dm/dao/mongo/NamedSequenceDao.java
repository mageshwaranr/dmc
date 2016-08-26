package com.thoongatechies.require.dm.dao.mongo;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by mages_000 on 6/2/2016.
 */
@Named
public class NamedSequenceDao {

    String EVENT_SEQUENCE_NAME = "EventSequence", ID_COL ="id", SEQUENCE_COL = "seq";

    @Inject
    private MongoTemplate mongoTemplate;

    public long getNextSequence(String sequenceName){
        NamedSequence next = mongoTemplate.findAndModify(query(where(ID_COL).is(sequenceName)),
                new Update().inc(SEQUENCE_COL, 1),
                options().returnNew(true),
                NamedSequence.class);
        return next.getSeq();
    }

    public long getNextEventSequence(){
        return getNextSequence(EVENT_SEQUENCE_NAME);
    }

    public long createSequence(String name, long seq){
        NamedSequence sequence = new NamedSequence();
        sequence.setId(name);
        sequence.setSeq(seq);
        mongoTemplate.save(sequence);
        return sequence.getSeq();
    }

    @PostConstruct
    public void initializeSequence(){
        NamedSequence sequence = mongoTemplate.findOne(query(where(ID_COL).is(EVENT_SEQUENCE_NAME)),NamedSequence.class);
        if(sequence == null){
            createSequence(EVENT_SEQUENCE_NAME,1);
        }
    }
}
