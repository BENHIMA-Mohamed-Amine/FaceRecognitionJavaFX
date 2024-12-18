package org.example.facenetfx.UserController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label loginMessage; // A label to show login success or error messages

    // This method is called when the "Log In" button is pressed
    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Example validation - you can replace this with actual logic (like checking a database)
        if (username.equals("admin") && password.equals("password")) {
//            System.out.println(getClass().getResource("./UserViews/register-user.fxml"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserViews/register-user.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } else {
            loginMessage.setText("Invalid credentials, please try again.");
            loginMessage.setStyle("-fx-text-fill: red;");
        }

        // Clear the fields after login attempt (optional)
        usernameField.clear();
        passwordField.clear();
    }
}

