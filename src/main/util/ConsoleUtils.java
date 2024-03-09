package util;

import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import bakery.CustomerOrder;
import bakery.Ingredient;
import bakery.MagicBakery;
import bakery.MagicBakery.ActionType;
import bakery.Player;

public class ConsoleUtils
{
    private Console console;
    public ConsoleUtils()
    {
        console = System.console();
    }
    public String readLine()
    {
        return console.readLine();
    }
    public String ReadLine(String fmt, Object... args)
    {
        return console.readLine(fmt, args);
    }
    public ActionType promptForAction(String prompt, MagicBakery bakery)
    {
        Collection<Object> allowedActions = new ArrayList<>();
        allowedActions.add(ActionType.DRAW_INGREDIENT);
        allowedActions.add(ActionType.REFRESH_PANTRY);
        if(!bakery.getFulfillableCustomers().isEmpty())
            allowedActions.add(ActionType.FULFIL_ORDER);
        if(!bakery.getBakeableLayers().isEmpty())
            allowedActions.add(ActionType.BAKE_LAYER);
        if(!bakery.getCurrentPlayer().getHand().isEmpty())
            allowedActions.add(ActionType.PASS_INGREDIENT);
        return (ActionType) promptEnumerateCollection(prompt, allowedActions);
    }
    public CustomerOrder promptForCustomer(String prompt, Collection<CustomerOrder> customers)
    {
        return (CustomerOrder) promptForCustomer(prompt, customers); //Weirdly, there's no issue with this collection of not objects, maybe because there's no explicit instance, like an ArrayList being made
    }
    public Player promptForExistingPlayer(String prompt, MagicBakery bakery)
    {
        Collection<Object> otherPlayers = new ArrayList<>(bakery.getPlayers());
        otherPlayers.remove(bakery.getCurrentPlayer());
        return (Player) promptEnumerateCollection(prompt, otherPlayers);
    }

    public File promptForFilePath(String prompt)
    {
        //Doesn't need to check file validity, so no need for try-catch.
        System.out.println(prompt);
        return new File(readLine());
        
    }
    public Ingredient promptForIngredient(String prompt, Collection<Ingredient> ingredients)
    {
        return promptForIngredient(prompt, ingredients);
    }

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

    public boolean promptForYesNo(String prompt)
    {
        System.out.println(prompt + " [Y]es/[N]o");
        if(readLine().toLowerCase().charAt(0) == 'y')
            return true;
        else if(readLine().toLowerCase().charAt(0) == 'n')
            return false;
        System.out.println("Please enter either y or n to this question:");
        return promptForYesNo(prompt);
    }

    private Object promptEnumerateCollection(String prompt, Collection<Object> collection)
    {
        Object[] arr = collection.toArray();
        for (int i = 0; i < arr.length; i++) {
                prompt += "[" + (i+1) + "]" + arr[i].toString() + ", ";
        }
        String input = readLine();
        try {
            
            int choice = Integer.parseInt(input);
            if(choice <1 || choice > arr.length)
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