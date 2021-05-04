package org.example;

public class Gerät {

    String Gerätename;
    String GeräteID;

    public Gerät(String gerätename, String geräteID) {
        Gerätename = gerätename;
        GeräteID = geräteID;
    }

    @Override
    public String toString() {
        return Gerätename + " " + GeräteID + ",";
    }
}
