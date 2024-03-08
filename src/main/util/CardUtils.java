package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bakery.Layer;
import bakery.CustomerOrder;
import bakery.Ingredient;

public  abstract class CardUtils
{
    /**
     * 
     * @param path the path to the customers file.
     * @param layers A list of all layers to see if something in the recipe is an ingredient or layer.
     * @return The list of customer orders generated.
     */
    public static List<CustomerOrder> readCustomerFile(String path, Collection<Layer> layers)
    {
        List<CustomerOrder> customers = null; 
        try (BufferedReader read = new BufferedReader(new FileReader(path))) 
        {
            //Structure of the csv is: level, name, list of ingredients/layers to make recipe, list of ingredients/layers to make garnish.
            //First 4 entries set this out, so can be skipped.
            //elements in the lists are separated with semi-colons
            // If there is no garnish, the line will just end with a comma
            read.readLine(); //skip first line
            String line = read.readLine(); 
            customers = new ArrayList<>();

            while(!line.equals(null)) 
            {
                String[] parts = line.split(", ");
                int level = Integer.parseInt(parts[0]);
                String name = parts[1];

                if(parts.length == 3)// If there is no garnish, then there will only be 3 parts, and the recipe will end in a comma, sometimes, it's inconsisten, hence the replace.
                    parts[2] = parts[2].replace(",", "");

                String[] recipeIngredients = parts[2].split("; ");
                ArrayList<Ingredient> recipe = new ArrayList<>();
                for (String ingredient : recipeIngredients) {
                    recipe.add(makeCorrectIngredient(ingredient, layers));
                }
                
                ArrayList<Ingredient> garnish = null;
                if(parts.length != 3)
                {
                    recipeIngredients = parts[3].split("; ");
                    garnish = new ArrayList<>();
                    for (String ingredient : recipeIngredients) {
                        garnish.add(makeCorrectIngredient(ingredient, layers));
                    }
                }
                    
                customers.add(new CustomerOrder(name, recipe, garnish, level));
                line = read.readLine();
            }
            
        } catch (IOException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }
        return customers;
    }
    
    /**
     * This function checks the name given against all layers to determine if it is a layer, and returns that layer object if so.
     * @param name the name of the ingredient
     * @param layers The list of all layers
     * @return the appropriate ingredient or layer object with all informatioin
     */
    private static Ingredient makeCorrectIngredient(String name, Collection<Layer> layers)
    {
        Layer test = new Layer(name, null);
        for (Layer layer : layers) { 
        // this is seeing if the name of a layer has been given, rather than an ingredient. If so, then add that layer into the recipe, with the full layer information.
            if(layer.equals(test))
            {
                return layer;
            }
        }
        return new Ingredient(name);
    }
}