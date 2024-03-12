package bakery;

/** 
 * This class is effectively an ingredient card in the name, and is mostly a wrapper for the String name it contains.
 */
public class Ingredient implements Comparable<Ingredient>
{
    private String name;
    /** 
     * The static HELPFUL_DUCK gives a reference to see if there is a helpful duck that can be substituted in for some other ingredient in a recipe. 
     */
    public static final Ingredient HELPFUL_DUCK = new Ingredient("helpful duck 𓅭"); // WHAT ARE YOU. Do I just check if ingredients are equal to you?
    private static final long serialVersionUID =1;
    
    /**
     * Creates an Ingredient object with the given name.
     * @param name
     * The name of the ingredient. This is the key part of the class, and will determine its equality and compariseon with other Ingredient objects.
     * It will also be what is given when toString() is called.
     */
    public Ingredient(String name)
    {
        this.name = name;
    }

    /** 
     * This will check if the object given in has the same name to determine equality.
     * If the object isn't an Ingredient object, then an exception will occur when trying to cast and it will return false
     * @param o     This is the object that will be compared.
     */
    @Override
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
     */
    public int compareTo(Ingredient ingredient)
    {
        return this.name.compareTo(ingredient.toString());
    }

    /**
     * The hashcode of this will be the hashcode of the name attribute
     */
    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    /**
     * When converted to string, an Ingredient object will return the name attribute 
     */
    @Override
    public String toString()
    {
        return name;
    }


}