module org.example.facenetfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires libtensorflow;
    requires opencv;
    requires java.desktop;


    opens org.example.facenetfx to javafx.fxml;
    exports org.example.facenetfx;
}