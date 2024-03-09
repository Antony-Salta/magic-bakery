package bakery;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class MagicBakery{
    private Customers customers;
    private Collection<Layer> layers;
    private Collection<Player> players;
    private Collection<Ingredient> pantry;
    private Collection<Ingredient> pantryDeck = new Stack<Ingredient>();
    private Collection<Ingredient> pantryDiscard = new Stack<Ingredient>();
    private Random random;
    private static final long serialVersionUID;

    public MagicBakery(long seed, String ingredientDeckFile, String layerDeckFile)
    {
        //do some stuff with the file paths. Is the seed a thing for the serial version?
        random = new Random(seed);
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
        for (String name : playerNames) {
            players.add(new Player(name));
        }
    }
    
}
