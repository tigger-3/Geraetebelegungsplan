package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.*;

public class Controller {
    public Calendar getCalendar() {
        return calendar;
    }

    private Calendar calendar = GregorianCalendar.getInstance();

    public void setAngemeldeterUser(User neuerUser) {
        this.angemeldeterUser = neuerUser;
    }
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
        //calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
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
    }

    public void nextWeek(){
        calendar.add(Calendar.WEEK_OF_MONTH,1);
        clearTermine();
        if(getSelectedGerät()!=null){
            getTermine();
        }
    }
    public void lastWeek(){
        calendar.add(Calendar.WEEK_OF_MONTH,-1);
        clearTermine();
        if(getSelectedGerät()!=null){
            getTermine();
        }
    }
    private void clearTermine(){
        for(int i = 0; i < termineDerWoche.length; i++){
            for(int j=0; j<termineDerWoche[i].length;j++){
                termineDerWoche[i][j]=null;
            }
        }
    }

    private Date mergeDateAndTime(java.sql.Date date, java.sql.Time time){
        // Construct date and time objects
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateCal.setTime(date);
        Calendar timeCal = Calendar.getInstance();
        //timeCal.setTimeZone(TimeZone.getTimeZone("GMT"));
        timeCal.setTime(time);

        // Extract the time of the "time" object to the "date"
        dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));

        // Get the time value!
        return dateCal.getTime();
    }

    public void getTermine(){
        try{
            ResultSet terminResults = dbController.getList(
                    "SELECT * " +
                            "FROM nutzung " +
                            "JOIN kunde " +
                            "ON kunde.kunden_id = nutzung.kunden_id " +
                            "WHERE geraete_id = " + selectedGerät.GeräteID);

            while(terminResults.next()){
                java.sql.Date test = terminResults.getDate("DATUM");
                Time uhrzeitT = terminResults.getTime("ANFANG");
                Date datum = mergeDateAndTime(test,uhrzeitT);
                //datum.setTime(uhrzeitT.getTime());
                Date startzeit = mergeDateAndTime(test,uhrzeitT);
                Instant uhrzeit = startzeit.toInstant();
                User benutzer = new User(
                        terminResults.getString("VORNAME"),
                        terminResults.getString("NACHNAME"),
                        terminResults.getString("KUNDEN_ID")
                );
                Time endzeitT = terminResults.getTime("ENDE");

                Date enddatum = mergeDateAndTime(test,endzeitT);
                //enddatum.setTime(endzeitT.getTime());
                Instant endzeit = enddatum.toInstant();
                Termin t = new Termin(datum,selectedGerät,uhrzeit,benutzer);
                t.setEndzeit(endzeit);

                Date oldCalendar = calendar.getTime();
                try{
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
                                for(int j = -1; j < termineDerWoche[i].length && !eingetragen; j++){
                                    if(datum.compareTo(comparisonDate)<0){
                                        if(j==-1){
                                            eingetragen = true; // before opening time
                                        }
                                        else{
                                            termineDerWoche[i][j]=t;
                                            eingetragen=true;
                                        }
                                    }
                                    calendar.add(GregorianCalendar.MINUTE,30);
                                    comparisonDate = calendar.getTime();
                                }
                            }
                        }
                        calendar.add(GregorianCalendar.DAY_OF_WEEK, 1);
                        comparisonDate = calendar.getTime();
                    }
                }
                catch (IndexOutOfBoundsException e){
                    //ignore - die Datenbankgruppe hat mal wieder Daten eingefügt, mit denen wir nichts anfangen können
                }
                calendar.setTime(oldCalendar);
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
            return termin.Benutzer.Kundenummer.equals(this.angemeldeterUser.Kundenummer);
        }
        else{
            return false; //if no user is logged in, he has not booked anything
        }
    }

    public void selectGerät(Gerät newSelection){
        selectedGerät = newSelection; //TODO update termine
        getTermine();
    }

    public void terminBuchen(Termin termin, int tag, int zeit){
        termineDerWoche[tag][zeit]=termin; //todo übertragung an DB

        try {
            dbController.insertTermin(termin);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void terminStornieren(Termin termin){
        for(int tag = 0; tag < termineDerWoche.length; tag++){
            for(int zeit=0; zeit < termineDerWoche[tag].length; zeit++){
                if(termineDerWoche[tag][zeit]==termin){
                    termineDerWoche[tag][zeit]=null;
                    try {
                        dbController.deleteTermin(termin);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }
    }

    public User momentanerUser(){
        return angemeldeterUser;
    }

    public void kalenderExportieren(){ //TODO - hinten angestellt

    }

}
