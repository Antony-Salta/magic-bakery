package gui;

import bakery.MagicBakery;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StartHandler {

    private  MagicBakery bakery;

    @FXML
    private VBox root;


    private List<String> names = new ArrayList<>();

    /**
     * Switches the game from this start prompt over to the actual game once the needed information to start a game has been gotten.
     * @param event the event that starts the game proper. This is just needed to get the stage and change its scene to a new one
     * @throws IOException if MainGame.fxml or any of the card CSVs cannot be read correctly.
     */
    private void switchToMainGame(Event event) throws IOException {
        //TODO: put in an actual random seed.
        if(bakery == null)
        {
            bakery = new MagicBakery(24,"../../io/ingredients.csv", "../../io/layers.csv");
            bakery.startGame(names, "../../io/customers.csv");
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainGame.fxml"));
        AnchorPane root = loader.load();


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        String css = this.getClass().getResource("main.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setFullScreen(true);
        MainHandler mainHandler =  loader.getController();


        stage.show();
        mainHandler.setup(bakery);

    }


    /**
     * Once the start button has been clicked, all the elements needed to prompt the user to enter names is instantiated.
     * @param actionEvent this event is clicking the start, instead of load button
     */
    public void startPrompt(ActionEvent actionEvent)
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


        begin.setOnAction(event ->
        {
                try {
                    switchToMainGame(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });

        Button submit = new Button("Submit");
        EventHandler<Event> submitHandler = (event) ->
        {
            boolean added = validateName(nameEntry);
            if (added) {
                if (names.size() == 2) {
                    buttons.getChildren().add(begin);
                }
                if (names.size() == 5) {
                    try {
                        switchToMainGame(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            nameEntry.setPromptText("Player name: e.g. Martin");
            numPlayers.setText(names.size() + "/5 Players");
        };

        submit.setOnAction(e ->
        {
            submitHandler.handle(e);
        });
        nameEntry.setOnKeyPressed(keyEvent ->
        {
                if(keyEvent.getCode().equals(KeyCode.ENTER))
                    submitHandler.handle(keyEvent);
        });
        buttons.getChildren().add(submit);

        children.addAll(numPlayers,prompt,nameEntry,buttons);
    }

    /**
     * Gets the user to select the file they want to load from their file explorer system, then loads that game and carries on playing it.
     * @param event this event is the load button being pressed
     */
    @FXML
    public void loadPrompt(ActionEvent event){

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose save file");
        chooser.setInitialDirectory(new File("./"));
        File save = chooser.showOpenDialog(root.getScene().getWindow());
        try
        {
            bakery = MagicBakery.loadState(save);
            switchToMainGame(event);
        }
        catch(ClassNotFoundException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid file");
            alert.setContentText("This file could not be loaded. Are you sure it was the right file?");
            alert.show();
            e.printStackTrace();
        }
        catch (IOException IOe){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error reading file.");
            alert.setContentText("This file could not be read. Try saving and closing any unneeded files.");
            alert.show();
            IOe.printStackTrace();
        }




    }

    /**
     * Validates the name entered before adding it to the names list if valid.
     * @param nameEntry the textfield that the name is taken from
     * @return true if a name was added to the names list.
     */
    public boolean validateName(TextField nameEntry)
    {
        String name = nameEntry.getText();

        nameEntry.setText("");

        if(name.equals(""))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid");
            alert.setContentText("You can't enter an empty string as a name, this won't be entered.");
            alert.show();
            return false;
        }
        for (String storedName : names) {
            if (storedName.toLowerCase().equals(name.toLowerCase())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid");
                alert.setContentText("You can't have the same name as another player, even with different cases. Please try again");
                alert.show();
                return false;
            }
        }

        names.add(name);
        return true;

    }
}
