package bakery;

import java.util.List;
import java.util.Collections;
import java.util.HashMap;  

/**
 * The Layer class is the representation of the layer card in the game. 
 * It acts as a form of ingredient for customer orders, but has a recipe that has to be fulfilled before a player can earn one of them.
 */
public class Layer extends Ingredient
{
    private List<Ingredient> recipe;
    private final long serialVersionUID =1;

    /**
     * This makes a layer object with the given attributes
     * @param name: The name of the layer
     * @param recipe: The ingredients needed to make this layer, sorted by name.
     */
    public Layer(String name, List<Ingredient> recipe)
    {
        super(name);
        if(recipe == null)
            throw new WrongIngredientsException("Cannot have a null recipe in constructor.");
        if(recipe.isEmpty())
            throw new WrongIngredientsException("Cannot have an empty recipe in constructor.");
        
        this.recipe = recipe;
        Collections.sort(this.recipe);
    }

    /**
     * This method checks if the layer can be baked given some ingredients.
     * It will check if there are enough ingredients matching the required ones, and if not, if there are enough Helpful Duck cards to substitute in.
     * @param ingredients: The list of ingredients to be checked if it can fulfill the recipe.
     * @return: the boolean of if the layer can be made
     */
    public boolean canBake(List<Ingredient> ingredients)
    {
        HashMap<String, Integer> quantities = new HashMap<>();
        for(Ingredient ingredient: recipe) // this builds up the amount needed for each ingredients
        {
            String iName = ingredient.toString(); // just to get rid of all the toString calls.
            if(!quantities.containsKey(iName))
                quantities.put(iName.toString(), 1);
            else
                quantities.replace(iName, quantities.get(iName) + 1);
        }

        int numDucks =0;
        for (Ingredient ingredient : ingredients) {
            String iName = ingredient.toString();
            if(quantities.containsKey(iName))
            {
                quantities.replace(iName, quantities.get(iName) - 1);
                if(quantities.get(iName) == 0)
                    quantities.remove(iName);
            }
                
            
            
            if(ingredient.equals(Ingredient.HELPFUL_DUCK)) // NOTE: dunno if this is how you actually count the helpful ducks.
                numDucks++;
        } // so at the end of the loop it will have counted down all of the ingredients, so then we just have to see if they have enough helpful ducks.
        
        int count = 0;
        for(Integer num: quantities.values()) // this gets the number of items in the recipe not accounted for by the normal ingredients.
        {
            count += num;
        }
            
        return numDucks >= count; // This will work if the normal ingredients cover it, since then it'll be 0 or more >= 0.
    }

    /**
     * This makes the hashcode linked to the name and recipe of the layer object.
     */
    @Override
    public int hashCode()
    {
        String combo = this.toString() + getRecipeDescription();
        return combo.hashCode();
    }
    
    /**
     * 
     * @return the recipe as a List<Ingredient> object
     */
    public List<Ingredient> getRecipe()
    {
        return recipe;
    }

    
    /**
     * 
     * @return the list of ingredients in the recipe, separated with commas 
     * When there are multiple of the same ingredient, it will be given like: "Eggs (x3)", instead of outputting "Eggs" 3 times.
     */
    public String getRecipeDescription()
    {
        return IngredientListUtil.stringFromIngList(recipe);
    }
    
}