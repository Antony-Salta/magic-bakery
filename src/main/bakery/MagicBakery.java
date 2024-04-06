package bakery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import bakery.CustomerOrder.CustomerOrderStatus;
import util.CardUtils;
import util.ConsoleUtils;
import util.StringUtils;

/**
 * This is the class that will store the state of the game, and change various parts of it as the game plays out.
 * It will track all of the different sets of cards that need to be managed in the game, like the layers, customers, pantry.
 * It will also track things to do with the players, like the number of actions left in a turn and the current player.
 * @author Antony Salta
 * @version 1.0
 *
 * This isn't the correct version number, but I haven't been tracking until now
 */
public class MagicBakery implements Serializable{
    private Customers customers;
    private Collection<Layer> layers;
    private Collection<Player> players;
    private Collection<Ingredient> pantry;
    private Collection<Ingredient> pantryDeck; 
    private Collection<Ingredient> pantryDiscard;
    private Random random;
    private static final long serialVersionUID =1;

    private Player currentPlayer;
    private int actionsLeft;

    /**
     * This enum gives the different options that the players can take during their turn
     * @author Antony Salta
     * @version 1.0
     *
     * This isn't the correct version number, but I haven't been tracking until now
     */
    public enum ActionType
    {
        DRAW_INGREDIENT,
        PASS_INGREDIENT,
        BAKE_LAYER,
        FULFIL_ORDER,
        REFRESH_PANTRY

    }
    /**
     * This constructor makes a MagicBakery object, which tracks the whole state of the game as it is played.
     * @param seed the seed used to instantiate the Random object with a specific seed.
     * @param ingredientDeckFile The string giving the path to the file containing all of the ingredients
     * @param layerDeckFile The string giving the path to the file containing all of the layers.
     * @throws IOException if there is an error when reading the layers or ingredients file.
     */
    public MagicBakery(long seed, String ingredientDeckFile, String layerDeckFile) throws IOException
    {
        //do some stuff with the file paths. Is the seed a thing for the serial version?
        this.random = new Random(seed);
        pantryDeck = new Stack<>();
        layers = CardUtils.readLayerFile(layerDeckFile);     
        pantryDeck.addAll(CardUtils.readIngredientFile(ingredientDeckFile));
            
        
        
        pantryDiscard = new Stack<Ingredient>();
        pantry = new ArrayList<>();
        players = new ArrayList<>();
    }
    
    /**
     * Takes the current player's hand, bake a layer and remove the required ingredients, then add the layer to their hand.@interface
     * This method is an action, so will decrement actionsLeft if it executes successfully.
     * Will throw WrongIngredientsException if the player doesn't have the required ingredients to bake the layer.
     * @param layer The layer to be baked
     */
    public void bakeLayer(Layer layer)
    {
        if(actionsLeft <= 0)
            throw new TooManyActionsException();
        List<Ingredient> hand = getCurrentPlayer().getHand();
        if(layer.canBake(hand))
        {
            //Unfortunately can't use removeAll, because it would remove duplicate elements in the hand
            
            for (Ingredient ingredient : layer.getRecipe()) {
                if(hand.contains(ingredient))
                {
                    hand.remove(ingredient);
                    ((Stack<Ingredient>)pantryDiscard).push(ingredient);
                }
                else
                {
                    hand.remove(Ingredient.HELPFUL_DUCK);
                    ((Stack<Ingredient>)pantryDiscard).push(Ingredient.HELPFUL_DUCK);
                }
            }
            layers.remove(layer);
            hand.add(layer);
            actionsLeft--;
            //Weird consequence of allowing the pantry to go empty, I'm going to assume that it should be stocked before pantry deck
            //pantry size will onlly be < 5 if pantryDeck is empty, so you have to pull from the discard. 
            if(pantry.size() <5)
            {
                pantryDeck.addAll(pantryDiscard);
                pantryDiscard.clear();
                Collections.shuffle((Stack<Ingredient>)pantryDeck, random);
                while(pantry.size() <5)
                {
                    ((ArrayList<Ingredient>)pantry).add(drawFromPantryDeck());
                }
            }

        }
        else
            throw new WrongIngredientsException("You don't have the necessary ingredients to bake this layer.");
            
    }

    /**
     * Takes the top element from pantryDeck
     * But if pantryDeck is empty, then all of the cards in pantryDiscard will be moved into it, and then pantryDeck shuffled before then drawing a card.
     * if there are no cards left in the pantryDeck or pantryDiscard, then {@link EmptyPantryException} will be thrown
     * @return the ingredient drawn from the pantry deck.
     */
    private Ingredient drawFromPantryDeck()
    {

        if(pantryDeck.isEmpty())
        {
            pantryDeck.addAll(pantryDiscard);
            pantryDiscard.clear();
            if(pantryDeck.isEmpty())
                throw new EmptyPantryException("There's no cards left, you'll have to use the cards in your hand for now.", null);
            Collections.shuffle((Stack<Ingredient>) pantryDeck, random);    
        }
        return ((Stack<Ingredient>)pantryDeck).pop();


    }

    /**
     * This function allows the player to choose an ingredient to draw from the pantry, and will decrement actionsLeft.
     * Will throw WrongIngredientException if the ingredient specified by ingredientName isn't in the pantry
     * @param ingredientName The name of the ingredient being drawn from the pantry
     */
    public void drawFromPantry(String ingredientName)
    {
        if(actionsLeft <= 0)
            throw new TooManyActionsException();

        boolean ingredientThere = false;
        for (int i = 0; i < pantry.size(); i++) 
        {
            Ingredient ingredient = ((ArrayList<Ingredient>)pantry).get(i);
            if (ingredientName.equals(ingredient.toString()))
            {
                getCurrentPlayer().addToHand(ingredient);
                try{
                    ((ArrayList<Ingredient>) pantry).set(i, drawFromPantryDeck());
                }catch(EmptyPantryException e)
                {
                    ((ArrayList<Ingredient>) pantry).remove(i);
                    System.out.println(e.getMessage());
                }

                
                ingredientThere = true;
                break; //Make sure this doesn't draw all ingredients with the same name.
            }
        }
        if(!ingredientThere)
            throw new WrongIngredientsException("That ingredient isn't in the pantry to be drawn");
        actionsLeft--;
    }

    /**
     * This function allows the player to choose an ingredient to draw from the pantry, and will decrement actionsLeft
     * Passing in null will draw from the pantry deck
     * Will throw WrongIngredientException if the ingredient specified isn't in the pantry
     * @param ingredient The ingredient being drawn from the pantry
     */
    public void drawFromPantry(Ingredient ingredient)
    {
        if(actionsLeft <= 0)
            throw new TooManyActionsException();

        //This is purely a thing to make it so that you can draw from the pantry deck within the menu
        if(ingredient == null)
        {
            try
            {
                Ingredient drawnIngredient = drawFromPantryDeck();
                getCurrentPlayer().addToHand(drawnIngredient);
                actionsLeft--;
            }
            catch(EmptyPantryException e)
            {
                System.out.println("The deck is empty, so you can't draw from there right now, until you bake some layers or fulfil orders.");
            }
            
        }
        else
        {
            boolean ingredientThere = false;
            for (int i = 0; i < pantry.size(); i++) 
            {
                Ingredient pantryIngredient = ((ArrayList<Ingredient>)pantry).get(i);
                if (ingredient.equals(pantryIngredient))
                {
                    getCurrentPlayer().addToHand(pantryIngredient);
                    try{
                        ((ArrayList<Ingredient>) pantry).set(i, drawFromPantryDeck());

                    }catch(EmptyPantryException e)
                    {
                        ((ArrayList<Ingredient>) pantry).remove(i);
                        System.out.println(e.getMessage());
                    }
                    ingredientThere = true;
                    break; //Make sure this doesn't draw all ingredients with the same name.
                }
            }
            if(!ingredientThere)
                throw new WrongIngredientsException("That ingredient isn't in the pantry to be drawn");
            actionsLeft--;
        }
        
        
    }

    /**
     * This should be called at the end of a player's turn, to switch to the next player and the next round if needed.
     * @return whether a new round is starting
     */
    public boolean endTurn()
    {
        ArrayList<Player> listPlayers  = (ArrayList<Player>) players; // Just to make this casting slightly less verbose
        currentPlayer = listPlayers.get( (listPlayers.indexOf(currentPlayer) + 1) % players.size());
        actionsLeft = getActionsPermitted();
        if(listPlayers.indexOf(currentPlayer) != 0)
            return false;
        else
        {
            if(!customers.getCustomerDeck().isEmpty()) // Wonderful stuff because I'm not allowed to error handle a stack.
                customers.addCustomerOrder();
            else
                customers.timePasses();
            return true;
        }

    }

    /**
     * This fulfills an order, using the CustomerOrder.fulfill() method
     * It uses the current player's hand as the list of ingredients to fulfil the order with, to see if it can fulfil/garnish the order.
     * It will decrement actionsLeft before doing anything else.
     * It will use and remove the ingredients used from the player's hand, and then return the player's hand as it stands
     * If an order is garnished, then 2 random cards will be drawn from the pantry and given to the the player
     * @param customer The customerOrder being fulfilled 
     * @param garnish indicator of whether the user is trying to garnish the order or not. 
     * @return The list of ingredients added to the hand due to fulfilling a garnish. This will be null if nothing is drawn
     */
    public List<Ingredient> fulfillOrder(CustomerOrder customer, boolean garnish)
    {
        if(actionsLeft <= 0)
            throw new TooManyActionsException();

        actionsLeft--;
        List<Ingredient> hand = getCurrentPlayer().getHand();
        List<Ingredient> used = customer.fulfill(hand, garnish);
        for (Ingredient ingredient: used)
        {
                hand.remove(ingredient);
                if(ingredient instanceof Layer)
                    layers.add((Layer) ingredient);
                else
                    pantryDiscard.add(ingredient);
        }
        customers.remove(customer);
        List<Ingredient> drawn = new ArrayList<>();
        if(customer.getStatus() == CustomerOrderStatus.GARNISHED) //draw two random cards from the pantry if it is garnished
        {
            for (int i = 0; i < 2; i++) {
                int index = random.nextInt(5);
                actionsLeft++; //have to counteract the decrement that's done normally
                drawFromPantry( ((ArrayList<Ingredient>) pantry).get(index));

                drawn.add(hand.get(hand.size() -1)); //get the most recently added item to the hand. I can't be bothered to redo the drawFromPantry code
            }
        }
        return drawn;
        // Do I remove the cards here or in the function that calls this, since it returns the list
        
    }

    /**
     * The number of actions permitted is 3 is there are 2 or 3 players, and 2 if there are 4 or 5 players.
     * @return the number of actions permitted per turn.
     */
    public int getActionsPermitted()
    {
        if(players.size() <=3)
            return 3;
        return 2;   
    }
    
    //Really don't get how you do this without just a class variable, since I'm not passing arguments in.
    /**
     * Gets the actions left in the turn.
     * @return the number of actions remaining for this turn.
     */
    public int getActionsRemaining()
    {
        return actionsLeft;
    }

    /**
     * Gets the layers that can be baked with the current player's hand
     * @return all distinct layers that can be fulfilled with the current player's hand
     */
    public Collection<Layer> getBakeableLayers()
    {   
        return getLayers().stream().filter(l -> l.canBake(getCurrentPlayer().getHand())).distinct().toList();
    }

    /**
     * Gets the current player in the game
     * @return the player whose turn it currently is
     */
    public Player getCurrentPlayer()
    {
        return currentPlayer;
    }

    /**
     * Gets the customers, which contains the customerDeck, activeCustomers and inactiveCustomers
     * @return the customers object
     */
    public Customers getCustomers()
    {
        return customers;
    }

    /**
     * Gets all of the customer orders that can be fulfilled with the current player's hand
     * @return the collection of fulfilable customer orders
     */
    public Collection<CustomerOrder> getFulfilableCustomers()
    {
        return customers.getFulfilable(getCurrentPlayer().getHand());
    }

    /**
     * This gets all of the customer orders that can be garnished with the current player's hand.
     * This means that they can be both fulfilled and garnished in this case
     * @return the collection of customer orders that can be garnished
     */
    public Collection<CustomerOrder> getGarnishableCustomers()
    {
        List<Ingredient> hand = getCurrentPlayer().getHand();
        Collection<CustomerOrder> garnishable = new ArrayList<>();

        for (CustomerOrder customerOrder : customers.getActiveCustomers()) {
            if(customerOrder != null && customerOrder.canFulfill(hand))
            {
                CustomerOrderStatus prevStatus = customerOrder.getStatus();
                //This is the easiest way to see if the order can be garnished, since basically does all of the steps that have to be taken.
                customerOrder.fulfill(hand, true);
                if(customerOrder.getStatus() == CustomerOrderStatus.GARNISHED)
                {
                    customerOrder.setStatus(prevStatus); // This undoes the fulfill action, since this is just hypotheticals.
                    garnishable.add(customerOrder);
                }
            }
        }
        return garnishable;
    }

    /**
     * Gets all of the layers that are available, which is 4 of each type of layer, until they get taken up
     * @return the collection of all distinct layers in the game which can be baked from the table.
     */
    public Collection<Layer> getLayers()
    {
        return layers.stream().distinct().toList();
    }

    /**
     * This gets the pantry, which is the ingredients that players can see and choose to pick from
     * @return the pantry object
     */
    public Collection<Ingredient> getPantry()
    {
        return pantry;
    }
    
    /**
     * This gets all players in the game
     * @return the collection of players
     */
    public Collection<Player> getPlayers() 
    {
        return players;
    }

    /**
     * This method will basically be in a point before the main game, where it can loop between asking the load a game or start a new one, so it doesn't need to throw an error.
     * @param file the file where the serialized MagicBakery is stored
     * @return the MagicBakery object stored in the file, which will have all of the information needed to play the game being loaded.
     * @throws IOException If there is an error reading the file
     * @throws ClassNotFoundException If the file does not have a valid save state in it.
     */
    public static MagicBakery loadState(File file) throws IOException, ClassNotFoundException
    {
        MagicBakery gameState = null;
        ObjectInputStream read = new ObjectInputStream(new FileInputStream(file));
        gameState = (MagicBakery) read.readObject();
        read.close();
        return gameState;
    } 

    /**
     * Passes an ingredient from the current player's hand to the chosen player's hand.
     * It will decrement actionsLeft if it executes correctly.
     * @param ingredient the ingredient being passed
     * @param recipient the player receiving the ingredient.
     */
    public void passCard(Ingredient ingredient, Player recipient)
    {
        if(actionsLeft <= 0)
            throw new TooManyActionsException();

        List<Ingredient> hand = getCurrentPlayer().getHand();
        if(!hand.contains(ingredient))
            throw new WrongIngredientsException();
        hand.remove(ingredient);
        recipient.addToHand(ingredient);
        actionsLeft--;
    }

    /** 
     * prints out the record for how inactive customers have been serviced. 
     * This will say how many have been fulfilled, and of those how many were garnished.
     * It will also say how many left because they gave up, never having their order fulfilled.
     */
    public void printCustomerServiceRecord()
    {
        int fulfilledCustomers, garnishedCustomers, givenUpCustomers;
        fulfilledCustomers = customers.getInactiveCustomersWithStatus(CustomerOrderStatus.FULFILLED).size();
        garnishedCustomers = customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GARNISHED).size();
        givenUpCustomers = customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GIVEN_UP).size();
        System.out.println("Delighted customers chowing down on a job well done: " + (fulfilledCustomers + garnishedCustomers) + ", with " + garnishedCustomers + " garnished.");
        System.out.println("Customers gone for walkies: " + givenUpCustomers);
    }

    /** 
     * Prints the state of the game so that the players can see everything they need to do make their choice of what action they will take.
     */
    public void printGameState()
    {
        System.out.println("\n\n==============\nCURRENT PLAYER: " + getCurrentPlayer().toString());
        System.out.println("\n");

        printCustomerServiceRecord();
        System.out.println("Customer orders to make: ");
        StringUtils.customerOrdersToStrings(customers.getActiveCustomers()).forEach(s -> System.out.println(s));
        System.out.println("layers: ");
        StringUtils.layersToStrings(getLayers()).forEach(s -> System.out.println(s));
        System.out.println("Pantry: ");
        StringUtils.ingredientsToStrings(pantry).forEach(s -> System.out.println(s));
        System.out.println("Your (" + getCurrentPlayer().toString() +"'s) hand: ");
        StringUtils.ingredientsToStrings(getCurrentPlayer().getHand()).forEach(s -> System.out.println(s));

        System.out.println("\nNumber of actions left: " + getActionsRemaining());
    }

    /** 
     * This is an action a player can take, where they choose to discard all cards in the pantry and get new cards from the deck instead.
     * It will decrement the actionsLeft variable.
     */
    public void refreshPantry()
    {
        if(actionsLeft <= 0)
            throw new TooManyActionsException();

        pantryDiscard.addAll(pantry);
        pantry.clear();
        for (int i = 0; i < 5; i++) {
            try{

                ((ArrayList<Ingredient>) pantry).add(drawFromPantryDeck());
            }catch(EmptyPantryException e)
            {
                System.out.println(e.getMessage());
                break;
            }
        }
        actionsLeft--;
        
    }

    /**
     * This file will save the state of the game to a file so that it can be loaded and played later
     * @param file the file that the state of the game (serialized MagicBakery object) is to be saved to
     * @throws IOException if there is an issue when writing to the file.
     */
    public void saveState(File file) throws IOException
    {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file)); 
        out.writeObject(this);
        out.close();
        
    }

    /**
     * This function starts the game, setting the needed variables to play the game, and then starting the game loop.
     * It will instantiate the pantry to take 5 ingredients from the pantry deck
     * It will put the correct number of customer orders in active Customers depending on the number of people
     * It will put 3 cards from the pantryDeck into each player's hand as the start of the game.
     * @param playerNames The list of player names needed to instantiate the new players, to be added into the players collection.
     * @param customerDeckFile The String of the path to the file where all of the Customers are, to be read to instantiate the customers attribute.
     * @throws IOException throws this if customers cannot be instantiated, because the customers file cannot be read properly.
     */
    public void startGame(List<String> playerNames, String customerDeckFile) throws IOException
    {

        int numPlayers = playerNames.size();
        if(numPlayers < 2 || numPlayers > 5)
            throw new IllegalArgumentException("Wrong number of players, there must be between 2 and 5 players");
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        
        customers = new Customers(customerDeckFile, random, layers, numPlayers);
        Collections.shuffle( ((Stack<Ingredient>)pantryDeck), random);
        for (int i = 0; i < 5; i++) 
        {
            pantry.add(((Stack<Ingredient>)pantryDeck).pop());    
        }

        customers.addCustomerOrder();    
        if(numPlayers == 3 || numPlayers == 5) // Only add a second customer in the beginning if there are 3 or 5 players.
            customers.addCustomerOrder();

        actionsLeft = getActionsPermitted();
        
        for (Player player : players) {
            for (int i = 0; i < 3; i++) 
            {
                player.addToHand(((Stack<Ingredient>)pantryDeck).pop());
                //Not bothering to do empty checking on this, since that's only meant to be an issue once cards go in pantryDiscard.
                // If it runs out here, the game's kind of unplayable.    
            }
        }
        currentPlayer = (Player) players.toArray()[0];

        //playGame();
    }

    
    
    
}
