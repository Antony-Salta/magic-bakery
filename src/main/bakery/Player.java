package main.bakery;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Player 
{
    private List<Ingredient> hand;
    private String name;
    private final long serialVersionUID;

    public Player(String name)
    {
        this.name = name;
    }
    
    public void addToHand(List<Ingredient> ingredients)
    {
        hand.addAll(ingredients);
    }
    public void addToHand(Ingredient ingredient)
    {
        hand.add(ingredient);
    }

    public boolean hasIngredient(Ingredient ingredient)
    {
        return hand.contains(ingredient);
    }

    public void removeFromHand(Ingredient ingredient)
    {
        hand.remove(ingredient);
    }

    public List<Ingredient> getHand()
    {
        Collections.sort(hand);
        return hand;
    }

    public String getHandStr()
    {
        Collections.sort(hand);
        String csList = "";
        Ingredient prevIng = hand.get(0);
        int numSame = 0;
        // e.g. c, e, e, e , s
        for( Ingredient ing: hand)
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
        
        csList += hand.get(hand.size() -1);
        if(numSame > 1)
            csList += " (x" + numSame + ")";

        return  csList;
    }

    @Override
    public String toString()
    {
        return name;
    }

    
}