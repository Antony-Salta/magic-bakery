package bakery;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import util.CardUtils;

public class MagicBakery{
    private Customers customers;
    private Collection<Layer> layers;
    private Collection<Player> players;
    private Collection<Ingredient> pantry;
    private Collection<Ingredient> pantryDeck; // TODO Change back and see if it works.
    private Collection<Ingredient> pantryDiscard;
    private Random random;
    private static final long serialVersionUID;

    public enum ActionType
    {
        DRAW_INGREDIENT,
        PASS_INGREDIENT,
        BAKE_LAYER,
        FULFIL_ORDER,
        REFRESH_PANTRY

    }

    public MagicBakery(long seed, String ingredientDeckFile, String layerDeckFile)
    {
        //do some stuff with the file paths. Is the seed a thing for the serial version?
        random = new Random(seed);
        pantryDeck = (Stack<Ingredient>) CardUtils.readIngredientFile(ingredientDeckFile);
        layers = CardUtils.readLayerFile(layerDeckFile);
        pantryDiscard = new Stack<Ingredient>();
        pantry = new ArrayList<>();
    }
    public void bakeLayer(Layer layer)
    {

    }
    public Ingredient drawFromPantryDeck()
    {

    }

    public void drawFromPantry(String ingredientName)
    {

    }
    public void drawFromPantry(Ingredient ingredient)
    {
        
    }

    public boolean endTurn()
    {

    }

    public List<Ingredient> fulfillOrder(CustomerOrder customer, boolean garnish)
    {
        
    }

    public int getActionsPermitted()
    {

    }
    
    public int getActionsRemaining()
    {

    }

    public Collection<Layer> getBakeableLayers()
    {

    }

    public Player getCurrentPlayer()
    {

    }

    public Customers getCustomers()
    {
        return customers;
    }
    public Collection<CustomerOrder> getFulfillableCustomers()
    {

    }

    public Collection<CustomerOrder> getGarnishableCustomers()
    {

    }

    public Collection<Layer> getLayers()
    {
        return layers;
    }

    public Collection<Ingredient> getPantry()
    {
        return pantry;
    }
    
    public Collection<Player> getPlayers() 
    {
        return players;
    }

    public static MagicBakery loadState(File file)
    {

    } 

    public void passCard(Ingredient ingredient, Player recipient)
    {

    }

    public void printCustomerServiceRecord()
    {

    }

    public void printGameState()
    {

    }

    public void refreshPantry()
    {

    }
    public void saveState(File file)
    {

    }

    public void startGame(List<String> playerNames, String customerDeckFile)
    {
        int numPlayers = playerNames.size();
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

        for (Player player : players) {
            for (int i = 0; i < 3; i++) 
            {
                player.addToHand(((Stack<Ingredient>)pantryDeck).pop());
                //Not bothering to do empty checking on this, since that's only meant to be an issue once cards go in pantryDiscard.
                // If it runs out here, the game's kind of unplayable.    
            }
        }
    }
    
}
