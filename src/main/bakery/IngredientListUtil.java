package bakery;

import java.util.Collections;
import java.util.List;


/**
 * This class is just here to give the method for converting a list of ingredients to a pretty string, because this 
 * is a common requirement for classes that don't inherit.
 * 
 * Another way to do this would be to make a wrapper class for List<Ingredient> that does this specifically, but that would slightly change the attributes of existing parts of the UML diagram.
 */
public abstract class IngredientListUtil {
    /**
     * 
     * @param list
     * A list is inputted, will then be sorted.
     * This list will be used to generate a comma separated string of the items in the list.
     * 
     * @return csList
     * the string csList, comma separated list.
     * This list will mark duplicate ingredients instead of outputting it twice. 
     * e.g. it will be: "Chocolate, Eggs (x2), Sugar"
     * instead of "Chocolate, Eggs, Eggs, Sugar"
     */
    protected static String stringFromIngList(List<Ingredient> list)
    {
        /**
         * 
         */
        Collections.sort(list);
        String csList = "";
        Ingredient prevIng = list.get(0);
        int numSame = 0;
        // e.g. c, e, e, e , s
        for( Ingredient ing: list)
        {
            if(ing != prevIng)
            {
                csList += prevIng.toString();
                if(numSame > 1)
                    csList += " (x" + numSame + ")";
                csList += ", ";
                numSame = 1;
                prevIng = ing;
            }
            else
                numSame ++;
        }
        
        csList += list.get(list.size() -1);
        if(numSame > 1)
            csList += " (x" + numSame + ")";

        return  csList;
    }
}
