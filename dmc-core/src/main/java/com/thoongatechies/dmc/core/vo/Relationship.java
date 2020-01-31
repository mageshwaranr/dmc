package com.thoongatechies.dmc.core.vo;

import lombok.Data;

import java.util.Collection;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Data
public class Relationship {

    private Collection<Flow> allFlows;

    private Collection<Link> links;

}
