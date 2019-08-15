package org.bse.core;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Runs the UbcSchedulerTool Application.
 */
public class CoreMain extends Application {

    public static final Version CURRENT_VERSION = Version.VERSION_0_0_0;

    @Override
    public void start(Stage stage) {
        stage.setTitle("UBC Course Scheduling Tool - " + CURRENT_VERSION.getVersionString());
        final Parent root = new BorderPane();
        final Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
