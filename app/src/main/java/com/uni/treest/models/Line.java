package com.uni.treest.models;

public class Line {
    private Terminus terminus1, terminus2;

    public Line(Terminus terminus1, Terminus terminus2) {
        this.terminus1 = terminus1;
        this.terminus2 = terminus2;
    }

    public String getLineName1(){
        return "Direzione " + terminus1.getSname();
    }

    public String getLines(){
        return terminus1.getSname() + " - " + terminus2.getSname();
    }

    public String getLineName2(){
        return "Direzione " + terminus2.getSname();
    }

    public Terminus getTerminus1() {
        return terminus1;
    }

    public void setTerminus1(Terminus terminus1) {
        this.terminus1 = terminus1;
    }

    public Terminus getTerminus2() {
        return terminus2;
    }

    public void setTerminus2(Terminus terminus2) {
        this.terminus2 = terminus2;
    }
}
