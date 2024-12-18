package org.example.facenetfx.UserController;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

public class RegisterUserController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button uploadButton;
    @FXML private Label imageLabel;
    @FXML private Button registerButton;
    @FXML private Label failedRegisterMessage;
    @FXML private Label successfulRegisterMessage;

    private File selectedImageFile;

    // Handle file upload
    @FXML
    private void handleUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        Stage stage = (Stage) uploadButton.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);
        if (selectedImageFile != null) {
            imageLabel.setText("Selected file: " + selectedImageFile.getName());
        }
    }

    // Handle user registration
    @FXML
    private void handleRegister(ActionEvent event) throws IOException {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            failedRegisterMessage.setText("All fields must be filled.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            failedRegisterMessage.setText("Passwords do not match.");
            return;
        }

        if (selectedImageFile == null) {
            failedRegisterMessage.setText("Please upload an image.");
            return;
        }
        // Add further registration logic (e.g., store user data, validate username, etc.)

        failedRegisterMessage.setText("");
        successfulRegisterMessage.setText("Registration successful!");

        // Redirect after a short delay
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> {
            try {
                // Load the dashboard screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/facenetfx/UserViews/dashboard.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) registerButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        delay.play();

    }
    @FXML
    private void clearMessage() {
        failedRegisterMessage.setText("");
        successfulRegisterMessage.setText("");
    }

    @FXML
    private void initialize() {
        firstNameField.setOnMouseClicked(event -> clearMessage());
        lastNameField.setOnMouseClicked(event -> clearMessage());
        usernameField.setOnMouseClicked(event -> clearMessage());
        passwordField.setOnMouseClicked(event -> clearMessage());
        confirmPasswordField.setOnMouseClicked(event -> clearMessage());
        uploadButton.setOnMouseClicked(event -> clearMessage());
//        registerButton.setOnMouseClicked(event -> clearMessage());
    }
}
