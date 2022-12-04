module com.cgvsu.cubicsplinefxapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;
    requires jdk.accessibility;


    opens com.cgvsu.cubicsplinefxapp to javafx.fxml;
    exports com.cgvsu.cubicsplinefxapp;
}