package org.example;

public class Controller {
    private User angemeldeterUser;

    public Termin[][] getTermineDerWoche() {
        return termineDerWoche;
    }

    private Termin[][] termineDerWoche;
    private Gerät[] geraeteListe;
    private Gerät selectedGerät;

    public Controller(User angemeldeterUser, Gerät[] geraeteListe) {
        this.angemeldeterUser = angemeldeterUser;
        termineDerWoche = new Termin[7][20];
        this.geraeteListe = geraeteListe;
        selectedGerät = null;
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
