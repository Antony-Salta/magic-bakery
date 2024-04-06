package bakery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents the Customer Order cards in the game, so have a recipe, an optional garnish, and a difficulty level associated with them.
 * @author Antony Salta
 * @version 1.0
 *
 * This isn't the correct version number, but I haven't been tracking until now
 */
public class CustomerOrder implements Serializable
{
    private List<Ingredient> garnish;
    private int level;
    private String name;
    private List<Ingredient> recipe;
    private CustomerOrderStatus status;
    private static final long serialVersionUID =1;

    /**
     * This gives the different statuses that CustomerOrders can be in.
     * To begin, they are waiting
     * They are impatient when they will be removed next turn from the activeCustomer view
     * They are fulfilled or garnished depending on how the players make that order
     * They are given up if they go into inactiveCustomers without being completed.
     * @author Antony Salta
     * @version 1.0
     *
     * This isn't the correct version number, but I haven't been tracking until now
     *

     */
    public enum CustomerOrderStatus
    {
        WAITING,
        FULFILLED,
        GARNISHED,
        IMPATIENT,
        GIVEN_UP
    }

    /**
     * Creates a customer order with the specified attributes, and will throw WrongIngredients exception if the recipe is null or empty.
     * @param name The name of the customer order.
     * @param recipe The list of ingredients needed to make this order, sorted
     * @param garnish The list of ingredients needed to make the optional garnish for a bonus, sorted
     * @param level The difficulty level of the order
     */
    public CustomerOrder(String name, List<Ingredient> recipe, List<Ingredient> garnish, int level)
    {
        if(recipe == null)
            throw new WrongIngredientsException("Cannot have a null recipe in constructor.");
        if(recipe.isEmpty())
            throw new WrongIngredientsException("Cannot have an empty recipe in constructor.");
        
        this.name = name;
        this.recipe = recipe;
        this.garnish = garnish;
        this.level = level;
        this.status = CustomerOrderStatus.WAITING;
        //if(this.recipe != null) Collections.sort(this.recipe);
        //if(this.garnish != null)Collections.sort(this.garnish);
    }

    /** 
     * Sets the status of the order to mark that it has not and cannot be fulfilled, with GIVEN_UP
     */
    public void abandon()
    {
        this.status = CustomerOrderStatus.GIVEN_UP;
    }

    /**
     * Takes a list of ingredients and see if it has enough ingredients/Helpful Ducks to make the recipe of this order.
     * @param ingredients The list of ingredients being checked
     * @return boolean of if the recipe can be made
     */
    public boolean canFulfill( List<Ingredient> ingredients)
    {
        return canMake(recipe, ingredients);
    }

    /**
     * Takes a list of ingredients and see if it has enough ingredients/Helpful Ducks to make the garnish of this order.
     * This does not look at if the recipe AND garnish can be made, just the garnish.
     * @param ingredients The list of ingredients being checked
     * @return boolean of if the garnish can be made
     */
    public boolean canGarnish( List<Ingredient> ingredients)
    {
        if(garnish == null || garnish.isEmpty()) // orders without a garnish can't be garnished
            return false;
        return canMake(garnish, ingredients);
    }

    /**
     * This function is a general function used by canFulfil and canGarnish to see if the given recipe can be made given some other list of ingredients.
     * @param thingToMake The list of ingredients needed to make either the recipe or the garnish.
     * @param ingredients The ingredients that can be used to make the specified recipe, generally a player's hand
     * @return Boolean value of whether the recipe/garnish can be made
     */
    private boolean canMake(List<Ingredient> thingToMake, List<Ingredient> ingredients)
    {
        HashMap<Ingredient, Integer> quantities = new HashMap<>();
        for(Ingredient ingredient: thingToMake) // this builds up the amount needed for each ingredients
        {
            if(!quantities.containsKey(ingredient))
                quantities.put(ingredient, 1);
            else
                quantities.replace(ingredient, quantities.get(ingredient) + 1);
        }

        int numDucks =0;

        for (Ingredient ingredient : ingredients) {
            if(quantities.containsKey(ingredient))
            {
                int numLeft = quantities.get(ingredient) -1;
                quantities.replace(ingredient, numLeft);
                if(quantities.get(ingredient) == 0)
                {
                    quantities.remove(ingredient);
                }
                    
            }
                
            if(ingredient.equals(Ingredient.HELPFUL_DUCK))
                numDucks++;
        } // so at the end of the loop it will have counted down all of the ingredients, so then we just have to see if they have enough helpful ducks.
        if(quantities.isEmpty())
            return true; //shortcut to avoid this if the normal ingredients account for it.
        int count = 0;
        for (Ingredient ingredient : quantities.keySet()) { // return false if there is a Layer unaccounted for.
            if (ingredient instanceof Layer)
                return false;
        }
        for(Integer num: quantities.values()) // this gets the number of items in the recipe not accounted for by the normal ingredients.
        {
            count += num;
        }

        return numDucks >= count; // This will work if the normal ingredients cover it, since then it'll be 0 or more >= 0.
    }

    /**
     * Attempts to fulfil the order, possibly with the garnish.
     * If the order cannot be garnished, even if the garnish flag is true, then the order will just be fulfilled.
     * It will set the status of the customerOrder to FULFILLED or GARNISHED appropriately.
     * It will throw {@link WrongIngredientsException} if the ingredients passed cannot fulfil the order.
     * @param ingredients The list of ingredients being used.
     * @param garnish This is a flag marking if the garnish is being made as well.
     * @return The list of ingredients used to fulfill the order, so counting Helpful Ducks if they were needed.
     */
    public List<Ingredient> fulfill(List<Ingredient> ingredients, boolean garnish)
    {
        List<Ingredient>used = new ArrayList<>(); 
        List<Ingredient> copy = new ArrayList<>(ingredients);
        if(canFulfill(copy))
        {
            
            for (Ingredient ingredient : recipe) 
            {
                if(copy.contains(ingredient))
                {
                    copy.remove(ingredient);
                    used.add(ingredient);
                }
                    
                else
                {
                    copy.remove(Ingredient.HELPFUL_DUCK);
                    used.add(Ingredient.HELPFUL_DUCK);
                }
            }
            if(garnish && canGarnish(copy))
            {
                for (Ingredient ingredient : this.garnish) 
                {
                    if(copy.contains(ingredient))
                    {
                        copy.remove(ingredient);
                        used.add(ingredient);
                    }
                    else
                    {
                        copy.remove(Ingredient.HELPFUL_DUCK);
                        used.add(Ingredient.HELPFUL_DUCK);
                    }
                        
                }
                status = CustomerOrderStatus.GARNISHED;   
            }
            else
                status = CustomerOrderStatus.FULFILLED;
        }
        else
            throw new WrongIngredientsException("Don't have the ingredients to fulfill the order");

        return used;
    }

    @Override
    /**
     * The toString() function returns the name of the order.
     * @return the name of the order object.
     */
    public String toString()
    {
        return name;
    }

    /**
     * Gets the order's garnish
     * @return the list of ingredients that make the garnish attribute
     */
    public List<Ingredient> getGarnish() 
    {
        return garnish;
    }

    /**
     * Gets the difficulty level of the order
     * @return the difficulty level of this order
     */
    public int getLevel() 
    {
        return level;
    }

    /**
     * Gets the recipe of the order
     * @return The list of ingredients that make the recipe attribute
     */
    public List<Ingredient> getRecipe() 
    {
        return recipe;
    }
    /**
     * Gets the status of the order
     * @return the CustomerOrderStatus enum that marks the status of this order
     */
    public CustomerOrderStatus getStatus() 
    {
        return status;
    }

    /**
     * Sets the status of the order to the one passed in.
     * @param status The CustomerOrderStatus to set this order to.
     */
    public void setStatus(CustomerOrderStatus status) 
    {
        this.status = status;
    }

    /**
     * Gets the comma separated list of ingredients in the recipe.
     * @return a string giving a comma separated list of ingredients in the recipe:
     * e.g. "Chocolate, Eggs (x2), Sugar"
     */
    public String getRecipeDescription()
    {
        return IngredientListUtil.stringFromIngList(recipe, false, true);
    }
    /**
     * Gets the comma separated list of ingredients in the garnish.
     * @return a string giving a comma separated list of ingredients in the garnish:
     * e.g. "Chocolate, Eggs (x2), Sugar"
     */
    public String getGarnishDescription()
    {
        return IngredientListUtil.stringFromIngList(garnish, false, false);
    }

}
