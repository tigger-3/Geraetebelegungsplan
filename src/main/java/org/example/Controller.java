package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
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

    public ArrayList<User> getUserListe(){
        ArrayList<User> userListe = new ArrayList<User>();
        ResultSet rs;

        try {
            rs =  dbController.getList("SELECT * FROM KUNDE");
            while(rs.next()){
                userListe.add(new User(rs.getString("Vorname"), rs.getString("Nachname"), rs.getString("Kunden_ID")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userListe;
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

        ResultSet rs;

        try {
           rs =  dbController.getList("SELECT * FROM GERAET");
           while(rs.next()){
               this.geraeteListe.add(new Gerät(rs.getString("geraetename"), rs.getString("geraete_id")));
           }
            for (Gerät g: geraeteListe
                 ) {
                System.out.println(g);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //TODO: get data from database
        // geräteListe füllen
        // selectedGerät auswählen
        // get Termine (bitte in eigener methode, damit bei neuaswahl des Gerätes update möglich ist
    }

    public void getTermine(){
        try{
            ResultSet terminResults = dbController.getList("SELECT * FROM nutzung WHERE geraete_id == " + selectedGerät.GeräteID);

            while(terminResults.next()){
                Date datum = terminResults.getDate("DATUM");
                Time uhrzeitT = terminResults.getTime("ANFANG");
                datum.setTime(uhrzeitT.getTime());
                Instant uhrzeit = datum.toInstant();
                User benutzer = new User("NAME NOCH IRRELEVANT", "irrelevant", terminResults.getString("KUNDEN_ID"));
                Termin t = new Termin(datum,selectedGerät,uhrzeit,benutzer);

                try{
                    Date oldCalendar = calendar.getTime();
                    Date comparisonDate = calendar.getTime();
                    boolean eingetragen = false;
                    for(int i = -1; i < termineDerWoche.length && !eingetragen; i++){
                        if(datum.compareTo(comparisonDate) <= 0){
                            if(i==-1){
                                eingetragen=true; //before current week
                            }
                            else{
                                calendar.add(GregorianCalendar.DAY_OF_WEEK,-1);
                                calendar.set(GregorianCalendar.HOUR,8);
                                comparisonDate = calendar.getTime();
                                for(int j = -1; j < termineDerWoche[i].length; j++){
                                    if(datum.compareTo(comparisonDate)<0){
                                        if(i==-1){
                                            eingetragen = true; // before opening time
                                        }
                                        else{
                                            termineDerWoche[i][j]=t;
                                            eingetragen=true;
                                        }
                                    }
                                    calendar.add(GregorianCalendar.MINUTE,30);
                                }
                            }
                        }
                        calendar.add(GregorianCalendar.DAY_OF_WEEK, 1);
                        comparisonDate = calendar.getTime();
                    }
                    calendar.setTime(oldCalendar);
                }
                catch (IndexOutOfBoundsException e){
                    //ignore - die Datenbankgruppe hat mal wieder Daten eingefügt, mit denen wir nichts anfangen können
                }
            }
        } catch (SQLException e){
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
            return false; //if no user is logged in, he has not booked anything
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
