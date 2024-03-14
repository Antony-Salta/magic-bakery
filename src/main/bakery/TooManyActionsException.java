package bakery;
/**
 * This exception is thrown when a player tries to perform an action after already using all of their actions
 */
public class TooManyActionsException extends IllegalStateException
{
    /**
     * Makes the exception with no other arguments, so does what the super class does
     */
    public TooManyActionsException()
    {
        super();
    }
    /**
     * Creates this exception with the given message
     * @param msg The message to be printed when this error is thrown.
     */
    public TooManyActionsException(String msg)
    {
        super(msg);
    }
}