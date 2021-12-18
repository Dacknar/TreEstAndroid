package com.uni.treest.models;

public class Terminus {
    private String sname;
    private int did;

    public Terminus(String sname, int did) {
        this.sname = sname;
        this.did = did;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }
}
