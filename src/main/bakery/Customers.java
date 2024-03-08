package bakery;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Customers{
    private Collection<CustomerOrder> activeCustomers;
    private Collection<CustomerOrder> customerDeck;
    private List<CustomerOrder> inactiveCustomers;
    private Random random;
    private static final long serialVersionUID;

    public Customers(String deckFile, Random random, Collection<Layer> layers, int numPlayers)
    {
        //do something with the file path
        //do something with the layers
        //do something with the numPlayers
        this.random = random;
    }
    public CustomerOrder addCustomerOrder()
    {

    }
    public boolean customerWillLeaveSoon()
    {

    }
    public CustomerOrder drawCustomer()
    {

    }
    public Collection<CustomerOrder> getActiveCustomers()
    {

    }

    public Collection<CustomerOrder> getCustomerDeck()
    {

    }

    public Collection<CustomerOrder> getFulfillable(List<Ingredient> hand)
    {

    }

    public Collection<CustomerOrder> getInactiveCustomersWithStatus(CustomerOrderStatus status)
    {

    }

    private void initialiseCustomerDeck(String deckFile, Collection<Layer> layers, int numPlayers)
    {

    }

    public boolean isEmpty()
    {

    }

    public CustomerOrder peek()
    {

    }

    public void remove(CustomerOrder customer)
    {

    }

    public int size()
    {

    }

    public CustomerOrder timePasses()
    {

    }

}