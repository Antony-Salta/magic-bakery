package gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        //Stage newStage = new Stage();
        Group root = new Group();
        Scene scene = new Scene(root, Color.rgb(190,120,60));

        stage.setTitle("Kim Joy's Magic Bakery!");
        Image icon = new Image("file:pictures/KJMB_Logo.png");
        stage.getIcons().add(icon);

        stage.setWidth(1360);
        stage.setHeight(720);
        stage.setResizable(false);
        stage.setX(50);
        stage.setY(50);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("Hit Q to escape fullscreen");
        stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("q"));

        stage.setScene(scene);
        stage.show();
    }
}
