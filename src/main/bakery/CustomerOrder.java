package main.bakery;

import java.util.HashMap;
import java.util.List;

import main.bakery.CustomerOrderStatus;
import main.bakery.Ingredient;

public class CustomerOrder
{
    private List<Ingredient> garnish;
    private int level;
    private String name;
    private List<Ingredient> recipe;
    private CustomerOrderStatus status;

    private final long serialVersionUID;

    public CustomerOrder(String name, List<Ingredient> recipe, List<Ingredient> garnish, int level)
    {
        this.name = name;
        this.recipe = recipe;
        this.garnish = garnish;
        this.level = level;
    }

    public void abandon()
    {
        status = CustomerOrderStatus.GIVEN_UP;
    }

    public boolean canFulfill( List<Ingredient> ingredients)
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

    public boolean canGarnish( List<Ingredient> ingredients)
    {
        HashMap<String, Integer> quantities = new HashMap<>();
        for(Ingredient ingredient: garnish) // this builds up the amount needed for each ingredients
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

    public List<Ingredient> fulfill(List<Ingredient> ingredients, boolean garnish)
    {
        List<Ingredient>backup = null; // this exists to hopefully be able to undo
        if(garnish)
            backup = List.copyOf(ingredients);
        
        if(canFulfill(ingredients))
        {
            
            for (Ingredient ingredient : recipe) 
            {
                if(ingredients.contains(ingredient))
                    ingredients.remove(ingredient);
                else
                    ingredients.remove(ingredient.HELPFUL_DUCK);
                
            }
            if(garnish)
            {
                if(canGarnish(ingredients))
                {
                    for (Ingredient ingredient : this.garnish) 
                    {
                        if(ingredients.contains(ingredient))
                            ingredients.remove(ingredient);
                        else
                            ingredients.remove(ingredient.HELPFUL_DUCK);
                    }
                    status = CustomerOrderStatus.GARNISHED;
                }
                else
                {
                    ingredients.clear(); // this bit's here because the docs say that backup will be unmodifiable
                    for (Ingredient ingredient : backup) {
                        ingredients.add(ingredient);
                    }
                }
                
            }
            else
                status = CustomerOrderStatus.FULFILLED;
        }
        return ingredients;
        
    }

    @Override
    public String toString()
    {
        return name;
    }

    public List<Ingredient> getGarnish() 
    {
        return garnish;
    }

    public int getLevel() 
    {
        return level;
    }

    public List<Ingredient> getRecipe() 
    {
        return recipe;
    }

    public CustomerOrderStatus getStatus() 
    {
        return status;
    }

    
    public void setStatus(CustomerOrderStatus status) 
    {
        this.status = status;
    }

    public String getRecipeDescription()
    {
        return ingredientListToString(recipe);
    }

    public String getGarnishDescription()
    {
        return ingredientListToString(garnish);
    }
    private String ingredientListToString(List<Ingredient> list)
    {
        String csList = "";
        for( Ingredient ing: list)
        {
            csList += ing.toString() + ", ";
        }
        csList =  csList.substring(0, csList.length() -2);
        return csList;
    }

}