package bakery;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import util.CardUtils;

public class MagicBakery implements Serializable{
    private Customers customers;
    private Collection<Layer> layers;
    private Collection<Player> players;
    private Collection<Ingredient> pantry;
    private Collection<Ingredient> pantryDeck; 
    private Collection<Ingredient> pantryDiscard;
    private Random random;
    private static final long serialVersionUID =1;

    private int currentPlayerIndex;
    private int actionsLeft;
    private int actionsLeftReset;

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
        pantryDeck = CardUtils.readIngredientFile(ingredientDeckFile);
        layers = CardUtils.readLayerFile(layerDeckFile);
        pantryDiscard = new Stack<Ingredient>();
        pantry = new ArrayList<>();
    }
    public void bakeLayer(Layer layer)
    {
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
        }
        throw new WrongIngredientsException("You don't have the necessary ingredients to bake this layer.");
            
    }
    public Ingredient drawFromPantryDeck()
    {
        if(pantryDeck.isEmpty())
        {
            pantryDeck.addAll(pantryDiscard);
            pantryDiscard.clear();
            Collections.shuffle((Stack<Ingredient>) pantryDeck, random);    
        }
        return ((Stack<Ingredient>)pantryDeck).pop();
    }

    public void drawFromPantry(String ingredientName)
    {
        boolean ingredientThere = false;
        for (int i = 0; i < pantry.size(); i++) 
        {
            Ingredient ingredient = ((ArrayList<Ingredient>)pantry).get(i);
            if (ingredientName.equals(ingredient.toString()))
            {
                getCurrentPlayer().addToHand(ingredient);
                ((ArrayList<Ingredient>) pantry).set(i, drawFromPantryDeck());
                ingredientThere = true;
                break; //Make sure this doesn't draw all ingredients with the same name.
            }
        }
        if(!ingredientThere)
            throw new WrongIngredientsException("That ingredient isn't in the pantry to be drawn");
    }
    public void drawFromPantry(Ingredient ingredient)
    {
        boolean ingredientThere = false;
        for (int i = 0; i < pantry.size(); i++) 
        {
            Ingredient pantryIngredient = ((ArrayList<Ingredient>)pantry).get(i);
            if (ingredient.equals(pantryIngredient))
            {
                getCurrentPlayer().addToHand(pantryIngredient);
                ((ArrayList<Ingredient>) pantry).set(i, drawFromPantryDeck());
                ingredientThere = true;
                break; //Make sure this doesn't draw all ingredients with the same name.
            }
        }
        if(!ingredientThere)
            throw new WrongIngredientsException("That ingredient isn't in the pantry to be drawn");
    }

    public boolean endTurn()
    {
        return getActionsRemaining() <=0;
    }

    //TODO: figure out if this takes the cards from the hand or not.
    public List<Ingredient> fulfillOrder(CustomerOrder customer, boolean garnish)
    {
        return customer.fulfill(getCurrentPlayer().getHand(), garnish);
        // Do I remove the cards here on in the function that calles this, since it returns the list
    }

    
    public int getActionsPermitted()
    {
        if(players.size() <=3)
            return 3;
        return 4;   
    }
    
    //Really don't get how you do this without just a class variable, since I'm not passing arguments in.
    public int getActionsRemaining()
    {
        return actionsLeft;
    }

    public Collection<Layer> getBakeableLayers()
    {   
        return layers.stream().filter(l -> l.canBake(getCurrentPlayer().getHand())).toList();
    }

    public Player getCurrentPlayer()
    {
        return ((ArrayList<Player>)players).get(currentPlayerIndex);
    }

    public Customers getCustomers()
    {
        return customers;
    }
    public Collection<CustomerOrder> getFulfilableCustomers()
    {
        return customers.getFulfilable(getCurrentPlayer().getHand());
    }

    public Collection<CustomerOrder> getGarnishableCustomers()
    {
        List<Ingredient> hand = getCurrentPlayer().getHand();
        return customers.getActiveCustomers().stream().filter(c -> c.canGarnish(hand)).toList();
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
        return null;
    } 

    public void passCard(Ingredient ingredient, Player recipient)
    {
        List<Ingredient> hand = getCurrentPlayer().getHand();
        if(!hand.contains(ingredient))
            throw new WrongIngredientsException();
        hand.remove(ingredient);
        recipient.addToHand(ingredient);
    }

    public void printCustomerServiceRecord()
    {

    }

    public void printGameState()
    {

    }

    public void refreshPantry()
    {
        pantryDiscard.addAll(pantry);
        for (int i = 0; i < pantry.size(); i++) {
            ((ArrayList<Ingredient>)pantry).set(i,drawFromPantryDeck());
        }
        
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

        if(numPlayers <=3)
            actionsLeftReset = 3;
        else if(numPlayers <=5)
            actionsLeftReset = 4;
        actionsLeft = actionsLeftReset;
        
        for (Player player : players) {
            for (int i = 0; i < 3; i++) 
            {
                player.addToHand(((Stack<Ingredient>)pantryDeck).pop());
                //Not bothering to do empty checking on this, since that's only meant to be an issue once cards go in pantryDiscard.
                // If it runs out here, the game's kind of unplayable.    
            }
        }

        playGame();
    }

    private void playGame()
    {
        while(!customers.getCustomerDeck().isEmpty() && !customers.isEmpty()) //Sp while there are still customers to be served
        {

        }
    }
    
}
