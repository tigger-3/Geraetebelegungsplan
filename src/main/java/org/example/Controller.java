package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class Controller {
    public Calendar getCalendar() {
        return calendar;
    }

    private final Calendar calendar = GregorianCalendar.getInstance();

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

    private final Termin[][] termineDerWoche;

    public List<Geraet> getGeraeteListe() {
        return geraeteListe;
    }

    private final List<Geraet> geraeteListe;

    public Geraet getSelectedGeraet() {
        return selectedGeraet;
    }

    public ArrayList<User> getUserListe(){
        ArrayList<User> userListe = new ArrayList<>();
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

    private Geraet selectedGeraet;
    private final OracleDB dbController;

    public Controller() {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        //calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        this.angemeldeterUser = null;
        termineDerWoche = new Termin[7][20];
        this.geraeteListe = new ArrayList<>();
        selectedGeraet = null;
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
               this.geraeteListe.add(new Geraet(rs.getString("geraetename"), rs.getString("geraete_id")));
           }
            for (Geraet g: geraeteListe
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
        if(getSelectedGeraet()!=null){
            getTermine();
        }
    }
    public void lastWeek(){
        calendar.add(Calendar.WEEK_OF_MONTH,-1);
        clearTermine();
        if(getSelectedGeraet()!=null){
            getTermine();
        }
    }
    private void clearTermine(){
        for (Termin[] termins : termineDerWoche) {
            Arrays.fill(termins, null);
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
                            "WHERE geraete_id = " + selectedGeraet.geraeteID);

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
                Termin t = new Termin(datum, selectedGeraet,uhrzeit,benutzer);
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
                                        //noinspection IfStatementWithIdenticalBranches
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
            return termin.benutzer.kundenummer.equals(this.angemeldeterUser.kundenummer);
        }
        else{
            return false; //if no user is logged in, he has not booked anything
        }
    }

    public void selectGeraet(Geraet newSelection){
        selectedGeraet = newSelection; //TODO update termine
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

    @SuppressWarnings("StringConcatenationInLoop")
    public void kalenderExportieren(){ //Kalenderexport nach RFC5545 - iCalendar
        String kalenderString =
            "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            //ProdID not used - "PRODID:http://www.example.com/calendarapplication/\n" +
            "METHOD:PUBLISH\n"
            //*
             +
            "BEGIN:VTIMEZONE\n" +
            "TZID:Europe/Berlin\n" +
            "BEGIN:STANDARD\n" +
            "DTSTART:16011028T030000\n" +
            "RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=10\n" +
            "TZOFFSETFROM:+0200\n" +
            "TZOFFSETTO:+0100\n" +
            "END:STANDARD\n" +
            "BEGIN:DAYLIGHT\n" +
            "DTSTART:16010325T020000\n" +
            "RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=3\n" +
            "TZOFFSETFROM:+0100\n" +
            "TZOFFSETTO:+0200\n" +
            "END:DAYLIGHT\n" +
            "END:VTIMEZONE\n"//*/
            ;
        //end of starting block
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        boolean termineVorhanden = false;
        for (Termin[] termineDesTages:termineDerWoche) {
            for (Termin termin:termineDesTages) {
                if(termin!=null && isTerminFromCurrentUser(termin)) {
                    kalenderString +=
                        "BEGIN:VEVENT\n" +
                        "UID:" + dateFormat.format(Date.from(termin.uhrzeit)) + "Z" + prolongenStringTo(angemeldeterUser.kundenummer,10) + "@meinfitnessstudio.com\n" +
                        //Organizer stays ignored "ORGANIZER:" + "\n" +
                        "SUMMARY:" + "Training auf " + termin.ausgewaehltesGeraet.geraetename + "\n" +
                        "DESCRIPTION:" + "Sie haben ein Training auf dem Gerät \"" + termin.ausgewaehltesGeraet.geraetename + "(" + termin.ausgewaehltesGeraet.geraeteID + ")\" gebucht." + "\n" +
                        "CLASS:PUBLIC\n" +
                        "DTSTART;TZID=Europe/Berlin:" + dateFormat.format(Date.from(termin.uhrzeit)) + "\n" +
                        "DTEND;TZID=Europe/Berlin:" + dateFormat.format(Date.from(termin.endzeit)) + "\n" +
                        "DTSTAMP:" + dateFormat.format(Date.from(Instant.now())) + "Z\n" +
                        "END:VEVENT\n";
                    termineVorhanden = true;
                }
            }
        }
        //end of event block
        kalenderString+=
            "END:VCALENDAR";

        if(termineVorhanden) { // wenn keine Termine vorhanden sind, geht ein Import schief.
            File outputFile = new File("Export " + dateFormat.format(Date.from(Instant.now())) + ".ics");
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
                bw.write(kalenderString);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String prolongenStringTo(String string, int length){
        StringBuilder stringBuilder = new StringBuilder(string);
        while (stringBuilder.length()<length){
            stringBuilder.insert(0, "0");
        }
        string = stringBuilder.toString();
        return string;
    }

}
