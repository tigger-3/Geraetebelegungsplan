package org.example;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.time.*;
import java.util.GregorianCalendar;


public class Termin {
    Date Datum;
    Gerät AusgewähltesGerät;
    Instant Uhrzeit;
    Instant Endzeit = null;
    User Benutzer;

    public Termin(Date datum, Gerät ausgewähltesGerät, Instant uhrzeit, User benutzer) {
        Datum = datum;
        AusgewähltesGerät = ausgewähltesGerät;
        Uhrzeit = uhrzeit;
        Benutzer = benutzer;
    }

    public void setEndzeit(Instant newEndzeit){
        this.Endzeit = newEndzeit;
    }

    public Instant getEndzeit(){
        if(Endzeit == null){
            Date start = Date.from(Uhrzeit);
            Calendar c = new GregorianCalendar();
            c.setTime(start);
            c.add(Calendar.MINUTE,30);
            return c.toInstant();
        }else{
            return Endzeit;
        }
    }
}
