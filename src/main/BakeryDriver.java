import util.ConsoleUtils;

import java.io.BufferedReader;
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
        String s = "what, how, do, you,";
        String[] lsit = s.split(", ");
        for (String string : lsit) {
            System.out.println(string);
        }
    }
    private static Reader FileReader(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'FileReader'");
    }

}