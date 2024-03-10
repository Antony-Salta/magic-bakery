package bakery;
public class WrongIngredientsException extends IllegalArgumentException
{
    public WrongIngredientsException()
    {
        super();
    }
    public WrongIngredientsException(String msg)
    {
        super(msg);
    }
}