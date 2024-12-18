module org.example.facenetfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires libtensorflow;
    requires opencv;
    requires java.desktop;


    opens org.example.facenetfx to javafx.fxml;
    opens org.example.facenetfx.UserController to javafx.fxml;
    opens org.example.facenetfx.Utils to javafx.fxml;
    exports org.example.facenetfx.Utils;
    exports org.example.facenetfx;
    opens org.example.facenetfx.Logs to javafx.fxml;
}