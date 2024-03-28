package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

//This is communicating between controllers, since you could have different controllers for different scenes to separate stuff out.
public class NewController {
    @FXML
    Label nameLabel;
    private Stage stage;
    private Scene scene;
    private Parent root;
    public void displayName(String name)
    {
        nameLabel.setText("Hello: " + name);
    }

    public void switchToScene1(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("test.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        String css = this.getClass().getResource("example.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }
}
