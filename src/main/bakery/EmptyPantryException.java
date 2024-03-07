package main.bakery;
public class EmptyPantryException extends RuntimeException
{
    public EmptyPantryException(String msg, Throwable e)
    {
        super(msg, e);
    }
}