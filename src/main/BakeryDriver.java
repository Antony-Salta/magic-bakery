import util.ConsoleUtils;
import bakery.MagicBakery;


public class BakeryDriver {
    private ConsoleUtils console;
    public BakeryDriver() {
    }
    public static void main(String[] args)  
    {
        //make a seed
        //MagicBakery magicBakery = new MagicBakery(0, "../../io/ingredients.csv", "../../io/layers.csv")
        String s = "what, how, do, you,";
        String[] lsit = s.split(", ");
        for (String string : lsit) {
            System.out.println(string);
        }
    }

}