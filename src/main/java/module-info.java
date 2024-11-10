module com.example.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.javafx to javafx.fxml;
    exports com.example.javafx;

    opens Domain to javafx.base;
    opens Repository to javafx.base;
    opens Service to javafx.base;
    opens Console to javafx.base;

}