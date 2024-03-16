import util.ConsoleUtils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import bakery.CustomerOrder;
import bakery.Ingredient;
import bakery.Layer;
import bakery.MagicBakery;
import bakery.Player;
import bakery.CustomerOrder.CustomerOrderStatus;
import bakery.MagicBakery.ActionType;


public class BakeryDriver {
    public BakeryDriver() {
    }
    public static void main(String[] args)  
    {
        
        
        ConsoleUtils console = new ConsoleUtils();
        MagicBakery bakery = null;
        while(!console.promptForStartLoad("Please choose whether you would like to start a new game or load an existing one."))
        {
            try {
                File gameSave = console.promptForFilePath("Please enter the path to the file storing the game save.");
                bakery = MagicBakery.loadState(gameSave);
                break; // If it works, don't prompt again
            } catch (IOException e) {
                System.out.println("Error in reading the file. Try saving and closing any unnecessary files. You'll now be \n");
            } catch(ClassNotFoundException e)
            {
                System.out.println("This file seems to be an invalid save state.\n");
            }
        }
        if(bakery == null)
        {
            //TODO: put in an actual random seed.
            long seed = 24;
            bakery = new MagicBakery(seed, "io/ingredients.csv", "io/layers.csv");
        bakery.startGame(console.promptForNewPlayers("Please enter the name of the player. The players will go in this order, and there have to be between 2 and 5 players."), "io/customers.csv");
        }
        
        playGame(bakery, console);
    }
    /** 
     * This function will do the main game loop, going between turns, and changing things as each new round starts.
     */
    private static void playGame(MagicBakery bakery, ConsoleUtils console)
    {
        while(!(bakery.getCustomers().getCustomerDeck().isEmpty() && bakery.getCustomers().isEmpty())) //Overall game loop
        {
            do // loop for a round of the game
            {
                while(!bakery.endTurn()) // loop for the actions each player takes on their turn
                {
                    bakery.printGameState();
                    ActionType choice = console.promptForAction("Please choose which action you would like to take: ", bakery);
                    //TODO: put this into a separate function, maybe each case have a separate function to be able to document it better.
                    switch(choice)
                    {
                        case DRAW_INGREDIENT:
                            if(console.promptForYesNo("Would you like to draw from the pantry? If you don't, then you will draw from the pantry deck."))
                            {
                                HashSet<Ingredient> pantry = new HashSet<>(bakery.getPantry()); // Gets rid of weird double choices.
                                Ingredient chosen = console.promptForIngredient("Please choose which ingredient from the pantry you would like to draw: ", pantry);
                                bakery.drawFromPantry(chosen);
                            }
                            else
                                bakery.getCurrentPlayer().addToHand(bakery.drawFromPantryDeck());
                            break;
                        
                        case PASS_INGREDIENT:
                            //Player chosenPlayer;
                            // if(bakery.getPlayers().size() ==2) // This bit just avoids choosing between 1 option
                            // {
                            //     for (Player player : bakery.getPlayers()) {
                            //         if(!player.equals(bakery.getCurrentPlayer()))
                            //         {
                            //             chosenPlayer = player;
                            //             break;
                            //         }
                            //     }
                            // }
                            Player chosenPlayer = console.promptForExistingPlayer("Please choose the player that you would like to pass an ingredient to: ", bakery);
                            Set<Ingredient> IngredientChoices = new HashSet<Ingredient>(bakery.getCurrentPlayer().getHand()); // So that they don't get duplicate choices
                            Ingredient passedIngredient = console.promptForIngredient("Please choose the ingredient that you would like to pass to " + chosenPlayer.toString() + ": ", IngredientChoices);
                            bakery.passCard(passedIngredient, chosenPlayer);    
                            break;

                        case BAKE_LAYER:
                            Collection<Ingredient> bakeables = new ArrayList<>(bakery.getBakeableLayers());
                            Layer layer = (Layer) console.promptForIngredient("Please choose the layer that you would like to bake: ", bakeables);
                            bakery.bakeLayer(layer);
                            break;

                        case FULFIL_ORDER:
                            Collection<CustomerOrder> fulfilables = new ArrayList<>(bakery.getFulfilableCustomers());
                            CustomerOrder order = console.promptForCustomer("Please choose the customer order that you would like to fulfil: ", fulfilables);

                            boolean garnish = false;
                            if(bakery.getGarnishableCustomers().contains(order)) // Only prompt if it's even possible to garnish the order.
                            {
                                garnish = console.promptForYesNo("Please choose whether you would like to try to garnish this order: ");
                            }
                            List<Ingredient> used = bakery.fulfillOrder(order, garnish);
                            //I'm doing this here instead of in fulfillOrder, because that's what the great UML diagram decreed. Even though we do it in method in bakeLayer
                            for (Ingredient ingredient : used) {
                                bakery.getCurrentPlayer().getHand().remove(ingredient);
                            }

                            LinkedList<CustomerOrder> activeCustomers = (LinkedList<CustomerOrder>) bakery.getCustomers().getActiveCustomers();
                            activeCustomers.set(activeCustomers.indexOf(order), null); // remove the fulfiled order from activeCustomers
                            
                            break;

                        case REFRESH_PANTRY:
                            bakery.refreshPantry();
                            break;
                    }
                    

    
                }
                //I'm giving the option to save at the end of each turn.
                while(console.promptForYesNo("Would you like to save the game now? "))
                {
                    try {
                        File saveFile = console.promptForFilePath("Please enter the path to the file where you would like to save the file: ");
                        bakery.saveState(saveFile);
                        System.out.println("File successfully saved! You can quit anytime by enterring Ctrl+C");
                        break;
                    } catch (IOException e) {
                        System.out.println("Writing the state to file didn't work. You may not be able to write to the file you specified.");
                    }
                }
                
            }while(!bakery.getCurrentPlayer().equals((Player) bakery.getPlayers().toArray()[0])); //End of the round once the currentPlayer loops back to being the first player.
            bakery.getCustomers().addCustomerOrder();

            System.out.println("\n==================================");
            System.out.println("\tEND OF ROUND: CUSTOMERS ARE MOVING");
            System.out.println("==================================\n");
            
        }
    }

}