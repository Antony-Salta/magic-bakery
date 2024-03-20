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
     * This method will take the current player's hand, bake a layer and remove the required ingredients, then add the layer to their hand.@interface
     * This method is an action, so will decrement actionsLeft if it executes successfully.
     * @param layer The layer to be baked
     * @return void There's nothing to be returned here, since the layer is added directly to the player's hand in here.
     * @throws a WrongIngredientsException if the player doesn't have the required ingredients to bake the layer.
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
            //TODO uncomment if layers ends up being limited
            //layers.remove(layer);
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
     * This method will take the top element from pantryDeck
     * But if pantryDeck is empty, then all of the cards in pantryDiscard will be moved into it, and then pantryDeck shuffled before then drawing a card.
     * @return the ingredient drawn from the pantry deck.
     * @throws EmptyPantryException if there are no cards left in the pantryDeck or pantryDiscard.
     */
    private Ingredient drawFromPantryDeck()
    {
        if(actionsLeft <= 0)
            throw new TooManyActionsException();

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
     * This function allows the player to choose an ingredient to draw from the pantry, and will decrement actionsLeft
     * @param ingredientName The name of the ingredient being drawn from the pantry
     * @return void This method will add the selected card to the player's hand, returning nothing
     * @throws WrongIngredientException if the ingredient specified by ingredientName isn't in the pantry
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
     * @param ingredientName The ingredient being drawn from the pantry
     * @return void This method will add the selected card to the player's hand.
     * @throws WrongIngredientException if the ingredient specified isn't in the pantry
     */
    public void drawFromPantry(Ingredient ingredient)
    {
        if(actionsLeft <= 0)
            throw new TooManyActionsException();

        //This is purely a thing to make it so that you can draw from the pantry deck within the menu
        if(ingredient.equals(Ingredient.HELPFUL_DUCK))
        {
            try
            {
                drawFromPantryDeck();
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
     * This checks the number of actions remaining to see if a player's turn should end.
     * If it should, then it changes the currentPlayer to be the next player.
     * @return whether the current player's turn should end
     */
    public boolean endTurn()
    {
        if(getActionsRemaining() <=0)
        {
            ArrayList<Player> listPlayers  = (ArrayList<Player>) players; // Just to make this casting slightly less verbose
            currentPlayer = listPlayers.get( (listPlayers.indexOf(currentPlayer) + 1) % players.size());
            actionsLeft = getActionsPermitted();
            return true;
        }
        return false;
    }

    //TODO: figure out if this takes the cards from the hand or not.
    /**
     * This fulfills an order, using the CustomerOrder.fulfill() method
     * It uses the current player's hand as the list of ingredients to fulfil the order with, to see if it can fulfil/garnish the order.
     * It will decrement actionsLeft before doing anything else.
     * @param customer The customerOrder being fulfilled 
     * @param garnish indicator of whether the user is trying to garnish the order or not. 
     * @return The list of ingredients used to fulfil the order
     */
    public List<Ingredient> fulfillOrder(CustomerOrder customer, boolean garnish)
    {
        if(actionsLeft <= 0)
            throw new TooManyActionsException();

        actionsLeft--;
        return customer.fulfill(getCurrentPlayer().getHand(), garnish);
        // Do I remove the cards here or in the function that calls this, since it returns the list
        
    }

    /**
     * 
     * @return the number of actions permitted per turn.
     * The number of actions permitted is 3 is there are 2 or 3 players, and 2 if there are 4 or 5 players.
     */
    public int getActionsPermitted()
    {
        if(players.size() <=3)
            return 3;
        return 2;   
    }
    
    //Really don't get how you do this without just a class variable, since I'm not passing arguments in.
    /**
     * 
     * @return the number of actions remaining for this turn.
     */
    public int getActionsRemaining()
    {
        return actionsLeft;
    }

    /**
     * 
     * @return all layers that can be fulfilled with the current players hand
     */
    public Collection<Layer> getBakeableLayers()
    {   
        return layers.stream().filter(l -> l.canBake(getCurrentPlayer().getHand())).toList();
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
     * This gets all of the customer orders that can be fulfilled with the current player's hand
     * @return the collection of fulfilable customer orders
     */
    public Collection<CustomerOrder> getFulfilableCustomers()
    {
        return customers.getFulfilable(getCurrentPlayer().getHand());
    }

    /**
     * This gets all of the customer orders that can be garnished with the current player's hand
     * @return the collection of customer orders that can be garnished
     */
    public Collection<CustomerOrder> getGarnishableCustomers()
    {
        List<Ingredient> hand = getCurrentPlayer().getHand();
        Collection<CustomerOrder> garnishable = new ArrayList<>();
        for (CustomerOrder customerOrder : customers.getActiveCustomers()) {
            if(customerOrder != null && customerOrder.canGarnish(hand))
                garnishable.add(customerOrder);
        }
        return garnishable;
    }

    /**
     * This gets all of the layers, which is 6 of each type of layer.
     * @return the collection of all of the layers in the game
     */
    public Collection<Layer> getLayers()
    {
        return layers;
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
     * @throws IOException IF there is an error reading the file
     * @throws FileNotFoundException If the file specified does not exist or cannot be read for some reason
     * @throws ClassNotFoundException If the file does not have a valid save state in it.
     */
    public static MagicBakery loadState(File file) throws FileNotFoundException, IOException, ClassNotFoundException
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
     * @return void. The chosen card is passed from one player to the other, nothing is returned.
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
     * @return void, it just prints things, there's nothing to be returned
     */
    public void printCustomerServiceRecord()
    {
        int fulfilledCustomers, garnishedCustomers, givenUpCustomers;
        fulfilledCustomers = customers.getInactiveCustomersWithStatus(CustomerOrderStatus.FULFILLED).size();
        garnishedCustomers = fulfilledCustomers + customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GARNISHED).size();
        givenUpCustomers = customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GIVEN_UP).size();
        System.out.println("Delighted customers chowing down on a job well done: " + fulfilledCustomers + ", with " + garnishedCustomers + " garnished.");
        System.out.println("Customers gone for walkies: " + givenUpCustomers);
    }

    /** 
     * Prints the state of the game so that the players can see everything they need to do make their choice of what action they will take.
     * @return void, it just prints, there is nothing to be returned
     */
    public void printGameState()
    {
        System.out.println("\n\n==============\nCURRENT PLAYER: " + getCurrentPlayer().toString());
        System.out.println("\n");

        printCustomerServiceRecord();
        System.out.println("Customer orders to make: ");
        StringUtils.customerOrdersToStrings(customers.getActiveCustomers()).forEach(s -> System.out.println(s));
        System.out.println("layers: ");
        StringUtils.layersToStrings(layers).forEach(s -> System.out.println(s));
        System.out.println("Pantry: ");
        StringUtils.ingredientsToStrings(pantry).forEach(s -> System.out.println(s));
        System.out.println("Your (" + getCurrentPlayer().toString() +"'s) hand: ");
        StringUtils.ingredientsToStrings(getCurrentPlayer().getHand()).forEach(s -> System.out.println(s));

        System.out.println("\nNumber of actions left: " + getActionsRemaining());
    }

    /** 
     * This is an action a player can take, where they choose to discard all cards in the pantry and get new cards from the deck instead.
     * It will decrement the actionsLeft variable.
     * @return void, there is nothing to be returned, since the pantry is just swapped out here.
     */
    public void refreshPantry()
    {
        if(actionsLeft <= 0)
            throw new TooManyActionsException();

        pantryDiscard.addAll(pantry);
        for (int i = 0; i < pantry.size(); i++) {
            try{
                ((ArrayList<Ingredient>) pantry).set(i, drawFromPantryDeck());
            }catch(EmptyPantryException e)
            {
                ((ArrayList<Ingredient>) pantry).remove(i);
                System.out.println(e.getMessage());
                break;
            }
        }
        actionsLeft--;
        
    }

    /**
     * This file will save the state of the game to a file so that it can be loaded and played later
     * @param file the file that the state of the game (serialized MagicBakery object) is to be saved to
     * @return void, there is nothing to be returned
     * @throws IOException 
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
