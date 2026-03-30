package pl.kryptografia.view;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/gui.fxml"));
        Scene scene = new Scene(root);
        root.setStyle("-fx-background-image: url(glorp.png); -fx-background-size: 100%;");
        stage.setTitle("DES");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/glorp.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}