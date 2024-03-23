package bakery;

import java.io.Serializable;

/** 
 * This class is effectively an ingredient card in the name, and is mostly a wrapper for the String name it contains.
 * @author Antony Salta
 * @version 1.0
 *
 * This isn't the correct version number, but I haven't been tracking until now
 */
public class Ingredient implements Comparable<Ingredient>, Serializable
{
    private String name;
    /** 
     * The static HELPFUL_DUCK gives a reference to see if there is a helpful duck that can be substituted in for some other ingredient in a recipe. 
     */
    public static final Ingredient HELPFUL_DUCK = new Ingredient("helpful duck 𓅭");
    private static final long serialVersionUID =1;
    
    /**
     * Creates an Ingredient object with the given name.
     * @param name The name of the ingredient. This is the key part of the class, and will determine its equality and comparison with other Ingredient objects.
     * It will also be what is given when toString() is called.
     */
    public Ingredient(String name)
    {
        this.name = name;
    }

    @Override
    /** 
     * This will check if the object given in has the same name to determine equality.
     * If the object isn't an Ingredient object, then an exception will occur when trying to cast and it will return false
     * @param o     This is the object that will be compared.
     * @return whether the given object is equal to this Ingredient or not.
     */
    public boolean equals(Object o) {
        
        try {
            Ingredient comparison = (Ingredient) o;
            return this.name.equals(comparison.toString()); //for some reason I can pull name even though it's private, i guess since it's the same class
        } catch (Exception e) {
            return false;
        }

    }
    
    /**
     * The result of this comparison will be the result of the string comparison of their name attributes, given by toString()
     * @param ingredient    The Ingredient object that is being compared with this one
     * @return  the result of String compareTo of this ingredient's name with the other ingredient's name.
     */
    public int compareTo(Ingredient ingredient)
    {
        return this.name.compareTo(ingredient.toString());
    }

    @Override
    /**
     * The hashcode of this will be the hashcode of the name attribute
     * @return the value of hashCode on the name attribute.
     */
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    /**
     * When converted to string, an Ingredient object will return the name attribute
     * @return the name attribute.
     */
    public String toString()
    {
        return name;
    }


}