package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    public User getAngemeldeterUser() {
        return angemeldeterUser;
    }

    private User angemeldeterUser;

    public Termin[][] getTermineDerWoche() {
        return termineDerWoche;
    }

    private Termin[][] termineDerWoche;
    private Gerät[] geraeteListe;

    public Gerät getSelectedGerät() {
        return selectedGerät;
    }

    private Gerät selectedGerät;
    private OracleDB dbController;

    public Controller() {
        this.angemeldeterUser = null;
        termineDerWoche = new Termin[7][20];
        this.geraeteListe = new Gerät[0];
        selectedGerät = null;
        dbController = new OracleDB("SUS_FS191_master", "m", "oracle.s-atiw.de", "1521", "atiwora");

        try {
            dbController.connect();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void close(){
        dbController.disconnect();
    }

    public boolean isTerminFromCurrentUser(Termin termin){
        if(angemeldeterUser != null) {
            return termin.Benutzer.Kundenummer == this.angemeldeterUser.Kundenummer;
        }
        else{
            return true; //mock
        }
    }

    public void selectGerät(Gerät newSelection){
        selectedGerät = newSelection; //TODO update termine
    }

    public void terminBuchen(Termin termin, int tag, int zeit){
        termineDerWoche[tag][zeit]=termin; //todo übertragung an DB
    }

    public void terminStornieren(Termin termin){
        for(int tag = 0; tag < termineDerWoche.length; tag++){
            for(int zeit=0; zeit < termineDerWoche[tag].length; zeit++){
                if(termineDerWoche[tag][zeit]==termin){
                    termineDerWoche[tag][zeit]=null; //todo übertragung an DB
                }
            }
        }
    }

    public User momentanerUser(){
        return angemeldeterUser;
    }

    public void kalenderExportieren(){

    }

}
