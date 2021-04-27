package org.example;

public class Controller {
    private User angemeldeterUser;

    public Termin[][] getTermineDerWoche() {
        return termineDerWoche;
    }

    private Termin[][] termineDerWoche;
    private Gerät[] geraeteListe;
    private Gerät selectedGerät;

    public Controller(User angemeldeterUser) {
        this.angemeldeterUser = angemeldeterUser;
        termineDerWoche = new Termin[7][20];
        this.geraeteListe = new Gerät[0];
        selectedGerät = null;
    }

    public boolean isTerminFromCurrentUser(Termin termin){
        return termin.Benutzer.Kundenummer == this.angemeldeterUser.Kundenummer;
    }

    public void selectGerät(Gerät newSelection){
        selectedGerät = newSelection; //TODO update termine
    }

    public void terminBuche(){

    }

    public void terminStornieren(){

    }

    public User momentanerUser(){
        return angemeldeterUser;
    }

    public void kalenderExportieren(){

    }

}
