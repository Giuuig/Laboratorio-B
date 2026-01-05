
package com.bookrecommender.ui;
import com.bookrecommender.dao.DBManager;
import com.bookrecommender.server.ServerBR;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        DBManager.getConnection(); 
        // Start server inside the same JVM (concurrent, socket-based)
        ServerBR.startDefault();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main-view.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);
        stage.setTitle("BookRecommender");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
