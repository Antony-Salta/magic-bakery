package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import bakery.Layer;
import bakery.CustomerOrder;
import bakery.Ingredient;

public  final class CardUtils
{
    //makes this uninstantiable
    private CardUtils(){}

    /**
     * Reads the file with all of the CustomerOrders and returns them in a list
     * @param path the path to the customers file.
     * @param layers A list of all layers to see if something in the recipe is an ingredient or layer.
     * @return The list of customer orders generated.
     * @throws IOException If the customer file cannot be read
     */
    public static List<CustomerOrder> readCustomerFile(String path, Collection<Layer> layers) throws IOException
    {
        List<CustomerOrder> customers = null; 
        BufferedReader read = new BufferedReader(new FileReader(path)); 
        
        //Structure of the csv is: level, name, list of ingredients/layers to make recipe, list of ingredients/layers to make garnish.
        //First 4 entries set this out, so can be skipped.
        //elements in the lists are separated with semi-colons
        // If there is no garnish, the line will just end with a comma
        read.readLine(); //skip first line
        String line = read.readLine(); 
        customers = new ArrayList<>();

        while(line != null) 
        {
            customers.add(stringToCustomerOrder(line, layers));
            line = read.readLine();
        }

        read.close();
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
        for (Layer layer : layers) { 
        // this is seeing if the name of a layer has been given, rather than an ingredient. If so, then add that layer into the recipe, with the full layer information.
            if(layer.toString().equals(name))
            {
                return layer;
            }
        }
        return new Ingredient(name);
    }
    /**
     * This reads the ingredients file and returns all of the ingredients as a list.
     * @param path the String path to the Ingredients.csv file
     * @return A list of ingredients in the file
     * @throws IOException If the ingredient file cannot be read
     */
    public static List<Ingredient> readIngredientFile(String path) throws IOException
    {
        //format is the name of the ingredient, and the quantity of that ingredient.
        List<Ingredient> ingredients = new ArrayList<>();
        BufferedReader read = new BufferedReader(new FileReader(path));
        read.readLine(); // skip first line
        String line = read.readLine();
        while(line != null)
        {
            ingredients.addAll(stringToIngredients(line));
            line = read.readLine();
        }
        read.close();
        return ingredients;
    }

    /**
     * This function reads the layers stored in the layers file and returns it as a list.
     * @param path the path to the layers.csv file
     * @return the list of layers read from the file
     * @@throws IOException If the layer file cannot be read
     */
    public static List<Layer> readLayerFile(String path) throws IOException
    {
        //format is the name of the layer, then the recipe separated by semi-colons
        List<Layer> layers = new ArrayList<>();
        BufferedReader read = new BufferedReader(new FileReader(path));
        read.readLine(); //skip first line
        String line = read.readLine();
        while(line != null)
        {
            layers.addAll(stringToLayers(line));
            line = read.readLine();
        }
        
        //TODO Figure out if there are meant to be a limited number of layers or not.
        //For now I'm gonna say no.
        // layers.addAll(layers);
        // layers.addAll(layers);
        //This little bit makes it so that there are 4 of each layer, since that's the amount it's meant to be
        read.close();
        return layers;
    }

    /**
     * Makes a CustomerOrder object as described by a comma separated string.
     * @param str the string giving the information for a customer order
     * @param layers The layers that are used to check if an ingredient is a layer of just an ingredient
     * @return the customer order described by the string
     */
    private static CustomerOrder stringToCustomerOrder(String str, Collection<Layer> layers)
    {
        String[] parts = str.split(",");
        int level = Integer.parseInt(parts[0]);
        String name = parts[1].strip();

        if(parts.length == 3)// If there is no garnish, then there will only be 3 parts, and the recipe will end in a comma, sometimes, it's inconsistent, hence the replace.
            parts[2] = parts[2].replace(",", "");

        String[] recipeIngredients = parts[2].split(";");
        ArrayList<Ingredient> recipe = new ArrayList<>();
        for (String ingredient : recipeIngredients) {
            recipe.add(makeCorrectIngredient(ingredient.strip(), layers));
        }
        
        ArrayList<Ingredient> garnish = null;
        if(parts.length != 3)
        {
            recipeIngredients = parts[3].split(";");
            garnish = new ArrayList<>();
            for (String ingredient : recipeIngredients) {
                garnish.add(makeCorrectIngredient(ingredient.strip(), layers));
            }
        }
        return new CustomerOrder(name, recipe, garnish, level);
                    
    }
    /**
     * Returns a list of ingredients given the appropriate string
     * @param str A line of the ingredients file.
     * @return The list of ingredients made from that line
     */
    private static List<Ingredient> stringToIngredients(String line)
    {
        List<Ingredient> ingredients = new ArrayList<>();
        String[] parts = line.split(",");
        String name = parts[0];
        int quantity = Integer.parseInt(parts[1].strip());
        for (int j = 0; j < quantity; j++) {
            ingredients.add(new Ingredient(name));
        }
        return ingredients;
    }
    /**
     * Returns a list of layers given the appropriate string
     * @param line: the line of the layers file
     * @return The list of layers in the line, 4 of the layer described.
     */
    private static List<Layer> stringToLayers(String line)
    {
            List<Layer> layers = new ArrayList<>();
            String[] parts = line.split(",");
            String name = parts[0];
            String[] ingredients = parts[1].split(";");
            List<Ingredient> recipe = new ArrayList<>();
            
            for (String ingName : ingredients) {
                recipe.add(new Ingredient(ingName.strip()));
            }
            layers.add(new Layer(name, recipe));
            //TODO Figure out if there are meant to be a limited number of layers or not.
            layers.addAll(layers);
            layers.addAll(layers);
            //This little bit makes it so that there are 4 of each layer, since that's the amount it's meant to be
        return layers;
    }
}