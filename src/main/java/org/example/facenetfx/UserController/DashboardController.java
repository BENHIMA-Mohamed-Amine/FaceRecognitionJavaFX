package org.example.facenetfx.UserController;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import org.example.facenetfx.Utils.NavBarController;

public class DashboardController {
    private NavBarController navBarController;

    public DashboardController() {
        // Initialize NavBarController
        this.navBarController = new NavBarController();
    }
    @FXML
    private void handleHover(MouseEvent event) {
        navBarController.handleHover(event);
    }

    @FXML
    private void handleExit(MouseEvent event) {
        navBarController.handleExit(event);
    }

}
