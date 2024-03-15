package util;

import java.io.Console;
import java.io.File;
import java.io.Serializable;
import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import bakery.CustomerOrder;
import bakery.Ingredient;
import bakery.MagicBakery;
import bakery.MagicBakery.ActionType;
import bakery.Player;

public class ConsoleUtils implements Serializable
{
    private Console console;

    /** 
     * makes a ConsoleUtils object, which initialises a console object.
     */
    public ConsoleUtils()
    {
        console = System.console();
    }

    /**
     * Reads a single line of text from the console
     * @return the line read in from the console, not including line-termination characters, or null if the stream is closed.
     */
    public String readLine()
    {
        return console.readLine();
    }
    /**
     * 
     * @param fmt A format string 
     * @param args arguments referenced by the format specifiers in the format string. Extra args beyond those specified by the format string are ignored.
     * @return the line read in from the console, not including line-termination characters, or null if the stream is closed.
     */
    public String ReadLine(String fmt, Object... args)
    {
        return console.readLine(fmt, args);
    }

    /**
     * Prompts the user to choose an action from a selection.
     * @param prompt the prompt to be printed to the user
     * @param bakery the MagicBakery object tracking the state of the game
     * @return the ActionType chosen by the user.
     */
    public ActionType promptForAction(String prompt, MagicBakery bakery)
    {
        Collection<Object> allowedActions = new ArrayList<>();
        allowedActions.add(ActionType.DRAW_INGREDIENT);
        allowedActions.add(ActionType.REFRESH_PANTRY);
        if(!bakery.getFulfilableCustomers().isEmpty())
            allowedActions.add(ActionType.FULFIL_ORDER);
        if(!bakery.getBakeableLayers().isEmpty())
            allowedActions.add(ActionType.BAKE_LAYER);
        if(!bakery.getCurrentPlayer().getHand().isEmpty())
            allowedActions.add(ActionType.PASS_INGREDIENT);
        
        return (ActionType) promptEnumerateCollection(prompt, allowedActions);
    }

    /**
     * 
     * Prompts the user to choose a customer from a selection.
     * @param prompt the prompt to be printed to the user
     * @param customers the customers that can be chosen from.
     * @return the Customer chosen by the user.
     */
    public CustomerOrder promptForCustomer(String prompt, Collection<CustomerOrder> customers)
    {
        Collection<Object> asObjects = new ArrayList<>(customers);
        return (CustomerOrder) promptEnumerateCollection(prompt, asObjects);
    }

    /**
     * Prompts the user to choose a player from one of the players in the game.
     * @param prompt The prompt to be printed to the user
     * @param bakery The MagicBakery object storing the state of the game
     * @return the player chosen by the player. There is no option to choose the current player.
     */
    public Player promptForExistingPlayer(String prompt, MagicBakery bakery)
    {
        Collection<Object> otherPlayers = new ArrayList<>(bakery.getPlayers());
        otherPlayers.remove(bakery.getCurrentPlayer());
        return (Player) promptEnumerateCollection(prompt, otherPlayers);
    }

    /**
     *  Prompts the user to enter the file path for some stored data, generally either one of the cards' files, or a save of the game.
     * This does not check the validity of the path enterred however.
     * @param prompt The prompt to be printed to the user
     * @return the File found by the user.
     */
    public File promptForFilePath(String prompt)
    {
        //Doesn't need to check file validity, so no need for try-catch.
        System.out.println(prompt);
        return new File(readLine());
        
    }

    /**
     * Prompts the user to choose an ingredient
     * @param prompt The prompt to be printed to the user
     * @param ingredients the list of ingredients that can be chosen from
     * @return The ingredient the player chooses
     */
    public Ingredient promptForIngredient(String prompt, Collection<Ingredient> ingredients)
    {
        Collection<Object> asObjects = new ArrayList<>(ingredients);
        return (Ingredient) promptEnumerateCollection(prompt, asObjects);
    }

    /**
     * Prompts the user for player names when initialising the game. It will force them to enter at least 2 names.
     * They can enter up to 5 names, but after the second, enterring an empty string will allow them to stop.
     * @param prompt The prompt to be printed to the user
     * @return The list of names inputted by the user as a list of strings.
     */
    public List<String> promptForNewPlayers(String prompt)
    {
        List<String> names = new ArrayList<>();
        while(names.size() < 5)
        {
            System.out.println(prompt);
            if(names.size() > 1)
                System.out.println("You can enter another name or stop by just hitting enter. There are currently " + names.size() + "/5 players in this game.");
            String name = console.readLine();
            if(name.equals(""))
            {
                if(names.size() < 2)
                {
                    System.out.println("You can't have an empty name. Please try again.");
                    continue;
                }
                else
                    break;
            }
            for (String storedName : names) {
                if(storedName.toLowerCase().equals(name))
                {
                    System.out.println("You can't have the same name as another player, even with different cases. Please try again");
                    continue;
                }
            }
            names.add(name);
        }
        return names;
    }

    /**
     * Prompts the user if they want to load a game or start a new game.
     * @param prompt The prompt to be printed to the user
     * @return true if they choose to start a new game, false if they choose to load a game.
     */
    public boolean promptForStartLoad(String prompt)
    {
        System.out.println(prompt + " [S]tart/[l]oad");
        if(readLine().toLowerCase().charAt(0) == 's')
            return true;
        else if(readLine().toLowerCase().charAt(0) == 'l')
            return false;
        System.out.println("Please enter either y or n to this question:");
        return promptForYesNo(prompt);
    }

    /**
     * Prompts the user to choose yes or no.
     * @param prompt The prompt to be printed to the user
     * @return true if they choose yes, false if they choose no.
     */
    public boolean promptForYesNo(String prompt)
    {
        System.out.println(prompt + " [Y]es/[N]o");
        String choice = readLine();
        if(choice.toLowerCase().charAt(0) == 'y')
            return true;
        else if(choice.toLowerCase().charAt(0) == 'n')
            return false;
        System.out.println("Please enter either y or n to this question:");
        return promptForYesNo(prompt);
    }

    /**
     * Prompts the user to choose between various numbered items in a collection. This method is used by the other methods, like promptForIngredient or customer.
     * If the collection only has one item, then that item is returned by default without printing the prompt.
     * @param prompt The prompt to be printed to the user
     * @param collection the collection being enumerated through
     * @return the object that was chosen.
     * @throws IllegalArgumentException if the collection given is null or empty
     */
    private Object promptEnumerateCollection(String prompt, Collection<Object> collection)
    {
        if(collection == null || collection.size() ==0)
            throw new IllegalArgumentException("the collection cannot be null.");
        if(collection.size() == 1) // No point asking the choice if there is no choice.
            return collection.toArray()[0];

        Object[] arr = collection.toArray();
        for (int i = 0; i < arr.length; i++) {
                prompt += "[" + (i+1) + "]" + arr[i].toString() + ", ";
        }
        prompt = prompt.substring(0, prompt.length()-2);
        System.out.println(prompt);
        String input = readLine();
        try {
            
            int choice = Integer.parseInt(input) -1;
            if(choice <0 || choice > arr.length -1)
            {
                System.out.println("Please enter a number within range");
                return promptEnumerateCollection(prompt, collection);
            }
            else
            {
                return arr[choice];
            }
        } catch (Exception e) {
            //This part checks if they input the string instead of the number
            List<String> options = collection.stream().map(o -> o.toString().toLowerCase()).collect(Collectors.toList());
            List<Object> objectList = collection.stream().collect(Collectors.toList());
            for (String string : options) {
                    if(string.equals(input.toLowerCase()))
                        return objectList.get(options.indexOf(string));
            }

            System.out.println("please enter a valid number from the choices shown. Try again.");
            return promptEnumerateCollection(prompt, collection);
        }
    }
}