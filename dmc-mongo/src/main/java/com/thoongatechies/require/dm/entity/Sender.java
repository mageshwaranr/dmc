package com.thoongatechies.require.dm.entity;

/**
 * Created by mages_000 on 6/1/2016.
 */
public class Sender {

    private String name, uuid;

    public static Sender from(String name, String uuid){
        Sender sender = new Sender();
        sender.setName(name);
        sender.setUuid(uuid);
        return sender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
