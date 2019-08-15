package org.bse.core;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Runs the UbcSchedulerTool Application.
 */
public class CoreMain extends Application {

    public static final UCSToolVersion CURRENT_VERSION = UCSToolVersion.getLatestVersion();

    @Override
    public void start(Stage stage) {
        stage.setTitle("UBC Course Scheduling Tool - " + CURRENT_VERSION.getVersionString());
        //stage.getIcons().add(new Image(CoreMain.class.getResourceAsStream()));
        final Parent root = new BorderPane();
        final Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
