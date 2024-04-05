package gui;

import bakery.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;


import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class MainHandler
{
    private MagicBakery bakery;
    @FXML
    private Label actionsLeft;
    @FXML
    private Label currentPlayer;

    @FXML
    private ScrollPane playerScroll;

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

    @FXML VBox mainLayout;

    private int playerIndex = 1;

    private final DropShadow yellowHighlight = new DropShadow(5,0,5, Color.YELLOW);
    private final DropShadow blueHighlight = new DropShadow(5,0,5, Color.BLUE);
    private final DropShadow greenHighlight = new DropShadow(5,0,5, Color.GREEN);

    private Image logo = new Image("file:images/KJMB_Logo.png");

    private final DataFormat ingredientFormat = new DataFormat("bakery.Ingredient");



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
        drawOtherHands();
        drawRows();

        updateActionsLeft();
        updateCurrentPlayer();

    }

    @FXML
    public void saveGame()
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose save file");
        chooser.setInitialDirectory(new File("./"));
        File save = chooser.showOpenDialog(currentPlayer.getScene().getWindow());
        try
        {
            bakery.saveState(save);
            Label saveConfirmation = new Label("Game saved.");
            saveConfirmation.setFont(new Font(48));
            saveConfirmation.setBackground(new Background(new BackgroundFill( Color.color(0d,0d,0d,0.5), null, null)));
            ((AnchorPane) currentPlayer.getScene().getRoot()).getChildren().add(saveConfirmation);
            Scene scene = currentPlayer.getScene();
            saveConfirmation.setLayoutX(scene.getWidth()/2);
            saveConfirmation.setLayoutY(scene.getHeight()/2);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(3000), saveConfirmation);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.play();
            fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Node origin = ((FadeTransition) event.getSource()).getNode();
                    ((AnchorPane) origin.getParent()).getChildren().remove(origin);
                }
            });
        }

        catch (IOException IOe){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error writing to file.");
            alert.setContentText("This file could not be written to. Try saving and closing any unneeded files.");
            alert.show();
            IOe.printStackTrace();
        }
    }

    public void drawFromPantry(MouseEvent event)
    {//TODO: make an animation for this using the chosen card.
        StackPane card = (StackPane) event.getSource();
        String name = ((Label) card.getChildren().get(2)).getText();
        bakery.drawFromPantry(name);
        Bounds cardBound = card.localToScene(card.getLayoutBounds());
        double[] from = {cardBound.getCenterX(), cardBound.getCenterY()};
        StackPane hand = (StackPane) handRow.getChildren().get(0);
        Bounds handBound = hand.localToScene(hand.getLayoutBounds());
        double[] to = {handBound.getCenterX(), handBound.getCenterY()};
        

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
        if(bakery.getActionsRemaining() == 0)
        {
            turnEnd = true;
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


            //Now comes a big section to animate the hands moving across to their new positions.
            moveHandsAround();

        }
        updateActionsLeft();
        return turnEnd;

    }

    /**
     * This will move the hands around to make it more obvious when a turn ends.
     * During this, all of the rows other than the pantry will be redrawn, as this is where the current player switches
     */
    private void moveHandsAround()
    {
        int numPlayers = bakery.getPlayers().size();
        double[][] coords = new double[numPlayers][2]; // structure is that in the inner array, there is the X coordinate, then the Y coordinate.

        for (int i = 0; i < numPlayers -1; i++)
        {// This will go through all of the non-current players
            StackPane hand;
            if(i % 2 == 0)
                hand = (StackPane) leftHands.getChildren().get(i/2);
            else
                hand = (StackPane) rightHands.getChildren().get(i/2);


            double y = hand.localToScene(hand.getLayoutBounds()).getCenterY();

            double x = hand.localToScene(hand.getLayoutBounds()).getCenterX();;


            coords[i][0] = x;
            coords[i][1] = y;
        }
        StackPane currentHand = (StackPane) handRow.getChildren().get(0);

        Bounds currentBound = currentHand.localToScene(currentHand.getLayoutBounds());
        coords[numPlayers-1][0] = currentBound.getCenterX();
        coords[numPlayers-1][1] = currentBound.getCenterY();

        StackPane[] hands = new StackPane[numPlayers];

        drawCustomers();
        drawLayers();
        drawHand();
        drawOtherHands();
        updateCurrentPlayer();

        //This loop to get the hand panes has to be done again to get the new panes that were drawn, while the coordinates from before the redrawing were needed to make the transition smoother.
        for (int i = 0; i < numPlayers -1; i++)
        {
            StackPane hand;
            if(i % 2 == 0)
                hand = (StackPane) leftHands.getChildren().get(i/2);
            else
                hand = (StackPane) rightHands.getChildren().get(i/2);

            hands[i] = hand;
        }
        hands[numPlayers-1] = (StackPane) handRow.getChildren().get(0);



        TranslateTransition[] moves = new TranslateTransition[numPlayers];
        RotateTransition[] rotates = new RotateTransition[numPlayers];
        for (int i = 0; i < numPlayers; i++)
        {
            TranslateTransition moveHand = new TranslateTransition(Duration.millis(1500), hands[i]);
            moveHand.setFromX(coords[(i+1) % numPlayers][0] - coords[i][0]);
            moveHand.setFromY(coords[(i+1) % numPlayers][1] - coords[i][1]);
            moveHand.setToX(0);
            moveHand.setToY(0);


            RotateTransition rotateHand = new RotateTransition(Duration.millis(1000), hands[i]);
            if(i == numPlayers-1) // will be the main hand
            {
                rotateHand.setFromAngle(90);
                rotateHand.setToAngle(0);
            }
            else if( i == numPlayers -2) // will be coming from the main hand
            {
                if(numPlayers % 2 == 0)
                    rotateHand.setFromAngle(-90);
                else
                    rotateHand.setFromAngle(90);
                rotateHand.setToAngle(0);
            }
            else
            {
                rotateHand.setFromAngle(180);
                rotateHand.setToAngle(0);
            }


            moves[i] = moveHand;
            rotates[i] = rotateHand;
        }

        for (int i = 0; i <numPlayers ; i++) {
            moves[i].play();
            rotates[i].play();
        }
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

        double maxWidth = handRow.getScene().getWidth()/2;
        StackPane handPane = makePlayerHand(bakery.getCurrentPlayer(),maxWidth);
        handRow.getChildren().add(handPane);
    }

    public void drawOtherHands()
    {
        leftHands.getChildren().clear();
        rightHands.getChildren().clear();
        rightHands.setLayoutX(rightHands.getScene().getWidth());
        leftHands.setLayoutX(0);

        double cardHeight = calculateCardHeight();
        AnchorPane.setLeftAnchor(mainLayout, cardHeight);
        AnchorPane.setRightAnchor(mainLayout, cardHeight);
        rightHands.setPrefWidth(cardHeight + 60); //This is a very weird thing because of the Groups that are used later, this is needed to make it so that only the hand with a mouse over has a hand hover, and not have the entire side move.
        leftHands.setPrefWidth(cardHeight + 60);

        double maxWidth = (leftHands.getScene().getHeight()/2) -150;
        int count =0;
        int numPlayers = bakery.getPlayers().size();
        int i = playerIndex;
        do {
            Player player =  ((ArrayList<Player>) bakery.getPlayers()).get(i);

            if(!player.equals(bakery.getCurrentPlayer()))
            {
                StackPane handPane = makePlayerHand(player,maxWidth);

                handPane.setOnDragOver(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        if(event.getDragboard().hasContent(ingredientFormat))
                        {
                            event.acceptTransferModes(TransferMode.MOVE);
                        }
                        event.consume();
                    }
                });

                handPane.setOnDragEntered(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        for( Node card :  handPane.getChildren())
                        {
                            ((Rectangle) ((StackPane)card).getChildren().get(0)).setEffect(new ColorAdjust(0.3, 0.3, 0, 0.5));
                        }

                        event.consume();
                    }
                });
                handPane.setOnDragExited(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        for( Node card :  handPane.getChildren())
                        {
                            ((Rectangle) ((StackPane)card).getChildren().get(0)).setEffect(new ColorAdjust(0, 0, 0, 0));
                        }
                        event.consume();
                    }
                });

                handPane.setOnDragDropped(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        boolean dragDropWorked = false;

                        if(event.getDragboard().hasContent(ingredientFormat))
                        {
                            Ingredient passedCard = (Ingredient) event.getDragboard().getContent(ingredientFormat);
                            bakery.passCard(passedCard,player);
                            dragDropWorked = true;
                        }

                        event.setDropCompleted(dragDropWorked);

                        event.consume();
                    }
                });

                ScrollPane name = makePlayerName(player.toString(), cardHeight);

                Group bounding = new Group(handPane);
                StackPane grouping = new StackPane();

                if(count %2 == 0) //stick it in the left hand side if even, to spread it somewhat evenly
                {
                    handPane.setRotate(90);
                    grouping.getChildren().addAll(bounding, name);
                    leftHands.getChildren().add(grouping);
                }
                else
                {
                    handPane.setRotate(-90);
                    grouping.getChildren().addAll(bounding, name);
                    rightHands.getChildren().add(grouping);
                }
                StackPane.setAlignment(name,Pos.TOP_CENTER);
                StackPane.setMargin(name,new Insets(-40,0,0,0));
                StackPane.setAlignment(bounding,Pos.BOTTOM_CENTER);
                count++;

            }

            i = (i+1) % numPlayers;
        }while( i != playerIndex);
        playerIndex = (playerIndex +1) % numPlayers;
        /*This player index thing makes it so that the position of the hands is determined by how close they are to playing.
        The order is that the next player will be top left, then top right, then bottom left, then bottom right.
        */

    }

    public StackPane makePlayerHand(Player player, double maxWidth)
    {
        StackPane handPane = new StackPane();
        handPane.setMaxWidth(maxWidth);
        double cardHeight = calculateCardHeight();
        handPane.setMaxHeight(cardHeight);
        double cardWidth = (cardHeight * 2/3) + 10;

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
                card = makeIngredientCard(ingredient.toString());
            }

            handPane.getChildren().add(card);
            double evenOffset = 0;
            if(numCards % 2 == 0)
                evenOffset = offset /2;

            StackPane.setMargin(card, new Insets(0, (numCards/2 - count) * offset - evenOffset, 0,0));
            count++;

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

            if(player.equals(bakery.getCurrentPlayer()))
            { //Give the ability to pass cards
                card.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Dragboard db = card.startDragAndDrop(TransferMode.MOVE);

                        ClipboardContent content = new ClipboardContent();
                        content.put(ingredientFormat, ingredient);
                        db.setContent(content);
                        event.consume();
                    }
                });

                card.setOnDragDone(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        if(event.getTransferMode() == TransferMode.MOVE)
                        { // If the drag drop has worked
                            if(!handleTurnEnd())
                            {
                                drawCustomers();
                                drawLayers();
                                drawHand();
                                drawOtherHands();
                            }
                        }
                        event.consume();
                    }
                });
            }

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
        nameLabel.getStyleClass().add("cardLabel");
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
        double height = calculateCardHeight();
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
        StackPane card = makeBasicCard(new Image("file:images/" + name + ".png"));
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("cardLabel");
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(((Rectangle) card.getChildren().get(0)).getWidth());
        card.getChildren().add(nameLabel);
        StackPane.setAlignment(nameLabel,Pos.TOP_CENTER);
        StackPane.setMargin(nameLabel,new Insets(3,0,0,0));
        return card;
    }

    public StackPane makeIngredientCard(String name)
    {
        StackPane card = makeNamedCard(name);
        Rectangle backing = (Rectangle) card.getChildren().get(0);
        backing.setFill(Color.WHITE);
        backing.setStroke(Color.GOLD);
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
        recipe.getStyleClass().add("cardLabel");
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


        Label recipe = new Label();
        String recipeText = "Recipe:\n- " + order.getRecipeDescription();
        recipe.getStyleClass().add("customerRecipe");
        recipe.setFont(new Font("Consolas", 13));

        recipe.setWrapText(true);
        recipe.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().add(recipe);
        StackPane.setAlignment(recipe,Pos.BOTTOM_LEFT);
        StackPane.setMargin(recipe, new Insets(0,0,5,0));
        recipe.setPadding(new Insets(0,5,0,5));
        recipe.setAlignment(Pos.CENTER);
        if( !(order.getGarnish() == null || order.getGarnish().isEmpty()) )
        {
            recipeText += "\nGarnish:\n- " + order.getGarnishDescription();
        }
        recipeText = recipeText.replace(", ", "\n- ");
        recipe.setText(recipeText);
        Rectangle backing = (Rectangle) card.getChildren().get(0);
        backing.setFill(Color.rgb(255,255,150));
        backing.setStroke(Color.LIGHTBLUE);
        recipe.setMaxWidth(backing.getWidth());

        Label nameLabel = (Label) card.getChildren().get(2);
        nameLabel.getStyleClass().add("customerRecipe");
        nameLabel.setFont(new Font("Consolas", 15));

        return card;
    }

    private ScrollPane makePlayerName(String name, double maxWidth)
    {
        Label nameLabel = new Label(name);
        ScrollPane scrollPane = new ScrollPane(nameLabel);
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        scrollPane.setMaxWidth(maxWidth);
        scrollPane.setMaxHeight(nameLabel.getHeight()+15);
        return scrollPane;
    }

    public void updateCurrentPlayer()
    {
        currentPlayer.setText("Current Player: " + bakery.getCurrentPlayer().toString());
        playerScroll.setMaxWidth(currentPlayer.getScene().getWidth()/3);
    }
    public void updateActionsLeft()
    {
        actionsLeft.setText(bakery.getActionsRemaining() + "/" + bakery.getActionsPermitted() + "Actions left");
    }

    private double calculateCardHeight()
    {
        return handRow.getScene().getHeight() / 6 + 20;
    }
}
