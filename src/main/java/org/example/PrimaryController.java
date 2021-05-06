package org.example;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PrimaryController {
    @FXML
    public void initialize(){
        if(App.mainController.isConnected()) {
            this.dbUserField.setText(App.mainController.dbUser);
            this.dbUserField.setDisable(true);
            this.dbPasswordField.setText(App.mainController.dbPassword);
            this.dbPasswordField.setDisable(true);
        }
    }

    @FXML
    private TextField kundennr_eingabe;
    @FXML
    private TextField dbUserField;
    @FXML
    private PasswordField dbPasswordField;

    @FXML
    private void checkUser() throws IOException {
        if(!App.mainController.isConnected()) {
            App.mainController.connectToDatabase(dbUserField.getText(), dbPasswordField.getText());
        }

        ArrayList<User> temp = App.mainController.getUserListe();
        String kundennr = kundennr_eingabe.getText();
        for (User u : temp
        ) {
            if (u.kundenummer.equals(kundennr)) {
                App.mainController.setAngemeldeterUser(u);
                App.setRoot("secondary");
            }
        }
    }
}
