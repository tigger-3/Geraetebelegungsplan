module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires java.sql;
    requires ojdbc8;

    opens org.example to javafx.fxml;
    exports org.example;
}