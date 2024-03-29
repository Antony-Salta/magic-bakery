package gui;

import bakery.MagicBakery;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

//Chocolate image by Freepik at https://www.freepik.com/free-vector/flat-design-pixel-art-food-illustration_38216041.htm#query=chocolate%20pixel%20art&position=2&from_view=keyword&track=ais&uuid=bc60eb34-62f3-4321-b470-b02975120073


public class GUI extends Application {

    private Parent root;

    public static void main(String[] args) {
        launch(args);
        System.out.println("done now");
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("startPrompt.fxml"));
        Scene scene = new Scene(root);
//
//        VBox root = new VBox(15);
//        root.setAlignment(Pos.TOP_CENTER);
//        root.setPadding(new Insets(30));
//        Label choice = new Label("Please choose whether you would like to start a new game or load an existing one from file");
//
//
//        Button start = new Button("Start");
//        start.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                new MainHandler(root).startPrompt(actionEvent););
//            }
//        });
//
//        Button load = new Button("Load");
//        start.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                handler.loadPrompt(actionEvent);
//            }
//        });
//        HBox buttonRow = new HBox(start,load);
//        buttonRow.setSpacing(100);
//        buttonRow.setAlignment(Pos.CENTER);
//
//        root.getChildren().addAll(choice,buttonRow);

        String css = this.getClass().getResource("main.css").toExternalForm();
        scene.getStylesheets().add(css);
        //interestingly the styling disappears when you switch around with scenes
        //That's because the way that this does it, you load the scene from the fxml file, so you have to add the css again if you want it

        //Scene scene = new Scene(root, 1280, 720, Color.SADDLEBROWN);
        /*
        //Stage newStage = new Stage();
        Group root = new Group();

        // Color.rgb(190,120,60)
        stage.setTitle("Kim Joy's Magic Bakery!");
        Image icon = new Image("file:images/KJMB_Logo.png");
        stage.getIcons().add(icon);

        Text text = new Text(20,80, "abcdefghiklmnop");
        text.setFont(Font.font("Consolas", 40));
        text.setFill(Color.LIGHTSKYBLUE);

        Line line = new Line(20,100,360,100);
        line.setStrokeWidth(5);
        line.setStroke(Color.RED);
        line.setOpacity(0.2);
        line.setRotate(45);

        Rectangle rectangle = new Rectangle(300,300,100,50);
        rectangle.setFill(Color.BISQUE);
        rectangle.setStrokeWidth(5);
        rectangle.setStroke(Color.RED);
        Polygon triangle = new Polygon();
        triangle.getPoints().setAll(360.0, 360.0,
                400.0,400.0,
                500.0,400.0);
        triangle.setFill(Color.TEAL);

        Circle circle = new Circle(400,100, 50, Color.DARKMAGENTA);

        //Image chocolate = new Image("file:images/pixelChocolate.jpg");
        ImageView imageView = new ImageView("file:images/pixelChocolate.jpg");
        imageView.setX(500);
        imageView.setY(100);
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);

        root.getChildren().addAll(line,text,rectangle,triangle,circle,imageView);
*/
//        stage.setWidth(1360);
//        stage.setHeight(720);
//        stage.setResizable(false);
//        stage.setX(50);
//        stage.setY(50);
        stage.setFullScreen(true);
        //stage.setFullScreenExitHint("Hit Q to escape fullscreen");
        //stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("q"));
        //

        stage.setScene(scene);
        stage.show();

        //funky validation on closing the application
        stage.setOnCloseRequest(event -> {
            event.consume();
            logout(stage);
        });
    }

    public void makeCardStacks()
    {
        final Image logo = new Image("file:image/KJMB_Logo.png");

        StackPane customerBack = new StackPane();
    }

    public void makeCustomer()
    {
        Label name;
        StackPane card = new StackPane();

    }
    public void makeLayer()
    {

    }
    public void makeIngredient()
    {

    }


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
