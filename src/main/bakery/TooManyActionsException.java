package bakery;

/**
 * This exception will be thrown if a player attempts to do too many actions in one turn.
 * @author Antony Salta
 * @version 1.0
 *
 * This isn't the correct version number, but I haven't been tracking until now
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