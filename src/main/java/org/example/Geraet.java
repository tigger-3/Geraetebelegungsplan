package org.example;

public class Geraet {

    String geraetename;
    String geraeteID;

    public Geraet(String geraetename, String geraeteID) {
        this.geraetename = geraetename;
        this.geraeteID = geraeteID;
    }

    @Override
    public String toString() {
        return geraetename + " " + geraeteID + ",";
    }
}
