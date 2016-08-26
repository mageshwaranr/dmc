package com.thoongatechies.require.dm.vo;

/**
 * Created by mages_000 on 6/1/2016.
 */
public class Link {

    private String src, tgt, name;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getTgt() {
        return tgt;
    }

    public void setTgt(String tgt) {
        this.tgt = tgt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Link from(Flow src, Flow tgt){
        Link link = new Link();
        link.setSrc(src.getId());
        link.setTgt(tgt.getId());
        link.setName(src.getName() + "->"+tgt.getName());
        return link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        if (src != null ? !src.equals(link.src) : link.src != null) return false;
        return !(tgt != null ? !tgt.equals(link.tgt) : link.tgt != null);

    }

    @Override
    public int hashCode() {
        int result = src != null ? src.hashCode() : 0;
        result = 31 * result + (tgt != null ? tgt.hashCode() : 0);
        return result;
    }
}
