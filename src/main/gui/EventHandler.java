package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class EventHandler {

    @FXML
    private Circle circle;
    public static void changeCircle()
    {
    }
    private double x;
    private double y;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField nameEntry;

    @FXML
    private Button logoutButton;

    @FXML
    private AnchorPane scenePane;

    @FXML
    private Button changeCrab;

    @FXML
    private ImageView crabView;

    public void switchToScene2(ActionEvent event) throws IOException {
        String name = nameEntry.getText();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Scene2.fxml"));
        root = loader.load();
        NewController newController = loader.getController();
        newController.displayName(name);

        //Parent root = FXMLLoader.load(getClass().getResource("scene2.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void logout(ActionEvent e)
    {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You're about to log out");
        alert.setContentText("Do you want to save before exiting?");
        if(alert.showAndWait().get() == ButtonType.OK)
        {
            stage = (Stage) scenePane.getScene().getWindow();
            System.out.println("You successfully exited!");
            stage.close();
        }
    }

    public void changeCrab()
    {
        Image newCrab = new Image("file:images/crab2.jpg");
        crabView.setImage(newCrab);
    }
    //Need a separate thing for mouseEvent and Action Event, and they can't have the same name
    //The other, proper way is that if you don't need the event, don't have it as an argument
    /*
    public void changeCrabClick(MouseEvent e)
    {
        Image newCrab = new Image("file:images/crab2.jpg");
        crabView.setImage(newCrab);
    }
    */

    public void up (ActionEvent e)
    {
        circle.setCenterY(y-=5);
    }
    public void down (ActionEvent e)
    {
        circle.setCenterY(y+=5);
    }
    public void left (ActionEvent e)
    {
        circle.setCenterX(x-=5);
    }
    public void right (ActionEvent e)
    {
        circle.setCenterX(x+=5);
    }
}
