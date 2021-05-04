package org.example;

import java.util.Date;
import java.time.*;


public class Termin {
    Date Datum;
    Gerät AusgewähltesGerät;
    Instant Uhrzeit;
    Duration Dauer;
    User Benutzer;

    public Termin(Date datum, Gerät ausgewähltesGerät, Instant uhrzeit, User benutzer) {
        Datum = datum;
        AusgewähltesGerät = ausgewähltesGerät;
        Uhrzeit = uhrzeit;
        Benutzer = benutzer;
    }
}
