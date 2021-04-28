package org.example;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class SecondaryController {

    private Controller controller = App.getMainController();

    @FXML
    private GridPane bookingGrid;

    private Button[][] terminButtons = new Button[7][20];



    private void deleteButtons(){
        for(int i = 0; i < terminButtons.length; i++){
            for(int j = 0; j < terminButtons[i].length; j++){
                if(terminButtons[i][j]!=null){
                    bookingGrid.getChildren().remove(terminButtons[i][j]);
                }
            }
        }
    }
    private void updateButton(Termin termin, int i, int j){
        if(terminButtons[i][j]!=null){
            bookingGrid.getChildren().remove(terminButtons[i][j]);
        }
        Button currentButton;
        if(termin == null) {
            //termin frei
            currentButton = new Button("buchen");
            currentButton.setStyle("-fx-background-color: #7BB6F1");
            currentButton.onActionProperty().setValue((event) -> {
                //todo read date and time
                Instant uhrzeit = Instant.parse("2021-04-28T09:00:00.00Z"); //YYYY-MM-DDTHH:mm:ss.msZ
                Date datum = Date.from(uhrzeit);
                Termin t = new Termin(datum, App.getMainController().getSelectedGerät(), uhrzeit, App.getMainController().getAngemeldeterUser());
                App.getMainController().terminBuchen(t,i,j);
                updateButton(t,i,j);
            });
        }else{
            //termin vorhanden
            if(controller.isTerminFromCurrentUser(termin)){
                //termin von angemeldetem user
                currentButton = new Button("stornieren");
                currentButton.setStyle("-fx-background-color: #FD7B8A");
                currentButton.onActionProperty().setValue((event) -> {
                    App.getMainController().terminStornieren(termin);
                    updateButton(null,i,j);
                });
            }
            else{
                //termin NICHT von angemeldetem user
                currentButton = new Button("belegt");
                currentButton.setStyle("-fx-background-color: #99DFA1");
                currentButton.setDisable(true);
            }
        }
        currentButton.prefHeight(33);
        currentButton.prefWidth(71);
        currentButton.setAlignment(Pos.CENTER);
        bookingGrid.add(currentButton,i+1,j+1);
        terminButtons[i][j] = currentButton;
    }
    @FXML
    private void updateTermine(){
        deleteButtons();
        Termin[][] termineDerWoche = controller.getTermineDerWoche();
        for(int i = 0; i < termineDerWoche.length; i++){
            for(int j = 0; j < termineDerWoche[i].length; j++){
                updateButton(termineDerWoche[i][j],i,j);
            }
        }
    }

}