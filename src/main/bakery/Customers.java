package bakery;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import bakery.CustomerOrder;
import bakery.CustomerOrder.CustomerOrderStatus;
import util.CardUtils;

public class Customers{
    private LinkedList<CustomerOrder> activeCustomers; // collection breaks my method calls, so I'm calling it what it is.
    private Stack<CustomerOrder> customerDeck; // I know it's a collection in the UML, but if I don't then I get errors on just using stack methods
    private List<CustomerOrder> inactiveCustomers;
    private Random random;
    private static final long serialVersionUID;

    public Customers(String deckFile, Random random, Collection<Layer> layers, int numPlayers)
    {
        initialiseCustomerDeck(deckFile, layers, numPlayers);   
        inactiveCustomers = new ArrayList<>(); 
        
        activeCustomers = new LinkedList<>();
        activeCustomers.add(customerDeck.pop());

        this.random = random;
    }
    public CustomerOrder addCustomerOrder()
    {

    }
    public boolean customerWillLeaveSoon()
    {
        return activeCustomers.size() >= 3;
    }
    public CustomerOrder drawCustomer()
    {

    }
    public Collection<CustomerOrder> getActiveCustomers()
    {
        return activeCustomers;
    }

    public Collection<CustomerOrder> getCustomerDeck()
    {
        return customerDeck;
    }

    public Collection<CustomerOrder> getFulfillable(List<Ingredient> hand)
    {
        Collection<CustomerOrder> fulfillable = new ArrayList<>();
        fulfillable.addAll(activeCustomers.stream().filter(c -> c.canFulfill(hand)).toList());
        return fulfillable;
    }

    public Collection<CustomerOrder> getInactiveCustomersWithStatus(CustomerOrderStatus status)
    {
        Collection<CustomerOrder> matching = new ArrayList<>();
        matching.addAll(activeCustomers.stream().filter(c -> c.getStatus() == status).toList());
        return matching;
    }

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
            List<CustomerOrder> separatedDeck = allCustomers.stream().filter(c -> c.getLevel() == cardLevel).toList();        
            for (int i = 0; i < integer; i++) {
                customerDeck.add(separatedDeck.get(0));
                separatedDeck.remove(0);
            }
            cardLevel++;
        }
        Collections.shuffle(customerDeck, random);
    }

    public boolean isEmpty()
    {
        return activeCustomers.isEmpty();
    }

    public CustomerOrder peek()
    {
        if(isEmpty())
            return null;
        return activeCustomers.getLast();
    }

    public void remove(CustomerOrder customer)
    {
        activeCustomers.remove(customer);
    }

    public int size()
    {
        return activeCustomers.size();
    }

    public CustomerOrder timePasses()
    {

    }

}