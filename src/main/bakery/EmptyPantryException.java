package bakery;
/** 
 * This exception occurs when the pantryDeck is empty, even after getting the ingredients from pantry discard.
 * @author Antony Salta
 * @version 1.0
 *
 * This isn't the correct version number, but I haven't been tracking until now
 */
//for some reason it's unchecked, even though I need to make error handling around it, since the player is meant to just use what's in their hand.
public class EmptyPantryException extends RuntimeException
{
    /**
     * Makes an EmptyPantryException with the given message and e as the cause
     * @param msg The message to be printed when this is thrown.
     * @param e The cause of this exception being thrown
     */
    public EmptyPantryException(String msg, Throwable e)
    {
        super(msg, e);
    }
}