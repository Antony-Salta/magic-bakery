package gui;
import gui.StartHandler;
import gui.MainHandler;

import bakery.MagicBakery;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import javafx.scene.control.ButtonType;

import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Chocolate image by Freepik at https://www.freepik.com/free-vector/flat-design-pixel-art-food-illustration_38216041.htm#query=chocolate%20pixel%20art&position=2&from_view=keyword&track=ais&uuid=bc60eb34-62f3-4321-b470-b02975120073


public class GUI extends Application {

    private Parent root;

    public static void main(String[] args) {
        launch(args);
        System.out.println("done now");
    }

    /**
     * Starts the game and sets the needed properties. The game will launch in full screen.
     * @param stage This is the stage passed in by the launch method, it's effectively the window created for the application.
     * @throws IOException If the startPrompt.fxml file cannot be read correctly.
     */
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("startPrompt.fxml"));
        Scene scene = new Scene(root);


        String css = this.getClass().getResource("main.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setFullScreen(true);
        stage.setResizable(true);


        stage.setScene(scene);
        stage.show();

        //funky validation on closing the application
        stage.setOnCloseRequest(event -> {
            event.consume();
            logout(stage);
        });
    }

    /**
     * This will make a popup window asking if the user is certain that they want to exit the game before allowing them to quit the application.
     * @param stage The window that the application exists in
     */
    public void logout(Stage stage)
    {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You're about to log out");
        alert.setContentText("Do you want to save before exiting?");
        if(alert.showAndWait().get() == ButtonType.OK)
        {
            System.out.println("You successfully exited!");
            stage.close();
        }
    }

}
