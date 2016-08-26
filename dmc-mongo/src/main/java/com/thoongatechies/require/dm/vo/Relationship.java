package com.thoongatechies.require.dm.vo;

import java.util.Collection;

/**
 * Created by mages_000 on 6/1/2016.
 */
public class Relationship {

    private Collection<Flow> allFlows;

    private Collection<Link> links;

    public Collection<Link> getLinks() {
        return links;
    }

    public void setLinks(Collection<Link> links) {
        this.links = links;
    }

    public Collection<Flow> getAllFlows() {
        return allFlows;
    }

    public void setAllFlows(Collection<Flow> allFlows) {
        this.allFlows = allFlows;
    }
}
