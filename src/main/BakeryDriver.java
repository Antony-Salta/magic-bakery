import util.ConsoleUtils;

import java.io.Reader;

import bakery.MagicBakery;


public class BakeryDriver {
    public BakeryDriver() {
    }
    public static void main(String[] args)  
    {
        
        //make a seed
        ConsoleUtils console = new ConsoleUtils();
        long seed = 24;
        MagicBakery magicBakery = new MagicBakery(seed, "io/ingredients.csv", "io/layers.csv");
        magicBakery.startGame(console.promptForNewPlayers("Please enter the name of the player. The players will go in this order, and there have to be between 2 and 5 players."), "io/customers.csv");
        
    }
    private static Reader FileReader(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'FileReader'");
    }

}