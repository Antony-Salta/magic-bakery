package gui;

import bakery.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javafx.animation.*;
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
import javafx.stage.Stage;
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
    private StackPane currentHandPane;

    @FXML
    private VBox leftHands;

    @FXML
    private VBox rightHands;

    @FXML
    private VBox mainLayout;

    @FXML
    private VBox messageBox;

    @FXML
    private Label customerStatus;

    @FXML StackPane helpBox;
    @FXML Button closePrompt;


    //This is going to be for a very weird thing to do with dragging cards to pass to other players.
    private StackPane draggedHand;

    private int playerIndex = 1;

    private final DropShadow yellowHighlight = new DropShadow(5,0,5, Color.YELLOW);
    private final DropShadow blueHighlight = new DropShadow(5,0,5, Color.BLUE);
    private final DropShadow greenHighlight = new DropShadow(5,0,5, Color.GREEN);

    private Image logo = new Image("file:../../images/KJMB_Logo.png");

    private final DataFormat ingredientFormat = new DataFormat("bakery.Ingredient");



    /**
     * Redraws all rows, generally for after an action.
     *
     * The other hands will not be redrawn with this.
     * updateCurrentPlayer and updateActions left aren't called, since that will always be done in handleTurnEnd
     */
    private void drawRows()
    {
        drawCustomers();
        drawLayers();
        drawPantry();
        drawHand();
    }

    /**
     * This sets up the scene and the needed instance variables. It will be called by StartHandler at the beginning of this handler being instantiated.
     * @param bakery the bakery object passed in from the startHandler
     */
    public void setup(MagicBakery bakery)
    {
        this.bakery = bakery;
        drawOtherHands();
        drawRows();

        updateActionsLeft();
        updateCurrentPlayer();
        updateCustomerStatus();

        // This section makes sure that the help message is centred, and they can't play the game while the message is up, since most of it will be blocked anyway
        Stage stage = (Stage) helpBox.getScene().getWindow();
        VBox blocker = (VBox) helpBox.getParent();
        blocker.setPrefWidth(stage.getWidth());
        blocker.setPrefHeight(stage.getHeight());
    }

    /**
     * Gives a file display system to allow the user to choose the file where they want to save their game.
     * From there, they can just choose to save it.
     */
    @FXML
    public void saveGame()
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose save file");
        chooser.setInitialDirectory(new File("../../"));
        File save = chooser.showOpenDialog(currentPlayer.getScene().getWindow());
        try
        {
            bakery.saveState(save);
            FadeTransition saveFade = makeFadingMessage("Game Saved!", null);
            saveFade.play();
            saveFade.setOnFinished(e -> deleteMessage(e));
        }

        catch (IOException IOe){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error writing to file.");
            alert.setContentText("This file could not be written to. Try saving and closing any unneeded files.");
            alert.show();
            IOe.printStackTrace();
        }
    }

    /**
     * 'Closes' the help prompt so that it isn't on screen
     */
    @FXML
    public void closePrompt()
    {
        helpBox.setVisible(false);
        helpBox.getParent().toBack();
    }

    /**
     * 'Opens' the help prompt so that it is visible on screen
     */
    @FXML
    public void showHelp()
    {
        helpBox.getParent().toFront();
        helpBox.setMaxWidth(((Label) helpBox.getChildren().get(0)).getWidth());
        helpBox.setVisible(true);
    }

    /**
     * Creates a message that will appear on screen and fade out
     * @param message the message to be displayed
     * @param duration this will default to 3000ms
     * @return the transition, so that it can be played and a setOnFinished method set.
     */
    private FadeTransition makeFadingMessage(String message, Duration duration)
    {
        if(duration == null)
            duration = Duration.millis(3000);

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setFont(new Font(48));
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setBackground(new Background(new BackgroundFill( Color.color(0d,0d,0d,0.5), null, null)));
        messageBox.getChildren().clear();
        messageBox.getChildren().add(messageLabel);
        messageBox.toFront();
        Scene scene = messageBox.getScene();
        messageBox.setPrefWidth(scene.getWidth());
        messageBox.setPrefHeight(scene.getHeight());
        messageLabel.setMaxWidth(scene.getWidth()/2);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(3000), messageLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        messageLabel.setAccessibleText(messageLabel.getText());
        return fadeOut;
    }

    /**
     * Specifically deletes the message from a fading message, just to reduce code duplication when setting the OnFinished for the animations.
     * Some usages may want to more than just delete the message at the end, so the setOnFinished cannot be set immediately.
     * @param event the event of the transition ending
     */
    private void deleteMessage(ActionEvent event)
    {
        Node origin = ((FadeTransition) event.getSource()).getNode();
        messageBox.getChildren().remove(origin);
        messageBox.toBack();
    }

    /**
     * Draws a card from the pantry and makes an associated animation
     * @param event The event of a pantry card being clicked on
     */
    private void drawFromPantry(MouseEvent event)
    {
        StackPane card = (StackPane) event.getSource();
        String name = ((Label) card.getChildren().get(2)).getText();
        bakery.drawFromPantry(name);

        TranslateTransition cardMove = animateNodeToNode(card, currentHandPane, null);
        cardMove.setOnFinished(e -> {
            if (handleTurnEnd())
                drawPantry();
            else
                drawRows();
        });
        cardMove.play();
    }

    /**
     * Draws a card from the pantry deck and makes an associated animation of it going to hand (which is messier than the other animations for fun reasons)
     * @param event the event of the pantry deck card being clicked
     */
    private void drawFromPantryDeck(MouseEvent event)
    {
        ArrayList<Ingredient> prevHand = new ArrayList<>(bakery.getCurrentPlayer().getHand());

        bakery.drawFromPantry((Ingredient) null);
        ArrayList<Ingredient> currentHand = new ArrayList<>(bakery.getCurrentPlayer().getHand());
        int indexOfNew = prevHand.size();
        //getHand gives a sorted list, so the player's hand before and after drawing a card will be the same, until it hits the newly inserted card, which can be considered to be at the end of the block of ingredients that are the same as it.
        for (int i = 0; i < prevHand.size(); i++) {
            if(!prevHand.get(i).equals(currentHand.get(i)))
            {
                indexOfNew = i;
                break;
            }
        }

        drawHand();
        drawPantry();

        //This has to be reversed because this animation can only happen after everything is redrawn
        StackPane card = (StackPane) currentHandPane.getChildren().get(indexOfNew);

        //This wait thing just makes sure that everything is actually drawn and has a width before the rest happens
        PauseTransition wait = new PauseTransition(Duration.millis(20));
        wait.setOnFinished(e -> drawCardAnimation(card));
        wait.play();

    }

    /**
     * Used in the drawCardFromPantry.
     * This will animate the card coming from the pantry deck, and then run handleTurnEnd as part of this being an action that the player takes
     * @param card, the card that is being animated from the pantry deck
     */
    private void drawCardAnimation(StackPane card)
    {
        //This just clears this stuff while it's moving, to stop weird animation issues.
        EventHandler<? super MouseEvent> hoverUp =  card.getOnMouseEntered();
        EventHandler<? super MouseEvent> hoverDown =  card.getOnMouseExited();
        card.setOnMouseEntered(null);
        card.setOnMouseExited(null);
        TranslateTransition reverseDraw = animateNodeToNode(card, pantryRow.getChildren().get(1), null);
        //The rate is -1, because the card needs to look like it's going to the hand from the deck, when its original position is actually the hand.
        reverseDraw.setRate(-1);
        reverseDraw.setOnFinished(e ->
            {
                card.setOnMouseEntered(hoverUp);
                card.setOnMouseExited(hoverDown);
                if(!handleTurnEnd())
                {
                    drawCustomers();
                    drawLayers();
                    drawHand();
                }
            });
        reverseDraw.play();
    }

    /**
     * This is called after a layer has been baked in the game logic, and makes associated animations of cards leaving and entering the player's hand.
     * @param event The event of the layer card being clicked
     * @param usedIngredients The ingredients used to create the layer, which have to be animated from the hand going back to the pantry deck.
     */
    private void bakeLayer(MouseEvent event, List<Ingredient> usedIngredients)
    {
        StackPane card = (StackPane) event.getSource();
        TranslateTransition cardMove = animateNodeToNode(card, currentHandPane, null);
        cardMove.setOnFinished(e ->{
                if(handleTurnEnd())
                    drawPantry(); //the pantry could technically need to be redrawn if the pantry deck was empty before
                else
                    drawRows();
            });

        ParallelTransition usedCardAnimations = animateUsedCards(usedIngredients);
        usedCardAnimations.play();
        cardMove.play();
    }

    private ParallelTransition animateUsedCards(List<Ingredient> usedIngredients)
    {
        ParallelTransition usedCardAnimations = new ParallelTransition();

        boolean recipeHasLayers = false;
        for (Ingredient ingredient: usedIngredients)
        {
            if( ingredient instanceof Layer){
                recipeHasLayers = true;
                break;
            }
        }

        for(Node ingredientNode :  currentHandPane.getChildren()) {
            StackPane ingredientCard = (StackPane) ingredientNode;
            String name = ((Label) ingredientCard.getChildren().get(2)).getText();
            Ingredient temp = null;

            //Now follows some awful ways of figuring out if a card is a layer or ingredient, and doing string parsing to recreate the layer if so
            if( ((Rectangle) ingredientCard.getChildren().get(0)).getStroke().equals(Color.GOLD))
                temp = new Ingredient(name.toLowerCase());
            else if(recipeHasLayers) // so if this whole thing even has to be done.
            {
                List<Ingredient> recipe = new ArrayList<>();
                String recipeString = ((Label) ingredientCard.getChildren().get(3)).getText();
                String[] ingredients = recipeString.split(", ");
                for (String ingredient: ingredients)
                {
                    //This regex will get the name and the number in the x2 if it's there, although I haven't seen a layer that needs 2 of any ingredient.
                    Pattern pattern = Pattern.compile("(.+) \\(x(\\d+)\\)");
                    Matcher matcher = pattern.matcher(ingredient);
                    if(matcher.find())
                    {
                        String actualName = matcher.group(1);
                        int quantity = Integer.parseInt(matcher.group(2));

                        for (int i = 0; i < quantity; i++) {
                            recipe.add(new Ingredient(actualName));
                        }
                    }
                    else
                        recipe.add(new Ingredient(ingredient));

                }
                temp = new Layer(name.toLowerCase(), recipe);
            }
            else if(!recipeHasLayers)
                continue; //Skip the layer card in the hand if the recipe doesn't need a layer


            if(usedIngredients.isEmpty())
                break;
            if (usedIngredients.contains(temp))
            {
                Node destination;
                if(temp instanceof Ingredient)
                    destination = pantryRow.getChildren().get(1);
                else
                    destination = layerRow;
                usedCardAnimations.getChildren().add(animateNodeToNode(ingredientCard, destination, null));
                usedIngredients.remove(temp);
            }

        }
        // now the clean-up round on helpful ducks
        if(!usedIngredients.isEmpty())
        {
            for(Node ingredientNode :  currentHandPane.getChildren()) {
                StackPane ingredientCard = (StackPane) ingredientNode;
                String name = ((Label) ingredientCard.getChildren().get(2)).getText();
                if(name.contains("helpful duck")) // not pasting the whole thing because I have precautions around the duck character not working on all platforms.
                {
                    usedCardAnimations.getChildren().add(animateNodeToNode(ingredientCard, pantryRow.getChildren().get(1), null));
                    usedIngredients.remove(0);
                }
            }
        }
        return usedCardAnimations;
    }

    /**
     * Uses animate node, and the bounding boxes of the source and destination node to animate one object to another.
     * @param source the node that is being moved
     * @param destination the node that the source is moving to
     * @param duration default duration time is 500ms if null is passed in, otherwise dictates how long the animation takes to complete.
     * @return the TranslateTransition giving the animation.
     */
    private TranslateTransition animateNodeToNode(Node source, Node destination, Duration duration)
    {
        if(duration == null)
        {
            duration = Duration.millis(500);
        }

        Bounds sourceBound = source.localToScene(source.getLayoutBounds());
        double[] from = {sourceBound.getCenterX(), sourceBound.getCenterY()};
        Bounds destinationBound = destination.localToScene(destination.getLayoutBounds());
        double[] to = {destinationBound.getCenterX(), destinationBound.getCenterY()};
        TranslateTransition movingCard = animateNode(source, from, to, duration);
        return movingCard;
    }



    /**
     * Makes a translate transition that moves a node from one set of coordinates to another, because this is going to be needed a lot in animations
     * @param source the Mode that is being moved
     * @param from The coordinate that it is moving from, this should be an array of length 2, with the center x and then center y coordinate
     * @param to the coordinate that it is moving to, in the same format of the from variable
     * @param duration the duration of the animation, which will default to 500ms if null is passed in
     * @return the translate transition of this, with a default duration of 500ms
     */
    private TranslateTransition animateNode(Node source, double[] from, double[] to, Duration duration)
    {
        if(duration == null)
            duration = Duration.millis(500);

        TranslateTransition move = new TranslateTransition(duration, source);
        move.setFromX(0);
        move.setFromY(0);
        move.setToX(to[0] - from[0]);
        move.setToY(to[1] - from[1]);
        return move;
    }

    /**
     * Effectively just does the same thing as refresh pantry in the game logic, with the extra handling needed for the UI.
     */
    @FXML
    public void refreshPantry()
    {
        bakery.refreshPantry();
        handleTurnEnd();
        drawPantry();

    }

    /**
     * This  should be called after any action. It will check if the current player's turn has ended, and then check if the round has ended
     * It will update the bakery methods as needed
     * It will redraw everything that needs to be redrawn if it is the end of a player's turn, which is everything but the pantry row.
     * (This could change if I make a separate setEffects method for layers and customers, but that's just optimisation)
     * It will always update the number of actions left
     * It will always update the currentPlayer when needed
     * It will update customerStatus if it is the end of a round.
     * At the end of a round, a message will be displayed, stating that a new round has started.
     * @return whether it is the end of a round, and therefore if everything but the pantry row has been redrawn
     */
    private boolean handleTurnEnd()
    {
        boolean gameOver = false;
        boolean turnEnd = false;
        boolean newRound = false;

        if(bakery.getActionsRemaining() == 0)
        {
            boolean wasDeckEmpty = bakery.getCustomers().getCustomerDeck().isEmpty();
            turnEnd = true;
            ParallelTransition animations = new ParallelTransition();

            if(bakery.endTurn())
            {
                Customers customers = bakery.getCustomers();
                newRound = true;

                boolean timeToStop = false;
                boolean hitCustomer = false;
                int i =1;
                //This loop makes all the animations needed to move customers along if it's the end of a round
                while( i < customerRow.getChildren().size() -1 &&  timeToStop == false)
                {
                    StackPane customerCard = (StackPane) customerRow.getChildren().get(i);
                    if( !((Label) customerCard.getChildren().get(2)).getText().equals("Customer order")) // so if there's an actual order there
                    {//If the deck is empty, then all cards animate to the right
                        double[] from = {0,0};
                        double[] to = {customerCard.getWidth() + customerRow.getSpacing(),0};
                        SequentialTransition delayedTransition = new SequentialTransition(
                                new PauseTransition(Duration.millis(100)),
                                animateNode(customerCard, from, to, null)
                        );
                        animations.getChildren().add(delayedTransition);
                        hitCustomer = true;
                    }
                    else
                    {
                        customerCard.setVisible(false);
                        if(!customers.getCustomerDeck().isEmpty() || hitCustomer) // if the deck isn't empty, then cards only animate right as long as there's no place without an order. If there's a customer before this, then always stop
                            timeToStop = true;
                    }
                    i++;
                }


                updateCustomerStatus();


                //If the game is ending.
                //TODO: put in separate function and prettify.
                if(customers.isEmpty() && customers.getCustomerDeck().isEmpty())
                {
                    gameOver =true;
                    AnchorPane root = (AnchorPane) customerRow.getScene().getRoot();
                    root.getChildren().clear();
                    int numCompleted = customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.GARNISHED).size() + customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.FULFILLED).size();
                    String ranking = "";
                    if(numCompleted >=7)
                        ranking = "GOLD";
                    else if(numCompleted >= 5)
                        ranking = "SILVER";
                    else if(numCompleted >= 3)
                        ranking = "BRONZE";
                    else
                        ranking = "NONE  :(";
                    String endMessage = "Game Over!\nRanking:\n" + ranking;
                    Label end = new Label(endMessage);
                    end.setFont(new Font("Verdana", 48));
                    end.setAlignment(Pos.CENTER);
                    end.setPrefHeight(root.getHeight());
                    end.setPrefWidth(root.getWidth());
                    end.setTextFill(Color.WHITE);
                    root.getChildren().add(end);
                }
            }
            if(!gameOver)
            {
                //this function will do all of the redrawing that is needed.
                if(newRound)
                {
                    FadeTransition roundMessage = makeFadingMessage("New round starting", Duration.millis(1500));

                    roundMessage.setOnFinished(e ->{
                            deleteMessage(e);
                            moveHandsAround(); // This will redraw everything that needs redrawing
                            animateDrawnCustomer(wasDeckEmpty);
                            playerIndex = (playerIndex +1) % bakery.getPlayers().size();
                        });
                    animations.getChildren().add(roundMessage);
                    animations.play();

                }
                else
                {
                    moveHandsAround();
                    playerIndex = (playerIndex +1) % bakery.getPlayers().size();
                /*This player index thing makes it so that the position of the hands is determined by how close they are to playing.
                The order is that the next player will be top left, then top right, then bottom left, then bottom right.
                */
                }

            }

        }
        if(!gameOver)
            updateActionsLeft();
        return turnEnd;

    }

    /**
     * Will animate the leftmost card as coming from the customer deck if that is appropriate.
     * This has to be called after everything is redrawn, otherwise it won't get the right customer
     * @param wasDeckEmpty Whether the deck was empty before the new round started
     */
    private void animateDrawnCustomer(boolean wasDeckEmpty)
    {
        if(!wasDeckEmpty) // This will animate the card that was just drawn from the deck, as coming from the deck
        {
            StackPane customerCard = (StackPane) customerRow.getChildren().get(1); //Only the leftmost customer order can be just drawn, since that is where it will always go
            if (!((Label) customerCard.getChildren().get(2)).getText().equals("Customer order")) // so if there's an actual order there
            {
                double[] from = {0,0};
                double[] to = {- calculateCardHeight() * 2/3 - customerRow.getSpacing(),0};
                TranslateTransition customerDraw = animateNode(customerCard,from,to, null);
                customerDraw.setRate(-1);
                customerDraw.play();
            }
        }
    }

    /**
     * This will move the hands around to show how the order goes, and each player can follow the new position of their hand.
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

        Bounds currentBound = currentHandPane.localToScene(currentHandPane.getLayoutBounds());
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
        hands[numPlayers-1] = currentHandPane;



        TranslateTransition[] moves = new TranslateTransition[numPlayers];
        RotateTransition[] rotates = new RotateTransition[numPlayers];
        for (int i = 0; i < numPlayers; i++)
        {
            //This will use the moveNode code in reverse
            TranslateTransition moveHand = animateNode(hands[i], coords[i], coords[(i+1) % numPlayers], Duration.millis(1500) );
            moveHand.setRate(-1);


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

    /**
     * Brings up two buttons asking if the user would like to fulfil or garnish their order, with the buttons being able to implement the needed logic.
     * @param event the event of a garnishable customer card being clicked on
     * @param order The associated CustomerOrder with the card that's been clicked.
     */
    private void askFulfilOrGarnish(MouseEvent event, CustomerOrder order)
    {
        StackPane card = (StackPane) event.getSource();
        Button fulfil = new Button("Fulfil?");
        Button garnish = new Button("Garnish?");
        garnish.getStyleClass().add("customerButton");
        fulfil.getStyleClass().add("customerButton");
        card.getChildren().addAll(fulfil,garnish);
        StackPane.setAlignment(fulfil,Pos.CENTER);
        StackPane.setAlignment(garnish,Pos.BOTTOM_CENTER);
        StackPane.setMargin(fulfil, new Insets(0, 0, garnish.getHeight()/2, 0));
        StackPane.setMargin(garnish, new Insets(fulfil.getHeight()/2, 0, 0, 0));
        fulfil.setOnAction(e -> fulfilOrder(card, order, false));
        garnish.setOnAction(e -> fulfilOrder(card, order, true));
    }

    /**
     * Animates the selected card as going to the discard pile, then on finish calls finishFulfilOrder,
     * which handles the game logic and animations of the player's card.
     * @param card The card that will be animated going to the discard pile
     * @param order the CustomerOrder that is being fulfilled
     * @param garnish Whether the order is being garnished or not
     */
    private void fulfilOrder(StackPane card, CustomerOrder order, boolean garnish) {
        //TODO: fix animation issue where the card carries on moving afterwards, maybe just delete it here
        TranslateTransition toDeck = animateNodeToNode(card, customerRow.getChildren().get(customerRow.getChildren().size()-1), null);
        toDeck.setOnFinished(e ->
                {
                    customerRow.getChildren().remove(card);
                    finishFulfilOrder(order,garnish);
                });
        toDeck.play();
    }

    /**
     * Calls the bakery's fulfillOrder method, and then animates the cards drawn from garnish going to the player's hand.
     * It will also use AnimateUsedCards to animate the used c cards as going into the pantry deck.
     * @param order the CustomerOrder that is being fulfilled
     * @param garnish whether the order is being garnished
     */
    private void finishFulfilOrder(CustomerOrder order, boolean garnish)
    {
        List<Ingredient> newCards =  bakery.fulfillOrder(order, garnish);
        ParallelTransition animations = new ParallelTransition();
        // This chunk of code will figure out where the cards drawn when garnishing come from, so that it can be animated.
        if (garnish) {


            for (int i = 2; i < pantryRow.getChildren().size(); i++) {
                if(newCards.isEmpty())
                    break;
                StackPane card = (StackPane) pantryRow.getChildren().get(i);
                String name = ((Label) card.getChildren().get(2)).getText();

                for (int j = 0; j < newCards.size(); j++) {
                    if (newCards.get(j).toString().equals(name)) {
                        animations.getChildren().add(animateNodeToNode(card, currentHandPane, null));
                        newCards.remove(j);
                        break;
                    }
                }

            }
            ParallelTransition usedCardAnimations = animateUsedCards(order.getGarnish());
            animations.getChildren().addAll( usedCardAnimations.getChildren());


        }


        updateCustomerStatus();

        ParallelTransition usedCardAnimations = animateUsedCards(order.getRecipe());
        animations.getChildren().addAll( usedCardAnimations.getChildren());
        animations.setOnFinished(e ->
        {
            if(handleTurnEnd())
                drawPantry(); // can technically change by putting ingredients back in when the pantry deck is emptied.
            else
                drawRows();
        });
        animations.play();
    }

    /**
     * Draws the elements in the customer row according to the game state. This will be the customer deck, up to 3 orders, and a discard pile.
     * It gives appropriate event handlers and highlights to fulfil or garnish orders, if it's possible for the current player
     */
    private void drawCustomers()
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

        for (int i = 0; i < 3-activeCustomers.size(); i++) {
            drawCardSlot(customerRow, "Customer order");
        }

        Collection<CustomerOrder> fulfilable = bakery.getFulfilableCustomers();
        Collection<CustomerOrder> garnishable = bakery.getGarnishableCustomers();
        Iterator<CustomerOrder> iterator = (
                (LinkedList<CustomerOrder>) activeCustomers
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
                    card.setOnMouseClicked(e ->askFulfilOrGarnish(e, order));
                }
                else if(fulfilable.contains(order))
                {
                    card.setEffect(blueHighlight);
                    card.setOnMouseClicked(e -> fulfilOrder(card, order, false));
                }
            }
        }
        if(customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.GARNISHED).isEmpty() && customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.FULFILLED).isEmpty() && customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.GIVEN_UP).isEmpty())
        {
            drawCardSlot(customerRow, "Customer Discard");
        }
        else
        {
            StackPane card = makeStackCard("Customer");

            Rectangle backing = (Rectangle) card.getChildren().get(0);
            backing.setFill(Color.LIGHTBLUE);
            backing.setStroke(Color.WHITE);

            customerRow.getChildren().add(card);
        }
    }

    /**
     * Draws the layer row based on the game state.
     * Layers will have the appropriate event handlers and highlights if they are bakeable by the current player.
     */
    private void drawLayers()
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
                card.setOnMouseClicked(e ->
                {
                    bakery.bakeLayer(layer);
                    bakeLayer(e, new ArrayList<>(layer.getRecipe()) ); //need to make the copy so that it doesn't wipe the other recipes
                });
            }

        }
    }

    /**
     * Draws the pantry, with a refresh pantry button, pantry deck, and 5 pantry cards.
     * There is no pantry discard, since it just gets shuffled back into the pantry deck, and there's no actual person that needs to manage the cards, and there are very few public methods to read the state of discard.
     */
    private void drawPantry()
    {
        pantryRow.getChildren().removeIf(node -> node instanceof StackPane); //keep the refresh pantry button
        StackPane stackCard = makeStackCard("Ingredient");
        Rectangle stackBacking = (Rectangle) stackCard.getChildren().get(0);
        stackBacking.setFill(Color.GOLD);
        stackBacking.setStroke(Color.WHITE);
        pantryRow.getChildren().add(stackCard);
        stackCard.setOnMouseClicked(e -> drawFromPantryDeck(e));
        for(Ingredient ingredient : bakery.getPantry())
        {
            StackPane card = makeNamedCard(ingredient.toString());
            Rectangle backing = (Rectangle) card.getChildren().get(0);
            backing.setFill(Color.WHITE);
            backing.setStroke(Color.GOLD);
            card.setOnMouseClicked(e -> drawFromPantry(e));

            pantryRow.getChildren().add(card);
        }
    }

    /**
     * Draws the current player's hand, giving events when the mouse is hovered over, and to drag and drop cards from one hand to another.
     */
    private void drawHand()
    {
        handRow.getChildren().clear();

        double maxWidth = handRow.getScene().getWidth()/2;
        currentHandPane = makePlayerHand(bakery.getCurrentPlayer(),maxWidth);
        handRow.getChildren().add(currentHandPane);
    }

    /**
     * Draws the other player's hands, which is similar to drawing the current player's hand, but without drag and drop being allowed.
     * Instead, cards can be dragged into these hands
     */
    private void drawOtherHands()
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

                handPane.setOnDragOver(e ->
                {
                        if(e.getDragboard().hasContent(ingredientFormat))
                            e.acceptTransferModes(TransferMode.MOVE);
                        e.consume();
                });

                handPane.setOnDragEntered(e ->
                {
                        for( Node card :  handPane.getChildren())
                        {
                            ((Rectangle) ((StackPane)card).getChildren().get(0)).setEffect(new ColorAdjust(0.3, 0.3, 0, 0.5));
                        }
                        e.consume();
                });
                handPane.setOnDragExited(e ->
                {
                        for( Node card :  handPane.getChildren())
                        {
                            ((Rectangle) ((StackPane)card).getChildren().get(0)).setEffect(new ColorAdjust(0, 0, 0, 0));
                        }
                        e.consume();
                });
                //This method does the main handling for the game logic of the drag being finished, passing a card to another hand after getting the dragboard content.
                handPane.setOnDragDropped(event ->
                {
                        boolean dragDropWorked = false;
                        if(event.getDragboard().hasContent(ingredientFormat))
                        {
                            draggedHand = (StackPane) event.getSource();

                            Ingredient passedCard = (Ingredient) event.getDragboard().getContent(ingredientFormat);
                            bakery.passCard(passedCard,player);
                            dragDropWorked = true;
                        }

                        event.setDropCompleted(dragDropWorked);
                        event.consume();
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


    }

    /**
     * This will make a hand, since it is very similar code for all of them. There is different functionality implemented for the current player's hand compared to the others
     * since cards can be dragged from the current player's hand, into another player's hand.
     * @param player Makes the stack pane of the specified player's hand
     * @param maxWidth the max width that the hand can take up
     * @return the stack pane, that is the player's hand
     */
    private StackPane makePlayerHand(Player player, double maxWidth)
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
            card.setOnMouseEntered(e ->
            {
                    Duration time = hover.getCurrentTime();
                    hover.setRate(1);
                    hover.playFrom(time);
                    card.toFront();
            });
            card.setOnMouseExited(e ->
            {
                    Duration time = hover.getCurrentTime();
                    hover.setRate(-1);
                    hover.playFrom(time);
                    handPane.getChildren().remove(handPane.getChildren().size()-1); // Weird screwing with the children list to put the card back in the right spot in terms of being ahead and behind other cards.
                    handPane.getChildren().add(originalIndex,card);
            });

            if(player.equals(bakery.getCurrentPlayer()))
            { //Give the ability to pass cards
                card.setOnDragDetected(event ->
                {
                        Dragboard db = card.startDragAndDrop(TransferMode.MOVE);

                        ClipboardContent content = new ClipboardContent();
                        content.put(ingredientFormat, ingredient);
                        db.setContent(content);
                        event.consume();
                });

                card.setOnDragDone(e -> handleDragDone(e));
            }

        }
        return handPane;
    }

    /**
     * This code will see when a drag-drop has finished, and do the necessary animation for the card moving from one hand to another.
     * @param event the event of a dragged card being dropped into a valid place.
     */
    private void handleDragDone(DragEvent event)
    {
        if(event.getTransferMode() == TransferMode.MOVE)
        { // If the drag drop has worked
            StackPane card = (StackPane) event.getSource();
            TranslateTransition moveCard = animateNodeToNode(card, draggedHand, Duration.millis(800));

            moveCard.setOnFinished(e ->
            {
                    if(!handleTurnEnd())
                    {
                        drawCustomers();
                        drawLayers();
                        drawHand();
                        drawOtherHands();
                    }
                    draggedHand = null;
            });

            RotateTransition spin = new RotateTransition(Duration.millis(500), (Node) event.getSource());
            if(moveCard.getToX() > 0)
                spin.setByAngle(-90);
            else
                spin.setByAngle(90);

            spin.play();
            moveCard.play();
        }
        event.consume();
    }

    /**
     * Generates a card that is translucent to show where a card should be going. Uses makeBasicCard, then adds another label to indicate the name of the slot
     * So the children of this StackPane will be the Rectangle backing, then an empty ImageView, then a centred Label
     * @param row the row that this card is to be inserted into
     * @param name the name given to this card
     */
    private void drawCardSlot(HBox row, String name)
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

        card.setAccessibleText(name + " slot");
        card.setAccessibleHelp("This is a slot where a card can go, depending on how the game goes.");

    }

    /**
     * This will be used to make the basic card shape, with an image in the middle
     * @return a stackPane with the basic card shape and format. The pane will have its first child be the backing rectangle, and the second the centre image
     */
    private StackPane makeBasicCard(Image image)
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

    /**
     * Makes a card that is the top of a stack of cards, e.g. ingredient or the customer deck. It will make a basic card, then add on a centred label
     * So the children are a backing rectangle, centre image, then a centred label.
     * @param name the name to be put on the card
     * @return the card that is made
     */
    private StackPane makeStackCard(String name)
    {
        StackPane card = makeBasicCard(logo);
        Label stackName = new Label(name);
        stackName.setWrapText(true);
        stackName.setAlignment(Pos.CENTER);
        stackName.setMaxWidth(((Rectangle) card.getChildren().get(0)).getWidth());
        card.getChildren().add(stackName);
        StackPane.setAlignment(stackName, Pos.BOTTOM_CENTER);
        StackPane.setMargin(stackName,new Insets(0,0,10,0));
        card.setAccessibleText(name + " deck");
        card.setAccessibleHelp("This is a deck of " + name + " cards");
        return card;
    }

    /**
     * Makes a card based on the makeBasicCard with a name at the top
     * @param name the name to go at the top of the card
     * @return a stackPane that with the children: rectangle, centre image, name label aligned at the top
     */
    private StackPane makeNamedCard(String name)
    {
        StackPane card = makeBasicCard(new Image("file:../../images/" + name + ".png"));
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

    /**
     * Makes an ingredient card based on the named card creation
     * @param name the name of the ingredient
     * @return a stackPane that with the children: rectangle, centre image, name label aligned at the top
     */
    private StackPane makeIngredientCard(String name)
    {
        StackPane card = makeNamedCard(name);
        Rectangle backing = (Rectangle) card.getChildren().get(0);
        backing.setFill(Color.WHITE);
        backing.setStroke(Color.GOLD);
        card.setAccessibleText("Ingredient: " + name);
        card.setAccessibleHelp("This is an ingredient card, which you will use to make layers, and ultimately fulfil orders");
        return card;
    }

    /**
     * This method will use the makeNamedCard to make a card as specified in that function, with a recipe displayed at the bottom
     * @param layer the layer that this card will be of
     * @return a StackPane representing a card with the children: backing rectangle, centre image, name label at the top, recipe label at the bottom
     */
    private StackPane makeLayerCard(Layer layer)
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
        card.setAccessibleText("Layer: " + layer.toString() + recipe.getText());
        card.setAccessibleHelp("This is a layer card, which you make with ingredients to help fulfil orders that come in.");
        return card;
    }
    /**
     * This method will use the makeNamedCard to make a card as specified in that function, with a recipe displayed at the bottom
     * @param order the customer order that this card will be of
     * @return a StackPane representing a card with the children: backing rectangle, centre image, name label at the top, recipe label at the bottom
     */
    private StackPane makeCustomerCard(CustomerOrder order)
    {
        StackPane card = makeNamedCard(order.toString());
        Label recipe = new Label();
        String recipeText = "Recipe:\n- " + order.getRecipeDescription();
        recipe.getStyleClass().addAll("customerCard","customerRecipe");

        recipe.setWrapText(true);
        recipe.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().add(recipe);
        StackPane.setAlignment(recipe,Pos.BOTTOM_LEFT);
        StackPane.setMargin(recipe, new Insets(0,0,5,0));
        recipe.setPadding(new Insets(0,10,0,10));
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
        nameLabel.getStyleClass().addAll("customerCard", "customerName");
        card.setAccessibleText("Customer order: " + order.toString() + " " + recipeText);
        card.setAccessibleHelp("This is a customer card, which you should try to fulfil by getting the needed ingredients in the recipe, and garnish if you can.");
        return card;
    }

    /**
     * Makes a scroll pane with a label inside, used when making the name labels for other players.
     * @param name The name of the player
     * @param maxWidth the max width of the scroll pane
     * @return the scroll pane
     */
    private ScrollPane makePlayerName(String name, double maxWidth)
    {
        Label nameLabel = new Label(name);
        nameLabel.setAlignment(Pos.TOP_CENTER);
        ScrollPane scrollPane = new ScrollPane(nameLabel);
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        scrollPane.setMaxWidth(maxWidth);
        scrollPane.setMaxHeight(nameLabel.getHeight()+40);
        return scrollPane;
    }

    /**
     * updates the name in the currentPlayer field.
     */
    private void updateCurrentPlayer()
    {
        currentPlayer.setText("Current Player: " + bakery.getCurrentPlayer().toString());
        playerScroll.setMaxWidth(currentPlayer.getScene().getWidth()/6);
        currentPlayer.setAccessibleText(currentPlayer.getText());
    }

    /**
     * Updates the number of actions being shown as being left.
     */
    private void updateActionsLeft()
    {
        actionsLeft.setText(bakery.getActionsRemaining() + "/" + bakery.getActionsPermitted() + "Actions left");
        actionsLeft.setAccessibleText(actionsLeft.getText());
    }

    /**
     * updates the customer status, so the number that have been fulfilled, garnished, and how many have given up.
     */
    private  void updateCustomerStatus()
    {
        int fulfilledCustomers, garnishedCustomers, givenUpCustomers;
        Customers customers = bakery.getCustomers();
        fulfilledCustomers = customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.FULFILLED).size();
        garnishedCustomers = customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.GARNISHED).size();
        givenUpCustomers = customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.GIVEN_UP).size();
        String status = "Customers fulfiled (and garnished) : " + (fulfilledCustomers + garnishedCustomers) + "(" + garnishedCustomers + "). \nCustomers that have given up: " + givenUpCustomers;
        customerStatus.setText(status);
        customerStatus.setAccessibleText(customerStatus.getText());
    }


    /**
     * Calculates how tall cards should be, based on the height of the scene.
     * @return The height in pixel that cards should be
     */
    private double calculateCardHeight()
    {
        return handRow.getScene().getHeight() / 6 + 20;
    }
}
