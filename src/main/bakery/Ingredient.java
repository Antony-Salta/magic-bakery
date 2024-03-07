package main.bakery;


public class Ingredient implements Comparable<Ingredient>
{
    private String name;
    public final Ingredient HELPFUL_DUCK = new Ingredient("Helpful Duck"); // WHAT ARE YOU. Do I just check if ingredients are equal to you?
    private final long serialVersionUID;
    
    public Ingredient(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        try {
            Ingredient comparison = (Ingredient) o;
            return this.name.equals(comparison.toString()); //for some reason I can pull name even though it's private, i guess since it's the same class
        } catch (Exception e) {
            return false;
        }

    }
    
    public int compareTo(Ingredient ingredient)
    {
        return this.name.compareTo(ingredient.toString());
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return name;
    }


}