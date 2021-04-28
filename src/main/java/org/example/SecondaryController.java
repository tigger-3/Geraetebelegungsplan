package org.example;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;

public class SecondaryController {

    private Controller controller = new Controller(null);

    @FXML
    private GridPane bookingGrid;

    private Button[][] terminButtons = new Button[7][20];

    private void onButtonClick(int column, int row){
        terminButtons[column][row].setText("stornieren");
    }

    @FXML
    private void updateTermine(){
        for(int i = 0; i < terminButtons.length; i++){
            for(int j = 0; j < terminButtons[i].length; j++){
                bookingGrid.getChildren().remove(terminButtons[i][j]);
            }
        }
        Termin[][] termineDerWoche = controller.getTermineDerWoche();
        for(int i = 0; i < termineDerWoche.length; i++){
            for(int j = 0; j < termineDerWoche[i].length; j++){
                Button currentButton;
                if(termineDerWoche[i][j] == null) {
                    //termin frei
                    currentButton = new Button("buchen");
                    currentButton.setStyle("-fx-background-color: #7BB6F1");
                    DropShadow s = new DropShadow();
                    currentButton.setEffect(s);
                    int column = i;
                    int row = j;
                    currentButton.onActionProperty().setValue((event) -> onButtonClick(column, row));//TODO add listener
                }else{
                    //termin vorhanden
                    if(controller.isTerminFromCurrentUser(termineDerWoche[i][j])){
                        //termin von angemeldetem user
                        currentButton = new Button("stornieren");
                        currentButton.setStyle("-fx-background-color: #FD7B8A");
                    }
                    else{
                        //termin NICHT von angemeldetem user
                        currentButton = new Button("belegt");
                        currentButton.setDisable(true);
                        currentButton.setStyle("-fx-background-color: #99DFA1");
                    }
                }
                currentButton.prefHeight(33);
                currentButton.prefWidth(71);
                currentButton.setAlignment(Pos.CENTER);
                bookingGrid.add(currentButton,i+1,j+1);
                terminButtons[i][j] = currentButton;
            }
        }
    }

}