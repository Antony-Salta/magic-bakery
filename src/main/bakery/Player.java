package bakery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This represents the players of the game, which will have a hand of ingredient cards and names.
 */
public class Player implements Serializable
{
    private List<Ingredient> hand;
    private String name;
    private static final long serialVersionUID =1;

    /**
     * 
     * @param name: Sets the name attribute to the name passed in this constructor.
     */
    public Player(String name)
    {
        this.name = name;
        this.hand = new ArrayList<>(); 
    }
    
    /**
     * 
     * @param ingredients: The list of ingredients to add to the player's hand
     */
    public void addToHand(List<Ingredient> ingredients)
    {
        hand.addAll(ingredients);
    }
    /**
     * 
     * @param ingredient: The ingredient to add to the player's hand
     */
    public void addToHand(Ingredient ingredient)
    {
        hand.add(ingredient);
    }

    /**
     * 
     * @param ingredient: The ingredient that is being checked to see if it is there
     * @return a boolean of if their hand contains a certain ingredient
     */
    public boolean hasIngredient(Ingredient ingredient)
    {
        return hand.contains(ingredient);
    }

    /**
     * 
     * @param ingredient: The ingredient to remove one from the player's hand
     */
    public void removeFromHand(Ingredient ingredient)
    {
        if(!hand.contains(ingredient))
            throw new IllegalArgumentException("The hand does not contain this ingredient to be removed");
        hand.remove(ingredient);
    }

    /**
     * 
     * @return the sorted list of ingredients making up the players hand
     */
    public List<Ingredient> getHand()
    {
        Collections.sort(hand);
        return hand;
    }

    /**
     * 
     * @return: the players hand as a comma separated list, and sorted.
     * e.g. "Chocolate, Eggs (x2), Sugar"
     */
    public String getHandStr()
    {
        return IngredientListUtil.stringFromIngList(hand);
    }

    /** 
     * @return: the name attribute of the player object.
     */
    @Override
    public String toString()
    {
        return name;
    }

    
}