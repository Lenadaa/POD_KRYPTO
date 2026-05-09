package pl.kryptografia.view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class App extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/gui.fxml"));
        Scene scene = new Scene(root);
        Random random = new Random();
        int chance = random.nextInt(100) + 1;
        if (chance <= 67) {
            root.setStyle("-fx-background-image: url(glorp.png); -fx-background-size: 100%;");
        }else{
            root.setStyle("-fx-background-image: url(pen.jpg); -fx-background-size: 100%;");
        }
        stage.setTitle("DES");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/glorp.png")));
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}