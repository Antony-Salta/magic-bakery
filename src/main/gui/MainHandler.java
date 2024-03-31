package gui;

import bakery.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class MainHandler
{
    private MagicBakery bakery;
    @FXML
    private Label actionsLeft;
    @FXML
    private Label currentPlayer;
    @FXML
    private HBox customerRow;
    @FXML
    private HBox layerRow;
    @FXML
    private HBox pantryRow;
    @FXML
    private HBox handRow;

    @FXML
    private VBox leftHands;

    @FXML VBox rightHands;

    private DropShadow yellowHighlight = new DropShadow(5,0,5, Color.YELLOW);
    private DropShadow blueHighlight = new DropShadow(5,0,5, Color.BLUE);
    private DropShadow greenHighlight = new DropShadow(5,0,5, Color.GREEN);

    private Image logo = new Image("file:images/KJMB_Logo.png");




    /**
     * Redraws all rows, generally for after an action.
     *
     * The other hands will not be redrawn with this.
     * updateCurrentPlayer and updateActions left aren't called, since that will always be done in handleTurnEnd
     */
    public void drawRows()
    {
        drawCustomers();
        drawLayers();
        drawPantry();
        drawHand();
    }

    /**
     *
     * @param bakery the bakery object passed in from the startHandler
     */
    public void setup(MagicBakery bakery)
    {
        this.bakery = bakery;
        drawRows();
        drawOtherHands();
        updateActionsLeft();
        updateCurrentPlayer();

    }
    public void drawFromPantry(MouseEvent event)
    {
        StackPane card = (StackPane) event.getSource();
        String name = ((Label) card.getChildren().get(2)).getText();
        bakery.drawFromPantry(name);

         if(handleTurnEnd())
             drawPantry();
         else
         {
             drawRows();
         }


    }
    public void drawFromPantryDeck(MouseEvent event)
    {
        bakery.drawFromPantry((Ingredient) null);
        if(!handleTurnEnd())
        {
            drawCustomers();
            drawLayers();
            drawHand();
        }
    }
    @FXML
    public void refreshPantry()
    {
        bakery.refreshPantry();
        handleTurnEnd();
        drawPantry();

    }

    /**
     * This function should be called after any action. It will check if the current player's turn has ended, and then check if the round has ended
     * It will update the bakery methods as needed
     * It will redraw everything that needs to be redrawn if it is the end of a player's turn, which is everything but the pantry row.
     * It will always update the number of actions left
     * It will always update the currentPlayer when needed
     * @return whether it is the end of a turn, and therefore if everything but the pantry row has been redrawn
     */
    public boolean handleTurnEnd()
    {
        boolean turnEnd = false;
        if(bakery.getActionsRemaining() == 0) {
            turnEnd = true;
            updateCurrentPlayer();
            if(bakery.endTurn())
            {
                Customers customers = bakery.getCustomers();
                //If the game is ending
                if(customers.isEmpty() && customers.getCustomerDeck().isEmpty())
                {
                    AnchorPane root = (AnchorPane) customerRow.getScene().getRoot();
                    root.getChildren().clear();
                    Label end = new Label("Game over!");
                    end.setFont(new Font("Verdana", 48));
                    end.setTextFill(Color.WHITE);
                    root.getChildren().add(end);
                }
            }
            drawCustomers();
            drawLayers();
            drawHand();
            drawOtherHands();
        }
        updateActionsLeft();
        return turnEnd;

    }
    public void askFulfilOrGarnish(MouseEvent event, CustomerOrder order)
    {
        StackPane card = (StackPane) event.getSource();
        Button fulfil = new Button("Fulfil?");
        Button garnish = new Button("Garnish?");
        card.getChildren().addAll(fulfil,garnish);
        StackPane.setAlignment(fulfil,Pos.CENTER_LEFT);
        StackPane.setAlignment(garnish,Pos.CENTER_RIGHT);
        fulfil.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fulfilOrder(order, false);
            }
        });
        garnish.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fulfilOrder(order, true);
            }
        });
    }
    public void fulfilOrder(CustomerOrder order, boolean garnish)
    {
        bakery.fulfillOrder(order,garnish);
        if(handleTurnEnd())
            drawPantry(); // can technically change by putting ingredients back in when the pantry deck is emptied.
        else
            drawRows();

    }

    public void drawCustomers()
    {
        customerRow.getChildren().clear();

        Customers customers = bakery.getCustomers();
        Collection<CustomerOrder> activeCustomers = bakery.getCustomers().getActiveCustomers();
        //This section will make appropriate customerDeck representation
        if(customers.getCustomerDeck().isEmpty())
        {
            drawCardSlot(customerRow, "Customer Deck");
        }
        else
        {
            StackPane card = makeStackCard("Customer");

            Label level = new Label( Integer.toString( ((Stack<CustomerOrder>) customers.getCustomerDeck()).peek().getLevel()) );
            level.setFont(new Font("Verdana", 15));
            level.setMaxWidth( ((Rectangle) card.getChildren().get(0)).getWidth());

            card.getChildren().add(level);
            StackPane.setMargin(level,new Insets(10,0,0,10));
            StackPane.setAlignment(level,Pos.TOP_LEFT);

            Rectangle backing = (Rectangle) card.getChildren().get(0);
            backing.setFill(Color.LIGHTBLUE);
            backing.setStroke(Color.WHITE);

            customerRow.getChildren().add(card);
        }

        Collection<CustomerOrder> fulfilable = bakery.getFulfilableCustomers();
        Collection<CustomerOrder> garnishable = bakery.getGarnishableCustomers();
        Iterator<CustomerOrder> iterator = (
                (LinkedList<CustomerOrder>) customers.getActiveCustomers()
        ).descendingIterator();
        while(iterator.hasNext())
        {
            CustomerOrder order = iterator.next();
            if(order == null)
            {
                drawCardSlot(customerRow, "Customer order");
            }
            else {
                StackPane card = makeCustomerCard(order);
                customerRow.getChildren().add(card);
                if(garnishable.contains(order))
                {
                    card.setEffect(greenHighlight);
                    card.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            askFulfilOrGarnish(event, order);
                        }
                    });
                }
                else if(fulfilable.contains(order))
                {
                    card.setEffect(blueHighlight);
                    card.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            fulfilOrder(order, false);
                        }
                    });
                }
            }
        }
    }
    public void drawLayers()
    {
        layerRow.getChildren().clear();
        Collection<Layer> bakeables =  bakery.getBakeableLayers();
        for (Layer layer: bakery.getLayers())
        {
            StackPane card = makeLayerCard(layer);
            layerRow.getChildren().add(card);
            if(bakeables.contains(layer))
            {
                // indicate bakeable layers
                card.setEffect(yellowHighlight);
                card.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        bakery.bakeLayer(layer);
                        if(handleTurnEnd())
                            drawPantry(); //the pantry could technically need to be redrawn if the pantry deck is empty
                        else
                            drawRows();

                    }
                });
            }

        }
    }
    public void drawPantry()
    {
        pantryRow.getChildren().removeIf(node -> node instanceof StackPane); //keep the refresh pantry button
        StackPane stackCard = makeStackCard("Ingredient");
        Rectangle stackBacking = (Rectangle) stackCard.getChildren().get(0);
        stackBacking.setFill(Color.GOLD);
        stackBacking.setStroke(Color.WHITE);
        pantryRow.getChildren().add(stackCard);
        stackCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                drawFromPantryDeck(event);
            }
        });

        for(Ingredient ingredient : bakery.getPantry())
        {
            StackPane card = makeNamedCard(ingredient.toString());
            Rectangle backing = (Rectangle) card.getChildren().get(0);
            backing.setFill(Color.WHITE);
            backing.setStroke(Color.GOLD);
            card.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event)
                {
                    drawFromPantry(event);
                }
            });

            pantryRow.getChildren().add(card);

        }
    }
    public void drawHand()
    {
        handRow.getChildren().clear();

        double maxWidth = handRow.getScene().getWidth() * 2/3 -100;
        StackPane handPane = makePlayerHand(bakery.getCurrentPlayer(),maxWidth);
        handRow.getChildren().add(handPane);
    }

    public void drawOtherHands()
    {
        leftHands.getChildren().clear();
        rightHands.getChildren().clear();
        double maxWidth = (leftHands.getScene().getHeight()/2) -100;
        int count =0;
        for(Player player : bakery.getPlayers())
        {
            if(!player.equals(bakery.getCurrentPlayer()))
            {
                StackPane handPane = makePlayerHand(player,maxWidth);


                if(count %2 == 0) //stick it in the left hand side if even, to spread it somewhat evenly
                {
                    handPane.setRotate(90);
                    Group bounding = new Group(handPane);
                    bounding.maxWidth(handPane.getHeight());
                    bounding.maxHeight(handPane.getWidth());
                    leftHands.getChildren().add(bounding); // So that the bounding boxes work properly
                }
                else
                {
                    handPane.setRotate(-90);
                    Group bounding = new Group(handPane);
                    bounding.maxWidth(handPane.getHeight());
                    bounding.maxHeight(handPane.getWidth());
                    rightHands.getChildren().add(bounding); // So that the bounding boxes work properly
                }
                count++;
            }
        }
    }

    public StackPane makePlayerHand(Player player, double maxWidth)
    {
        StackPane handPane = new StackPane();
        double sceneHeight = handRow.getScene().getHeight();
        handPane.setMaxWidth(maxWidth);
        double cardWidth = ((sceneHeight /6) * 2/3) + 10; //This is terrible practice since this can easily change, but this is how the width is calculated normally, plus the stroke on the outside.

        int numCards = player.getHand().size();
        double offset;
        if(cardWidth * numCards <= maxWidth)
            offset = cardWidth * 2;
        else
            offset = maxWidth * 2 / numCards;

        int count = 0;
        for(Ingredient ingredient : player.getHand())
        {
            StackPane card;
            if(ingredient instanceof Layer)
            {
                card = makeLayerCard((Layer) ingredient);
            }
            else
            {
                card = makeNamedCard(ingredient.toString());
                Rectangle backing = (Rectangle) card.getChildren().get(0);
                backing.setFill(Color.WHITE);
                backing.setStroke(Color.GOLD);
            }

            handPane.getChildren().add(card);
            double evenOffset = 0;
            if(numCards % 2 == 0)
                evenOffset = offset /2;
            StackPane.setMargin(card, new Insets(0, (numCards/2 - count) * offset - evenOffset, 0,0));
            count++;

            if(player.equals(bakery.getCurrentPlayer()))
            {

            }
            TranslateTransition hover = new TranslateTransition(Duration.millis(200),card);
            hover.setToY(card.getLayoutY() -50);
            hover.setFromY(card.getLayoutY());
            int originalIndex = handPane.getChildren().indexOf(card);
            card.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Duration time = hover.getCurrentTime();
                    hover.setRate(1);
                    hover.playFrom(time);
                    card.toFront();
                }
            });
            card.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Duration time = hover.getCurrentTime();
                    hover.setRate(-1);
                    hover.playFrom(time);
                    handPane.getChildren().remove(handPane.getChildren().size()-1); // Weird screwing with the children list to put the card back in the right spot in terms of being ahead and behind other cards.
                    handPane.getChildren().add(originalIndex,card);
                }
            });

        }
        return handPane;
    }

    public void drawCardSlot(HBox row, String name)
    {
        StackPane card = makeBasicCard(null);
        ObservableList<Node> children = card.getChildren();
        Rectangle backing = (Rectangle) children.get(0);
        backing.setFill(Color.WHITE);
        backing.setStroke(Color.GREY);
        backing.setOpacity(0.5);
        backing.getStrokeDashArray().addAll(10d,5d); //

        Label nameLabel = new Label(name);
        nameLabel.setFont(new Font("Verdana", 15));
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.TOP_CENTER);
        nameLabel.setOpacity(0.7);
        nameLabel.setMaxWidth( ((Rectangle) card.getChildren().get(0)).getWidth());


        card.getChildren().add(nameLabel);
        StackPane.setAlignment(nameLabel,Pos.CENTER);
        row.getChildren().add(card);
    }

    /**
     * This will be used to make the basic card shape, with an image in the middle
     * @return a stackPane with the basic card shape and format. The pane will have its first child be the backing rectangle, and the second the centre image
     */
    public StackPane makeBasicCard(Image image)
    {
        double height = customerRow.getScene().getWindow().getHeight() / 6;
        double width = height * 2/3;
        Rectangle backing = new Rectangle(width, height); // make a card that is 1.5x longer than it is wide
        backing.setArcHeight(15);
        backing.setArcWidth(15);
        backing.setStroke(Color.WHITE);
        backing.setStrokeWidth(5);
        backing.setStrokeType(StrokeType.OUTSIDE);

        ImageView centreImage = new ImageView(image);
        centreImage.setFitHeight(height);
        centreImage.setFitWidth(width);
        centreImage.setPreserveRatio(true);

        StackPane card = new StackPane();
        card.getChildren().addAll(backing, centreImage);
        StackPane.setMargin(centreImage,new Insets(0,0,5,0));
        card.setMaxWidth(width + 10);
        card.setMaxHeight(height + 10);
        return card;
    }

    public StackPane makeStackCard(String name)
    {
        StackPane card = makeBasicCard(logo);
        Label stackName = new Label(name);
        stackName.setWrapText(true);
        stackName.setAlignment(Pos.CENTER);
        stackName.setMaxWidth(((Rectangle) card.getChildren().get(0)).getWidth());
        card.getChildren().add(stackName);
        StackPane.setAlignment(stackName, Pos.BOTTOM_CENTER);
        StackPane.setMargin(stackName,new Insets(0,0,10,0));
        return card;
    }

    /**
     * Makes a card based on the makeBasicCard with a name at the top
     * @return a stackPane that with the children: rectangle, centre image, name label aligned at the top
     */
    public StackPane makeNamedCard(String name)
    {
        StackPane card = makeBasicCard(null);
        Label nameLabel = new Label(name);
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(((Rectangle) card.getChildren().get(0)).getWidth());
        card.getChildren().add(nameLabel);
        StackPane.setAlignment(nameLabel,Pos.TOP_CENTER);
        StackPane.setMargin(nameLabel,new Insets(3,0,0,0));
        return card;
    }

    /**
     * This method will use the makeNamedCard to make a card as specified in that function, with a recipe displayed at the bottom
     * @param layer the layer that this card will be of
     * @return a StackPane representing a card with the children: backing rectangle, centre image, name label at the top, recipe label at the bottom
     */
    public StackPane makeLayerCard(Layer layer)
    {
        StackPane card = makeNamedCard(layer.toString());
        Label recipe = new Label("Recipe:\n" + layer.getRecipeDescription());
        recipe.setWrapText(true);
        recipe.setAlignment(Pos.CENTER);
        recipe.setPadding(new Insets(0,5,0,5));

        card.getChildren().add(recipe);
        StackPane.setAlignment(recipe,Pos.BOTTOM_CENTER);
        Rectangle backing = (Rectangle) card.getChildren().get(0);
        backing.setFill(Color.WHITE);
        backing.setStroke(Color.PINK);
        recipe.setMaxWidth(backing.getWidth());
        StackPane.setMargin(recipe,new Insets(0,0,10,0));
        return card;
    }
    public StackPane makeCustomerCard(CustomerOrder order)
    {
        StackPane card = makeNamedCard(order.toString());
        Label recipe = new Label("Recipe:\n" + order.getRecipeDescription());
        recipe.setWrapText(true);
        recipe.setAlignment(Pos.TOP_CENTER);
        card.getChildren().add(recipe);
        StackPane.setAlignment(recipe,Pos.BOTTOM_CENTER);
        StackPane.setMargin(recipe, new Insets(0,0,10,0));
        recipe.setPadding(new Insets(0,5,0,5));
        recipe.setAlignment(Pos.CENTER);
        if( !(order.getGarnish() == null || order.getGarnish().isEmpty()) )
        {
            recipe.setText(recipe.getText() + "\nGarnish:\n" + order.getGarnishDescription());
        }

        Rectangle backing = (Rectangle) card.getChildren().get(0);
        backing.setFill(Color.WHITE);
        backing.setStroke(Color.LIGHTBLUE);
        recipe.setMaxWidth(backing.getWidth());
        return card;
    }
    public void updateCurrentPlayer()
    {
        currentPlayer.setText("Current Player: " + bakery.getCurrentPlayer().toString());
    }
    public void updateActionsLeft()
    {
        actionsLeft.setText(bakery.getActionsRemaining() + "/" + bakery.getActionsPermitted() + "Actions left");
    }
}
