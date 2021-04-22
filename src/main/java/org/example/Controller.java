package org.example;

public class Controller {
    User AngemeldeterUser;
    Termin AktuellerTermin;
    Gerät[] GeräteListe;

    public Controller(User angemeldeterUser, Termin aktuellerTermin, Gerät[] geräteListe) {
        AngemeldeterUser = angemeldeterUser;
        AktuellerTermin = aktuellerTermin;
        GeräteListe = geräteListe;
    }

    public void TerminBuche(){

    }

    public void TerminStornieren(){

    }

    public User momentanerUser(){
        return null;
    }

    public void KalenderExportieren(){

    }

}
