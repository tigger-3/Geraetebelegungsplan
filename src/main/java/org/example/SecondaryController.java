package org.example;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;

public class SecondaryController {

    @FXML
    public void initialize(){
        kalenderwocheNr.setText(String.valueOf(App.controller.getCalendar().get(Calendar.WEEK_OF_YEAR)));
        updateGeraeteMenu();
    }

    @FXML
    private GridPane bookingGrid;
    @FXML
    private Menu geraeteMenu;
    @FXML
    private Label kalenderwocheNr;

    private final List<MenuItem> geraeteMenuEintraege = new ArrayList<>();

    @FXML
    private void updateGeraeteMenu(){
        for(MenuItem eintrag: geraeteMenuEintraege){
            geraeteMenu.getItems().remove(eintrag);
            geraeteMenuEintraege.remove(eintrag);
        }

        for(Gerät geraet: App.controller.getGeraeteListe()){
            MenuItem eintrag = new MenuItem();
            eintrag.setText(geraet.Gerätename + " (" + geraet.GeräteID + ")");
            eintrag.onActionProperty().setValue((event) -> {
                App.controller.selectGeraet(geraet);
                updateTermine();
            });
            geraeteMenu.getItems().add(eintrag);
            geraeteMenuEintraege.add(eintrag);
        }
    }

    private final Button[][] terminButtons = new Button[7][20];

    private void updateButton(Termin termin, int i, int j){
        Calendar c = App.controller.getCalendar();
        Date oldTime = c.getTime();
        c.add(Calendar.DATE,i);
        c.add(Calendar.HOUR_OF_DAY,(int)Math.floor(8+((double)j/2)));
        c.add(Calendar.MINUTE,(j%2) * 30);
        //c.setTimeZone();
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
                Termin t = new Termin(datum, App.controller.getSelectedGeraet(), uhrzeit, App.controller.getAngemeldeterUser());
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
        for (Button[] terminButton : terminButtons) {
            for (Button button : terminButton) {
                bookingGrid.getChildren().remove(button);
            }
        }
        Termin[][] termineDerWoche = App.controller.getTermineDerWoche();
        for(int i = 0; i < termineDerWoche.length; i++){
            for(int j = 0; j < termineDerWoche[i].length; j++){
                updateButton(termineDerWoche[i][j],i,j);
            }
        }
    }

    @FXML
    private void abmelden(){
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            e.printStackTrace();
        }
        App.controller.setAngemeldeterUser(null);
    }

    @FXML
    private void wocheVor(){
        App.controller.nextWeek();
        kalenderwocheNr.setText(String.valueOf(App.controller.getCalendar().get(Calendar.WEEK_OF_YEAR)));
        if(App.controller.getSelectedGeraet()!=null){
            updateTermine();
        }
    }
    @FXML
    private void wocheZurueck(){
        App.controller.lastWeek();
        kalenderwocheNr.setText(String.valueOf(App.controller.getCalendar().get(Calendar.WEEK_OF_YEAR)));
        if(App.controller.getSelectedGeraet()!=null){
            updateTermine();
        }
    }
}