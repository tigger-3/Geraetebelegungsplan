package org.example;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PrimaryController {

    @FXML
    private TextField kundennr_eingabe;

    @FXML
    private void checkUser() throws IOException {
        ArrayList<User> temp = App.mainController.getUserListe();
        String kundennr = kundennr_eingabe.getText();
        for (User u: temp
             ) {
            if(u.kundenummer.equals(kundennr)){
                App.mainController.setAngemeldeterUser(u);
                App.setRoot("secondary");
            }
        }
    }
}
