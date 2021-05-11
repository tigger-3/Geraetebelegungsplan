package org.example;

import java.util.Calendar;
import java.util.Date;
import java.time.*;
import java.util.GregorianCalendar;


public class Termin {
    Date datum;
    Geraet ausgewaehltesGeraet;
    Instant uhrzeit;
    Instant endzeit = null;
    User benutzer;

    public Termin(Date datum, Geraet ausgewaehltesGeraet, Instant uhrzeit, User benutzer) {
        this.datum = datum;
        this.ausgewaehltesGeraet = ausgewaehltesGeraet;
        this.uhrzeit = uhrzeit;
        this.benutzer = benutzer;
    }

    //Überflüssig wenn keine Modifizierer vorhanden
    public void setEndzeit(Instant newEndzeit){
        this.endzeit = newEndzeit;
    }

    public Instant getEndzeit(){
        if(endzeit == null){
            Date start = Date.from(uhrzeit);
            Calendar c = new GregorianCalendar();
            c.setTime(start);
            c.add(Calendar.MINUTE,30);
            return c.toInstant();
        }else{
            return endzeit;
        }
    }
}
