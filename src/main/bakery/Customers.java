package bakery;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import bakery.CustomerOrder;
import bakery.CustomerOrder.CustomerOrderStatus;
import util.CardUtils;

/**
 * This class holds all information to do with the customers in the game, separating them into 3 different collections
 * There is the customerDeck collection, where a customer order is drawn from at the end of each turn
 * Then there is the activeCustomers collection, where the customers that players can actually fulfil are, which will be moved along every round.
 * Finally, there is the inactiveCustomers list, where customers that have either had their order fulfiled, or ones that left the shop before the players managed to fulfil their order go.
 */
public class Customers implements Serializable{
    /**
     * activeCustomers is a queue done by a linked list, but looking at the string Utils representation, 
     * it seems like item in the tail is considered the leftmost activeCustomer, and the item in the head is the rightmost, so it's reversed because of the descendingIterator call.
     */
    private Collection<CustomerOrder> activeCustomers; //Time for tedious casting
    private Collection<CustomerOrder> customerDeck; 
    private List<CustomerOrder> inactiveCustomers;
    private Random random;
    private static final long serialVersionUID =1;

    /**
     * Makes a customer object, which should be initialised at the beginning of the game, initialising the customer side of the game.
     * @param deckFile String of the path to the file containing the deck of CustomerOrders
     * @param random the specific random object instantiated with some specific seed.
     * @param layers The collection of all layers in the game
     * @param numPlayers The number of players for the game
     */
    public Customers(String deckFile, Random random, Collection<Layer> layers, int numPlayers)
    {
        this.random = random;
        initialiseCustomerDeck(deckFile, layers, numPlayers);   
        inactiveCustomers = new ArrayList<>(); 
        
        activeCustomers = new LinkedList<>();
        activeCustomers.add(null);
        activeCustomers.add(null);
        activeCustomers.add(null);
    }

    /**
     * This will draw a customer from the customerDeck and add it to the activeCustomers list.
     * It will also shuffle all of the cards right, adding the rightmost order to the inActiveCustomers list if there is one there.
     * @return The customer drawn from the deck. It will return null if the deck is empty
     */
    public CustomerOrder addCustomerOrder()
    {
        if(customerWillLeaveSoon())
            inactiveCustomers.add(timePasses());
        else   
            timePasses();
        CustomerOrder drawnCustomer = drawCustomer();
        activeCustomers.add(drawnCustomer);
        drawnCustomer.setStatus(CustomerOrderStatus.WAITING);
        
        if(customerWillLeaveSoon()) //set new impatient customer if applicable
            ((LinkedList<CustomerOrder>) activeCustomers).getFirst().setStatus(CustomerOrderStatus.IMPATIENT);
        
        return drawnCustomer; // I really have no clue what customer order object I'm meant to return here
    }

    /**
     * Determines if a customer will leave the activeCustomers deck when timePasses is called (so a round ends).
     * @return whether a card will leave the activeCustomers deck.
     */
    public boolean customerWillLeaveSoon()
    {
        if( ((LinkedList<CustomerOrder>)activeCustomers).getFirst() == null)
            return false;
        if(!customerDeck.isEmpty())
            return size() ==3;

        //so now we know that there is an order in the rightmost slot, and the deck is empty.
        // In this case, then a customer will only stay if there is a gap, so the middle element is null, and the tail has a customer
        return !(((LinkedList<CustomerOrder>)activeCustomers).getLast() != null && ((LinkedList<CustomerOrder>)activeCustomers).get(1) == null);
    }

    /**
     * This method draws a card from customerDeck, if it is there.
     * @return the customerOrder drawn from the deck, null if customerDeck is empty
     */
    public CustomerOrder drawCustomer()
    {
        if(!customerDeck.isEmpty())
            return ((Stack<CustomerOrder>)customerDeck).pop();
        else 
            return null;
    }

    /**
     * 
     * @return the activeCustomers list.
     */
    public Collection<CustomerOrder> getActiveCustomers()
    {
        return activeCustomers;
    }

    /**
     * 
     * @return the customerDeck stack.
     */
    public Collection<CustomerOrder> getCustomerDeck()
    {
        return customerDeck;
    }

    /**
     * This will return the CustomerOrders in activeCustomers that can be fulfilled given a player's hand.
     * @param hand the list of ingredients and layers in some players hands. 
     * @return The CustomerOrders in activeCustomers that can be fulfilled.
     */
    public Collection<CustomerOrder> getFulfilable(List<Ingredient> hand)
    {
        Collection<CustomerOrder> fulfillable = new ArrayList<>();
        
        for (CustomerOrder customerOrder : activeCustomers) 
        {
            if(customerOrder != null && customerOrder.canFulfill(hand))
                fulfillable.add(customerOrder);    
        }
        return fulfillable;
    }

    /**
     * This method gets all of the cards in inactiveCustomers with the matching status.
     * @param status the status that is being checked for.
     * @return all CustomerOrders in inactiveCustomers with the matching status.
     */
    public Collection<CustomerOrder> getInactiveCustomersWithStatus(CustomerOrderStatus status)
    {
        Collection<CustomerOrder> matching = new ArrayList<>();
        matching.addAll(inactiveCustomers.stream().filter(c -> c.getStatus() == status).toList()); // The extra matching object is needed to use the stream, since it only returns as a list, and I need a collection
        return matching;
    }

    /**
     * This method will initialise the deck of customers that the players will go through throughout the game, which will change depending on the number of players.
     * 
     * @param deckFile the path to the file containing all of the CustomerOrders 
     * @param layers the collection of all layers, used to check if an ingredient should be an Ingredient or Layer object
     * @param numPlayers The number of players, used to determine how many of each level of Customer should be put in the customerDeck.
     */
    private void initialiseCustomerDeck(String deckFile, Collection<Layer> layers, int numPlayers)
    {
        List<CustomerOrder> allCustomers = CardUtils.readCustomerFile(deckFile, layers);
        Collections.shuffle(allCustomers, random);
        customerDeck = new Stack<>();
        ArrayList<Integer> numCards = new ArrayList<>();
        switch (numPlayers) {
            case 2:
                Collections.addAll(numCards, 4,2,1);
                break;
            case 3, 4:
                Collections.addAll(numCards, 1,2,4);
                break;
            case 5:
                Collections.addAll(numCards, 0,1,6);
                break;
            default:
                break;
        }
        int cardLevel =1;
        for (Integer integer : numCards) {
            List<CustomerOrder> separatedDeck =  new ArrayList<>();
            for (CustomerOrder customer : allCustomers) {
                if(customer.getLevel() == cardLevel)
                    separatedDeck.add(customer);
            }        
            for (int i = 0; i < integer; i++) {
                customerDeck.add(separatedDeck.get(0));
                separatedDeck.remove(0);
            }
            cardLevel++;
        }
        Collections.shuffle(((Stack<CustomerOrder>)customerDeck), random);
    }

    /**
     * Returns whether activeCustomers list has no customerOrder objects in it.
     * @return boolean of if the activeCustomers list is empty
     */
    public boolean isEmpty()
    {
        return size() == 0;
    }

    /**
     * Gets the rightmost (head of list) element in the activeCustomers list
     * @return the rightmost element in activeCustomers. If there is no customer in the rightmost slot, it returns null.
     */
    public CustomerOrder peek()
    {
        //I hate this casting.
        return  ((LinkedList<CustomerOrder>) activeCustomers).peekFirst(); // remember, rightmost is the head of the list.
    }

    /**
     * Removes a customer from activeCustomer deck. Should mostly be used for fulfilling orders.
     * @param customer the customer to be removed
     */
    public void remove(CustomerOrder customer)
    {
        ((LinkedList<CustomerOrder>) activeCustomers).set(((LinkedList<CustomerOrder>) activeCustomers).indexOf(customer), null); // need to keep null entries in so that gaps are represented properly, which is only really necessary for the sake of printing with gaps.
    }

    /**
     * 
     * @return the number of customers in activeCustomers.
     */
    public int size()
    {
        //Can't actually use the size method because there are actually always 3 elements, some might just be null.
        return (int) activeCustomers.stream().filter(c -> c != null).count();
    }


    /**
     * This method will remove the first (rightmost) element in the activeCustomers list if it is due to leave
     * This method will always remove an element from activeCustomers, either the rightmost element (head of the list) which could be null, or a null gap.
     * @return the CustomerOrder that is leaving from the rightmost position. This will return null if there is no CustomerOrder due to leave.
     */
    public CustomerOrder timePasses()
    {
        if(customerWillLeaveSoon())
            return ((LinkedList<CustomerOrder>) activeCustomers).removeFirst();
        //If there isn't a customer leaving with an empty deck, then that means there is a null element somewhere, and the rightmost null element should be removed when time passes to shuffle everything along.
        //This logic would break if activeCustomers was more than 3 elements though.
        if(customerDeck.isEmpty())
        {
            for (int i = 0; i < activeCustomers.size(); i++) 
            {
                if( ((LinkedList<CustomerOrder>) activeCustomers).get(i) == null)
                {
                    return ((LinkedList<CustomerOrder>) activeCustomers).remove(i);
                }        
            }
        }
        //in this case, there isn't a customer leaving soon, but customerDeck isn't empty
        // here, you actually remove the leftmost null element, since you are trying to put a card in on the left, rather than just shuffling everything right one.
        for (int i = activeCustomers.size() -1; i > -1; i--) 
        {
            if( ((LinkedList<CustomerOrder>) activeCustomers).get(i) == null)
            {
                return ((LinkedList<CustomerOrder>) activeCustomers).remove(i);
            }        
        }
        return null; // this is actually unreachable.
        
    }

}