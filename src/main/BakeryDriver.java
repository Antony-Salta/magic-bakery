import util.ConsoleUtils;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.io.Reader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import bakery.MagicBakery;


public class BakeryDriver {
    private ConsoleUtils console;
    public BakeryDriver() {
    }
    public static void main(String[] args)  
    {
        
        
        //make a seed
        //MagicBakery magicBakery = new MagicBakery(0, "io/ingredients.csv", "io/layers.csv")
        Console console = System.console();
        
        System.out.println("input something");
        String in =  console.readLine();
        if(in.equals(""))
            System.out.println("Holds empties");
    }
    private static Reader FileReader(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'FileReader'");
    }

}