package gui;

import bakery.CustomerOrder;
import bakery.Customers;
import bakery.MagicBakery;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainHandler
{
    private MagicBakery bakery;
    @FXML
    Label actionsLeft;
    @FXML
    Label currentPlayer;
    @FXML
    HBox customerRow;
    @FXML
    HBox layerRow;
    @FXML
    HBox pantryRow;
    @FXML
    HBox handRow;

    public void setup(MagicBakery bakery)
    {
        this.bakery = bakery;
        drawCustomers();
        drawLayers();
        drawPantry();
        drawHand();

    }
    public void drawCustomers()
    {
        customerRow.getChildren().clear();

        Customers customers = bakery.getCustomers();
        Collection<CustomerOrder> activeCustomers = bakery.getCustomers().getActiveCustomers();
        //This section will make appropriate customerDeck represenation
        if(customers.getCustomerDeck().isEmpty())
        {

            drawEmptyStack(customerRow, "Customer Deck");
        }
        else{
            double height = customerRow.getScene().getWindow().getHeight() / 6;
            Rectangle backing = new Rectangle(height * 2/3, height); // make a card that is 1.5x longer than it is wide
            backing.setFill(Color.LIGHTBLUE);
            backing.setArcHeight(10);
            backing.setStroke(Color.WHITE);
            backing.setStrokeWidth(5);

            Label nameLabel = new Label("Customer");
            nameLabel.setFont(new Font("Verdana", 15));
            Label level = new Label( Integer.toString( ((Stack<CustomerOrder>) customers.getCustomerDeck()).peek().getLevel()) );
            nameLabel.setFont(new Font("Verdana", 15));

            ImageView logo = new ImageView("file:images/KJMB_Logo.png");
            logo.setFitHeight(height/2);
            logo.setPreserveRatio(true);
            StackPane card = new StackPane();
            card.getChildren().addAll(backing,nameLabel,level,logo);
            StackPane.setMargin(nameLabel,new Insets(0,0,20,0));
            StackPane.setMargin(level,new Insets(10,0,0,10));
            StackPane.setAlignment(nameLabel,Pos.BOTTOM_CENTER);
            StackPane.setAlignment(level,Pos.TOP_LEFT);
            StackPane.setAlignment(logo,Pos.CENTER);
            StackPane.setMargin(logo,new Insets(0,0,5,0));
            customerRow.getChildren().add(card);

        }


    }
    public void drawLayers()
    {

    }
    public void drawPantry()
    {

    }
    public void drawHand()
    {

    }
    public void drawEmptyStack(HBox row, String name)
    {
        double height = customerRow.getScene().getWindow().getHeight() / 5;
        Rectangle backing = new Rectangle(height * 2/3, height); // make a card that is 1.5x longer than it is wide
        backing.setOpacity(0.2);
        backing.setFill(Color.WHITE);
        backing.setArcHeight(10);
        backing.setStroke(Color.GRAY);
        backing.setStrokeWidth(5);
        backing.setStrokeDashOffset(3);

        Label nameLabel = new Label(name);
        nameLabel.setAlignment(Pos.BOTTOM_CENTER);
        nameLabel.setFont(new Font("Verdana", 15));
        StackPane card = new StackPane(backing,nameLabel);
        StackPane.setMargin(nameLabel,new Insets(0,0,20,0));
        row.getChildren().add(card);
    }
}
