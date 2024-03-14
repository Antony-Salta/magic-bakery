package bakery;
/**
 * This exception is thrown when there are errors to do with ingredients, like having empty/null recipes, or trying to use an ingredient that isn't available
 */
public class WrongIngredientsException extends IllegalArgumentException
{
    /**
     * Makes the exception with no other arguments, so it only calls the super constructor.
     */
    public WrongIngredientsException()
    {
        super();
    }
    /**
     * Makes the exception with the given message
     * @param msg The message to be printed when this exception is thrown
     */
    public WrongIngredientsException(String msg)
    {
        super(msg);
    }
}