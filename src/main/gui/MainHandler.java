package gui;

import bakery.MagicBakery;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainHandler {

    private static MagicBakery bakery;

    @FXML
    private VBox root;


    private List<String> names = new ArrayList<>();

    public  void switchToMainGame(ActionEvent event) throws IOException {
        //apparently there is no scene
        Parent root = FXMLLoader.load(getClass().getResource("mainGame.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        String css = this.getClass().getResource("main.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public  void startPrompt(ActionEvent event)
    {
        ObservableList<Node> children = root.getChildren();
        children.clear();
        Label numPlayers = new Label("0/5 Players");
        Label prompt = new Label("Please enter the name of the player");
        TextField nameEntry = new TextField();
        nameEntry.setMaxWidth(root.getWidth() /2);
        nameEntry.setPromptText("Player name: e.g. Martin");


        HBox buttons = new HBox(100);
        buttons.setAlignment(Pos.CENTER);

        Button begin = new Button("Begin game");

        begin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    switchToMainGame(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button submit = new Button("Submit");


        submit.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent actionEvent) {
                names.add(nameEntry.getText());
                nameEntry.setText("");
                nameEntry.setPromptText("Please enter the name of the player");
                numPlayers.setText(names.size() + "/5 Players");
                if(names.size() == 2){
                    buttons.getChildren().add(begin);
                }
                if(names.size()==5)
                {
                    try {
                        switchToMainGame(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        buttons.getChildren().add(submit);

        children.addAll(numPlayers,prompt,nameEntry,buttons);

    }
    public  void loadPrompt(ActionEvent event)
    {
        root.getChildren().removeIf( node -> node instanceof Button); //Wipe out the buttons
    }




}
