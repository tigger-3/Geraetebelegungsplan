package org.example;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class SecondaryController {

    @FXML
    public void initialize(){
        updateGeräteMenu();
    }

    @FXML
    private GridPane bookingGrid;
    @FXML
    private Menu geraeteMenu;

    private List<MenuItem> geräteMenuEinträge = new ArrayList<MenuItem>();

    @FXML
    private void updateGeräteMenu(){
        for(MenuItem eintrag: geräteMenuEinträge){
            geraeteMenu.getItems().remove(eintrag);
            geräteMenuEinträge.remove(eintrag);
        }

        for(Gerät gerät: App.controller.getGeraeteListe()){
            MenuItem eintrag = new MenuItem();
            eintrag.setText(gerät.Gerätename + " (" + gerät.GeräteID + ")");
            eintrag.onActionProperty().setValue((event) -> {
                App.controller.selectGerät(gerät);
                updateTermine();
            });
            geraeteMenu.getItems().add(eintrag);
            geräteMenuEinträge.add(eintrag);
        }
    }

    private Button[][] terminButtons = new Button[7][20];

    private void updateButton(Termin termin, int i, int j){
        Calendar c = App.controller.getCalendar();
        Date oldTime = c.getTime();
        c.add(c.DATE,i);
        c.add(c.HOUR_OF_DAY,(int)Math.floor(8+(j/2)));
        c.add(c.MINUTE,(j%2) * 30);
        Date datum = c.getTime();
        Instant uhrzeit = datum.toInstant();

        if(terminButtons[i][j]!=null){
            bookingGrid.getChildren().remove(terminButtons[i][j]);
        }
        Button currentButton;
        if(termin == null) {
            //termin frei
            currentButton = new Button("buchen");
            currentButton.setStyle("-fx-background-color: #7BB6F1");
            currentButton.onActionProperty().setValue((event) -> {
                Date tDatum = datum;
                Instant tUhrzeit = uhrzeit;
                Termin t = new Termin(tDatum, App.controller.getSelectedGerät(), tUhrzeit, App.controller.getAngemeldeterUser());
                App.controller.terminBuchen(t,i,j);
                updateButton(t,i,j);
            });
        }else{
            //termin vorhanden
            if(App.controller.isTerminFromCurrentUser(termin)){
                //termin von angemeldetem user
                currentButton = new Button("stornieren");
                currentButton.setStyle("-fx-background-color: #99DFA1");
                currentButton.onActionProperty().setValue((event) -> {
                    App.controller.terminStornieren(termin);
                    updateButton(null,i,j);
                });
            }
            else{
                //termin NICHT von angemeldetem user
                currentButton = new Button("belegt");
                currentButton.setStyle("-fx-background-color: #FD7B8A");
                currentButton.setDisable(true);
            }
        }
        currentButton.setPrefHeight(25);
        currentButton.setPrefWidth(71);
        currentButton.setAlignment(Pos.CENTER);
        bookingGrid.add(currentButton,i+1,j+1);
        terminButtons[i][j] = currentButton;

        c.setTime(oldTime);
    }

    @FXML
    private void updateTermine(){
        for(int i = 0; i < terminButtons.length; i++){
            for(int j = 0; j < terminButtons[i].length; j++){
                bookingGrid.getChildren().remove(terminButtons[i][j]);
            }
        }
        Termin[][] termineDerWoche = App.controller.getTermineDerWoche();
        for(int i = 0; i < termineDerWoche.length; i++){
            for(int j = 0; j < termineDerWoche[i].length; j++){
                updateButton(termineDerWoche[i][j],i,j);
            }
        }
    }

}