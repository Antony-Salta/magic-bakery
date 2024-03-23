package bakery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This represents the players of the game, which will have a hand of ingredient cards and names.
 * @author Antony Salta
 * @version 1.0
 *
 * This isn't the correct version number, but I haven't been tracking until now
 */
public class Player implements Serializable
{
    private List<Ingredient> hand;
    private String name;
    private static final long serialVersionUID =1;

    /**
     * Makes a player with the specified name, and an empty list for their hand
     * @param name Sets the name attribute to the name passed in this constructor.
     */
    public Player(String name)
    {
        this.name = name;
        this.hand = new ArrayList<>(); 
    }
    
    /**
     * Adds a list of ingredients to the player's hand
     * @param ingredients The list of ingredients to add to the player's hand
     */
    public void addToHand(List<Ingredient> ingredients)
    {
        hand.addAll(ingredients);
    }
    /**
     * Adds an ingredient to the player's hand
     * @param ingredient The ingredient to add to the player's hand
     */
    public void addToHand(Ingredient ingredient)
    {
        hand.add(ingredient);
    }

    /**
     * Checks if the player has some ingredient in their hand
     * @param ingredient The ingredient that is being checked to see if it is there
     * @return a boolean of if their hand contains a certain ingredient
     */
    public boolean hasIngredient(Ingredient ingredient)
    {
        return hand.contains(ingredient);
    }

    /**
     * Removes the first instance of the specified ingredient from the player's hand
     * @param ingredient The ingredient to remove one from the player's hand
     */
    public void removeFromHand(Ingredient ingredient)
    {
        if(!hand.contains(ingredient))
            throw new WrongIngredientsException("The hand does not contain this ingredient to be removed");
        hand.remove(ingredient);
    }

    /**
     * Gets the sorted list of ingredients in the player's hand.
     * @return the sorted list of ingredients making up the players hand
     */
    public List<Ingredient> getHand()
    {
        Collections.sort(hand);
        return hand;
    }

    /**
     * Gets the player's hand as a comma separated list, sorted and with capitalised names for ingredients.
     * @return the players hand as a comma separated list
     * e.g. "Chocolate, Eggs (x2), Sugar"
     */
    public String getHandStr()
    {
        return IngredientListUtil.stringFromIngList(hand, true, true);
        // if(hand.isEmpty())
        //     return ""; //Don't bother if it's empty
        // Collections.sort(hand);
        // String csList = "";
        // Ingredient prevIng = hand.get(0);
        // int numSame = 0;
        // // e.g. c, e, e, e , s
        // for( Ingredient ing: hand)
        // {
        //     if(ing != prevIng)
        //     {
        //         String original = prevIng.toString();
        //         String CapitalisedName = original.substring(0,1).toUpperCase();
        //         CapitalisedName += original.substring(1);
        //         csList += CapitalisedName;
        //         if(numSame > 1)
        //             csList += " (x" + numSame + ")";
        //         csList += ", ";
        //         numSame = 1;
        //         prevIng = ing;
        //     }
        //     else
        //         numSame ++;
        // }
        // String original = hand.get(hand.size()-1).toString();
        // String CapitalisedName = original.substring(0,1).toUpperCase();
        // CapitalisedName += original.substring(1);
        // csList += CapitalisedName;
        // if(numSame > 1)
        //     csList += " (x" + numSame + ")";

        // return  csList;
    }

    @Override
    /**
     * Gets the name of the player as the player's string representation.
     * @return the name attribute of the player object.
     */
    public String toString()
    {
        return name;
    }

    
}