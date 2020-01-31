package com.thoongatechies.dmc.core.entity;

import lombok.Data;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Data
public class Sender {

    private String name, uuid;

    public static Sender from(String name, String uuid){
        Sender sender = new Sender();
        sender.setName(name);
        sender.setUuid(uuid);
        return sender;
    }

}
