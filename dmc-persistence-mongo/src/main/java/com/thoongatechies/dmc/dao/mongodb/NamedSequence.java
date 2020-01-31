package com.thoongatechies.dmc.dao.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by mages_000 on 6/2/2016.
 */
@Document
public class NamedSequence {

    @Id
    private String id;

    private long seq;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    @Override
    public String toString() {
        return "NamedSequence{" +
                "id='" + id + '\'' +
                ", seq=" + seq +
                '}';
    }
}
