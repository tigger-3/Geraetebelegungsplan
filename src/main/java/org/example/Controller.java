package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.*;

public class Controller {
    public Calendar getCalendar() {
        return calendar;
    }

    private Calendar calendar = GregorianCalendar.getInstance();

    public User getAngemeldeterUser() {
        return angemeldeterUser;
    }

    private User angemeldeterUser;

    public Termin[][] getTermineDerWoche() {
        return termineDerWoche;
    }

    private Termin[][] termineDerWoche;

    public List<Gerät> getGeraeteListe() {
        return geraeteListe;
    }

    private List<Gerät> geraeteListe;

    public Gerät getSelectedGerät() {
        return selectedGerät;
    }

    private Gerät selectedGerät;
    private OracleDB dbController;

    public Controller() {
        calendar.set(calendar.DAY_OF_WEEK, calendar.MONDAY);
        calendar.set(calendar.HOUR_OF_DAY,0);
        calendar.set(calendar.MINUTE,0);
        calendar.set(calendar.SECOND,0);
        calendar.set(calendar.MILLISECOND,0);
        this.angemeldeterUser = null;
        termineDerWoche = new Termin[7][20];
        this.geraeteListe = new ArrayList<Gerät>();
        selectedGerät = null;
        dbController = new OracleDB("SUS_FS191_eschulte", "emily", "oracle.s-atiw.de", "1521", "atiwora");

        try {
            dbController.connect();
        }catch(Exception e){
            e.printStackTrace();
        }

        //TODO: get data from database
        // geräteListe füllen
        // selectedGerät auswählen
        // get Termine (bitte in eigener methode, damit bei neuaswahl des Gerätes update möglich ist
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
    } //todo mock

    public void kalenderExportieren(){ //TODO - hinten angestellt

    }

}
