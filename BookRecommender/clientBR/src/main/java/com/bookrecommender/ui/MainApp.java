package com.bookrecommender.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Applicazione JavaFX client.
 * NON avvia il server: ci si connette a un serverBR esterno (jar separato).
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main-view.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);
        stage.setTitle("BookRecommender - Client");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
