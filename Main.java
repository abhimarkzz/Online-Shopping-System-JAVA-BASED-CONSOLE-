package com.shopping;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;
            // Boot all services first

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/shopping/login.fxml"));
            Scene scene = new Scene(loader.load(), 1280, 800);
            scene.getStylesheets().add(
                    getClass().getResource("/com/shopping/styles.css").toExternalForm());
            stage.setTitle("🛒  Online Shopping System");
            stage.setScene(scene);
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.show();
        } catch (Exception e) {
            System.err.println("[Main] Failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Switch scene from any controller */
    public static void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/com/shopping/" + fxmlFile));
            Scene scene = new Scene(loader.load(), 1280, 800);
            scene.getStylesheets().add(
                    Main.class.getResource("/com/shopping/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("[Main] switchScene error (" + fxmlFile + "): " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Load a new scene and return its controller */
    public static <T> T switchSceneGetController(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/com/shopping/" + fxmlFile));
            Scene scene = new Scene(loader.load(), 1280, 800);
            scene.getStylesheets().add(
                    Main.class.getResource("/com/shopping/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
            return loader.getController();
        } catch (Exception e) {
            System.err.println("[Main] switchSceneGetController error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}