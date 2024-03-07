package main.bakery;

import java.util.List;
import java.util.HashMap;  

public class Layer extends Ingredient
{
    private List<Ingredient> recipe;
    private final long serialVersionUID;

    public Layer(String name, List<Ingredient> recipe)
    {
        super(name);
        this.recipe = recipe;
    }

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
                quantities.replace(iName, quantities.get(iName) - 1);
            
            if(quantities.get(iName) == 0)
                quantities.remove(iName);
            if(ingredient.equals(ingredient.HELPFUL_DUCK)) // NOTE: dunno if this is how you actually count the helpful ducks.
                numDucks++;
        } // so at the end of the loop it will have counted down all of the ingredients, so then we just have to see if they have enough helpful ducks.
        
        int count = 0;
        for(Integer num: quantities.values()) // this gets the number of items in the recipe not accounted for by the normal ingredients.
        {
            count += num;
        }
            
        return numDucks >= count; // This will work if the normal ingredients cover it, since then it'll be 0 or more >= 0.
    }

    @Override
    public int hashCode()
    {
        String combo = this.toString() + recipe.toString();
        return combo.hashCode();
    }
    
    public List<Ingredient> getRecipe()
    {
        return recipe;
    }

    public String getRecipeDescription()
    {
        String csList = "";
        for( Ingredient ing: recipe)
        {
            csList += ing.toString() + ", ";
        }
        return  csList.substring(0, csList.length() -2);

    }
    
}