package com.thoongatechies.dmc.core.vo;

import lombok.Data;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Data
public class Link {

    private String src, tgt, name;


    public static Link from(Flow src, Flow tgt){
        Link link = new Link();
        link.setSrc(src.getId());
        link.setTgt(tgt.getId());
        link.setName(src.getName() + "->"+tgt.getName());
        return link;
    }


}
