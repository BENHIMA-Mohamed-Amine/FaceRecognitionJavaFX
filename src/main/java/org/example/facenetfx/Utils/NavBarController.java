package org.example.facenetfx.Utils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class NavBarController {
    @FXML private Button dashboardLink;
    @FXML private Button usersLink;
    @FXML private Button logsLink;
    @FXML private Button profileLink;
    // On mouse exit (when the mouse leaves the button), reset the style
    @FXML
    public void handleExit(MouseEvent mouseEvent) {
        Button sourceButton = (Button) mouseEvent.getSource();
        sourceButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
    }

    @FXML
    public void handleHover(MouseEvent mouseEvent) {
        Button sourceButton = (Button) mouseEvent.getSource();
        sourceButton.setStyle("-fx-background-color: #444; -fx-text-fill: white;");
    }

    @FXML
    public void handleGoToDashboard() throws IOException {
        Stage stage = (Stage) dashboardLink.getScene().getWindow();
        if(stage.getScene().getRoot().getId()!=null && stage.getScene().getRoot().getId().equals("dashboardRoot")) {
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/facenetfx/UserViews/dashboard.fxml"));
        Parent root = loader.load();
        root.setId("dashboardRoot");
        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void handleGoToUsers() throws IOException {
        Stage stage = (Stage) usersLink.getScene().getWindow();
        if (stage.getScene().getRoot().getId() != null && stage.getScene().getRoot().getId().equals("usersRoot")) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/facenetfx/UserViews/users-table.fxml"));
        Parent root = loader.load();
        root.setId("usersRoot");
        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void handleGoToLogs() throws IOException {
        Stage stage = (Stage) usersLink.getScene().getWindow();
        if (stage.getScene().getRoot().getId() != null && stage.getScene().getRoot().getId().equals("logsRoot")) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/facenetfx/Logs/logs.fxml"));
        Parent root = loader.load();
        root.setId("logsRoot");
        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void handleGoToProfile() throws IOException {
        Stage stage = (Stage) usersLink.getScene().getWindow();
        if (stage.getScene().getRoot().getId() != null && stage.getScene().getRoot().getId().equals("profileRoot")) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/facenetfx/UserViews/profile.fxml"));
        Parent root = loader.load();
        root.setId("profileRoot");
        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.show();
    }



}
