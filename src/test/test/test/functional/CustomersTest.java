package test.functional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import bakery.CustomerOrder;
import bakery.CustomerOrder.CustomerOrderStatus;
import bakery.Customers;
import bakery.Ingredient;
import bakery.Layer;
import util.CardUtils;

@Tag("functional")
@Tag("Customers")
public class CustomersTest {
    static CustomerOrder order1 = null, order2 = null, order3 = null, order4 = null, order5 = null;
    static List<Layer> layers;
    static List<Ingredient> pantry;

	@BeforeAll
	public static void setUp() throws FileNotFoundException, IOException {
        layers = CardUtils.readLayerFile("./io/layers.csv");

        pantry = new ArrayList<Ingredient>();
        pantry.add(new Ingredient("biscuit"));
        pantry.add(new Ingredient("flour"));
        pantry.add(new Ingredient("sugar"));
        pantry.add(new Ingredient("eggs"));
        pantry.add(new Ingredient("butter"));
        pantry.add(new Ingredient("milk"));
        pantry.add(new Ingredient("chocolate"));
        pantry.add(new Ingredient("walnuts"));
        pantry.add(new Ingredient("jam"));
        pantry.add(new Ingredient("double cream"));
        for (Layer layer: layers) {
            if (layer.toString().equals("jam")) pantry.add(layer);
            if (layer.toString().equals("sponge")) pantry.add(layer);
        }

        // Just creating a set of recipes. We could load them customers.csv but
        // this would add an unnecessary dependency

        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        for (Layer layer: layers) {
            if (layer.toString().equals("biscuit")) ingredients.add(layer);
        }
        ingredients.add(new Ingredient("butter"));
        ingredients.add(new Ingredient("chocolate"));
        order1 = new CustomerOrder("chocolate bombe", ingredients, new ArrayList<Ingredient>(), 1);

        ingredients = new ArrayList<Ingredient>();
        ingredients.add(new Ingredient("butter"));
        ingredients.add(new Ingredient("eggs"));
        ingredients.add(new Ingredient("flour"));
        order2 = new CustomerOrder("crumpets", ingredients, new ArrayList<Ingredient>(), 1);

        ingredients = new ArrayList<Ingredient>();
        ingredients.add(new Ingredient("chocolate"));
        for (Layer layer: layers) {
            if (layer.toString().equals("jam")) ingredients.add(layer);
            if (layer.toString().equals("sponge")) ingredients.add(layer);
        }
        order3 = new CustomerOrder("jaffa cakes", ingredients, new ArrayList<Ingredient>(), 2);

        ingredients = new ArrayList<Ingredient>();
        for (Layer layer: layers) {
            if (layer.toString().equals("biscuit")) ingredients.add(layer);
        }
        order4 = new CustomerOrder("viennese whirls", ingredients, new ArrayList<Ingredient>(), 3);

        ingredients = new ArrayList<Ingredient>();
        for (Layer layer: layers) {
            if (layer.toString().equals("icing")) ingredients.add(layer);
        }
        order5 = new CustomerOrder("fondant fancies", ingredients, new ArrayList<Ingredient>(), 3);
	}

    Customers getDeterministicCustomers() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = new Customers("./io/customers.csv", new Random(12345), layers, 4);

        @SuppressWarnings("unchecked")
        Collection<CustomerOrder> deck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");
        deck.clear();
        order1.setStatus(CustomerOrderStatus.WAITING);
        order2.setStatus(CustomerOrderStatus.WAITING);
        order3.setStatus(CustomerOrderStatus.WAITING);
        order4.setStatus(CustomerOrderStatus.WAITING);
        order5.setStatus(CustomerOrderStatus.WAITING);

        deck.add(order5);
        deck.add(order4);
        deck.add(order3);
        deck.add(order2);
        deck.add(order1);

        return customers;
    }

    private List<Integer> countLevels(Collection<CustomerOrder> deck) {
        List<Integer> counts = new ArrayList<Integer>();
        for (int i = 0; i <= 3; ++i) {
            counts.add(0);
        }

		for (CustomerOrder customer: deck) {
			if (customer != null) {
                counts.set(customer.getLevel(), counts.get(customer.getLevel()) + 1);
            }
		}
        return counts;
    }


    public Collection<CustomerOrder> getFulfilableWrapper(Customers customer, List<Ingredient> hand) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		Method mtd = FunctionalHelper.getMethod(customer, "getFulfillable", List.class);
		if (mtd == null) {
		    // there is no getFulfillable in Customers, let's try getFulfilable
		    mtd = FunctionalHelper.getMethod(customer, "getFulfilable", List.class);
        }

        if (mtd == null) return null;

        // This is a complicated way for saying: customer.getFulfillable(hand);
		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> fulfillable = (Collection<CustomerOrder>)mtd.invoke(customer, hand);
        return fulfillable;
    }

    // --- Constructor ---

    @Test
	public void testConstructor__TwoPlayersPiSeed() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = new Customers("./io/customers.csv", new Random(314159265), layers, 2);
        assertEquals(0, customers.size());

        @SuppressWarnings("unchecked")
		List<CustomerOrder> inactiveCustomers = (List<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");
		assertTrue(inactiveCustomers.isEmpty());

        // Check the CustomerOrders in the deck have the right distribution of levels

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        List<Integer> lvlCounts = countLevels(customerDeck);

		assertEquals(4, lvlCounts.get(1));
		assertEquals(2, lvlCounts.get(2));
		assertEquals(1, lvlCounts.get(3));

        // Check the CustomerDeck has been shuffled correctly

        CustomerOrder[] deckArray = customerDeck.toArray(new CustomerOrder[0]);
        assertEquals("shortbread biscuits", deckArray[0].toString());
        assertEquals("raspberry pavlova", deckArray[1].toString());
        assertEquals("millionaire's shortbread", deckArray[2].toString());
        assertEquals("lemon drizzle cake", deckArray[3].toString());
        assertEquals("jaffa cakes", deckArray[4].toString());
        assertEquals("cocoa crème doughnuts", deckArray[5].toString());
        assertEquals("chocolate bombe", deckArray[6].toString());
    }

    @Test
	public void testConstructor__TwoPlayersAvoSeed() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = new Customers("./io/customers.csv", new Random(602214076), layers, 2);
        assertEquals(0, customers.size());

        @SuppressWarnings("unchecked")
		List<CustomerOrder> inactiveCustomers = (List<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");
		assertTrue(inactiveCustomers.isEmpty());

        // Check the CustomerOrders in the deck have the right distribution of levels

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        List<Integer> lvlCounts = countLevels(customerDeck);

		assertEquals(4, lvlCounts.get(1));
		assertEquals(2, lvlCounts.get(2));
		assertEquals(1, lvlCounts.get(3));

        // Check the CustomerDeck has been shuffled correctly

        CustomerOrder[] deckArray = customerDeck.toArray(new CustomerOrder[0]);
        assertEquals("shortbread biscuits", deckArray[0].toString());
        assertEquals("crumpets", deckArray[1].toString());
        assertEquals("chocolate tea cake", deckArray[2].toString());
        assertEquals("chocolate bombe", deckArray[3].toString());
        assertEquals("millionaire's shortbread", deckArray[4].toString());
        assertEquals("danish pastries", deckArray[5].toString());
        assertEquals("swiss roll", deckArray[6].toString());
    }

    @Test
	public void testConstructor__ThreePlayersPiSeed() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = new Customers("./io/customers.csv", new Random(314159265), layers, 3);
        assertEquals(0, customers.size());

        @SuppressWarnings("unchecked")
		List<CustomerOrder> inactiveCustomers = (List<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");
		assertTrue(inactiveCustomers.isEmpty());

        // Check the CustomerOrders in the deck have the right distribution of levels

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        List<Integer> lvlCounts = countLevels(customerDeck);

		assertEquals(1, lvlCounts.get(1));
		assertEquals(2, lvlCounts.get(2));
		assertEquals(4, lvlCounts.get(3));

        // Check the CustomerDeck has been shuffled correctly

        CustomerOrder[] deckArray = customerDeck.toArray(new CustomerOrder[0]);
        assertEquals("jaffa cakes", deckArray[0].toString());
        assertEquals("lemon drizzle cake", deckArray[1].toString());
        assertEquals("millionaire's shortbread", deckArray[2].toString());
        assertEquals("showstopper cake (2 tiered)", deckArray[3].toString());
        assertEquals("frasier cake", deckArray[4].toString());
        assertEquals("almond & chocolate torte", deckArray[5].toString());
        assertEquals("cocoa crème doughnuts", deckArray[6].toString());
    }

    @Test
	public void testConstructor__ThreePlayersAvoSeed() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = new Customers("./io/customers.csv", new Random(602214076), layers, 3);
        assertEquals(0, customers.size());

        @SuppressWarnings("unchecked")
		List<CustomerOrder> inactiveCustomers = (List<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");
		assertTrue(inactiveCustomers.isEmpty());

        // Check the CustomerOrders in the deck have the right distribution of levels

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        List<Integer> lvlCounts = countLevels(customerDeck);

		assertEquals(1, lvlCounts.get(1));
		assertEquals(2, lvlCounts.get(2));
		assertEquals(4, lvlCounts.get(3));

        // Check the CustomerDeck has been shuffled correctly

        CustomerOrder[] deckArray = customerDeck.toArray(new CustomerOrder[0]);
        assertEquals("swiss roll", deckArray[0].toString());
        assertEquals("crumpets", deckArray[1].toString());
        assertEquals("empire biscuit", deckArray[2].toString());
        assertEquals("danish pastries", deckArray[3].toString());
        assertEquals("chocolate tea cake", deckArray[4].toString());
        assertEquals("frasier cake", deckArray[5].toString());
        assertEquals("cocoa crème doughnuts", deckArray[6].toString());
    }

    @Test
	public void testConstructor__FourPlayersPiSeed() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = new Customers("./io/customers.csv", new Random(314159265), layers, 4);
        assertEquals(0, customers.size());

        @SuppressWarnings("unchecked")
		List<CustomerOrder> inactiveCustomers = (List<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");
		assertTrue(inactiveCustomers.isEmpty());

        // Check the CustomerOrders in the deck have the right distribution of levels

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        List<Integer> lvlCounts = countLevels(customerDeck);

		assertEquals(1, lvlCounts.get(1));
		assertEquals(2, lvlCounts.get(2));
		assertEquals(4, lvlCounts.get(3));

        // Check the CustomerDeck has been shuffled correctly

        CustomerOrder[] deckArray = customerDeck.toArray(new CustomerOrder[0]);
        assertEquals("jaffa cakes", deckArray[0].toString());
        assertEquals("lemon drizzle cake", deckArray[1].toString());
        assertEquals("millionaire's shortbread", deckArray[2].toString());
        assertEquals("showstopper cake (2 tiered)", deckArray[3].toString());
        assertEquals("frasier cake", deckArray[4].toString());
        assertEquals("almond & chocolate torte", deckArray[5].toString());
        assertEquals("cocoa crème doughnuts", deckArray[6].toString());
    }

    @Test
	public void testConstructor__FivePlayersPiSeed() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = new Customers("./io/customers.csv", new Random(314159265), layers, 5);
        assertEquals(0, customers.size());

        @SuppressWarnings("unchecked")
		List<CustomerOrder> inactiveCustomers = (List<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");
		assertTrue(inactiveCustomers.isEmpty());

        // Check the CustomerOrders in the deck have the right distribution of levels

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        List<Integer> lvlCounts = countLevels(customerDeck);

		assertEquals(0, lvlCounts.get(1));
		assertEquals(1, lvlCounts.get(2));
		assertEquals(6, lvlCounts.get(3));

        // Check the CustomerDeck has been shuffled correctly

        CustomerOrder[] deckArray = customerDeck.toArray(new CustomerOrder[0]);
        assertEquals("cocoa crème doughnuts", deckArray[0].toString());
        assertEquals("frasier cake", deckArray[1].toString());
        assertEquals("jaffa cakes", deckArray[2].toString());
        assertEquals("old fashioned trifle", deckArray[3].toString());
        assertEquals("almond & chocolate torte", deckArray[4].toString());
        assertEquals("viennese whirls", deckArray[5].toString());
        assertEquals("showstopper cake (2 tiered)", deckArray[6].toString());
    }

    // It's hard to separate the functionality of the constructor and the initialiseCustomerDeck method,
    // but we can call initialiseCustomerDeck again after construction, using a different seed,
    // to see whether the customerDeck has the expected state

    @Test
	public void testInitialiseCustomerDeck__TwoPlayersPiSeed() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = new Customers("./io/customers.csv", new Random(-1), layers, 2);

        Method mtd = FunctionalHelper.getMethod(customers, "initialiseCustomerDeck", String.class, Collection.class, int.class);
        FunctionalHelper.setFieldValue(customers, "random", new Random(314159265));
        // This should produce the same state as the earlier testConstructor__TwoPlayersPiSeed
        try {
            mtd.invoke(customers, "./io/customers.csv", layers, 2);
        } catch (InvocationTargetException e) {
            fail();
        }

        // Check the CustomerOrders in the deck have the right distribution of levels

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        List<Integer> lvlCounts = countLevels(customerDeck);

		assertEquals(4, lvlCounts.get(1));
		assertEquals(2, lvlCounts.get(2));
		assertEquals(1, lvlCounts.get(3));

        // Check the CustomerDeck has been shuffled correctly

        CustomerOrder[] deckArray = customerDeck.toArray(new CustomerOrder[0]);
        assertEquals("shortbread biscuits", deckArray[0].toString());
        assertEquals("raspberry pavlova", deckArray[1].toString());
        assertEquals("millionaire's shortbread", deckArray[2].toString());
        assertEquals("lemon drizzle cake", deckArray[3].toString());
        assertEquals("jaffa cakes", deckArray[4].toString());
        assertEquals("cocoa crème doughnuts", deckArray[5].toString());
        assertEquals("chocolate bombe", deckArray[6].toString());
    }

    @Test
	public void testCustomerDeck__HasLIFOSemantics() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = new Customers("./io/customers.csv", new Random(314159265), layers, 2);
        
		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        // We check that customerDeck supports LIFO semantics.
		// Should have a push()
		assertDoesNotThrow(() -> {FunctionalHelper.getMethod(customerDeck.getClass(), "push");});
		// Should have a pop()
		assertDoesNotThrow(() -> {FunctionalHelper.getMethod(customerDeck.getClass(), "pop");});
		// Should have a peek()
		assertDoesNotThrow(() -> {FunctionalHelper.getMethod(customerDeck.getClass(), "peek");});
    }

    // --- Getters ---
    @Test
    public void testGetActiveCustomers__Init() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        @SuppressWarnings("unchecked")
        Collection<CustomerOrder> activeCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "activeCustomers");
        Collection<CustomerOrder> activeCustomers2 = customers.getActiveCustomers();
        if (activeCustomers.size() == activeCustomers2.size()) {
            assertTrue(activeCustomers.containsAll(activeCustomers2));
            assertTrue(activeCustomers2.containsAll(activeCustomers));
        } else {
            assertTrue(activeCustomers2.size() < activeCustomers.size());
            assertTrue(activeCustomers.containsAll(activeCustomers2));
        }
    }



    @Test
    public void testGetCustomerDeck__Empty() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        Collection<CustomerOrder> deck = customers.getCustomerDeck();
        CustomerOrder[] deckArray = deck.toArray(new CustomerOrder[1]);
        assertEquals(order1, deckArray[4]);
        assertEquals(order2, deckArray[3]);
        assertEquals(order3, deckArray[2]);
        assertEquals(order4, deckArray[1]);
        assertEquals(order5, deckArray[0]);
    }

    // Active Customers Manipulation Methods: addCustomerOrder(), remove(), timePasses()
    // The following tests check the correctness of the methods above *AND* the correctness
    // of the methods that retrieve the state of the active customers, i.e., customerWillLeaveSoon(), isEmpty(), peek(), size()

    // --- add(), remove(), and state checking ---
    @Test
    public void testInitialState() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        assertFalse(customers.customerWillLeaveSoon());
        assertTrue(customers.isEmpty());
        assertNull(customers.peek());
        assertEquals(0, customers.size());
    }

    @Test
    public void testAddCustomerOrder__FirstCard() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        // Initial State Sanity Checks: If one of these checks fails, there is a problem with various state accessing methods. Fix them first!
        assertEquals(0, customers.size());
        assertEquals(5, customerDeck.size());
        assertTrue(customerDeck.contains(order1));
        assertEquals(0, inactiveCustomers.size());

        assertFalse(customers.customerWillLeaveSoon());
        assertTrue(customers.isEmpty());
        assertNull(customers.peek());

        // ADD!
        assertNull(customers.addCustomerOrder());

        // State after addCustomerOrder() should be: order1 -> null -> null
        assertEquals(1, customers.size());
        assertEquals(4, customerDeck.size());
        assertFalse(customerDeck.contains(order1));
        assertEquals(0, inactiveCustomers.size());

        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertNull(customers.peek()); 
    }

    @Test
    public void testAddCustomerOrder__SecondCard() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        // Set the initial state: order1 -> null -> null
        customers.addCustomerOrder();

        // Initial State Sanity Checks: If one of these checks fails, there is a problem with various state accessing methods. Fix them first!
        // Alternatively, there is a problem with adding the first order. Check the previous test.
        assertEquals(1, customers.size());
        assertEquals(4, customerDeck.size());
        assertTrue(customerDeck.contains(order2));
        assertEquals(0, inactiveCustomers.size());

        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertNull(customers.peek()); 

        // ADD!
        assertNull(customers.addCustomerOrder());

        // State after addCustomerOrder() should be: order2 -> order1 -> null
        assertEquals(2, customers.size());
        assertEquals(3, customerDeck.size());
        assertFalse(customerDeck.contains(order2));
        assertEquals(0, inactiveCustomers.size());

        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertNull(customers.peek()); 
    }

    @Test
    public void testAddCustomerOrder__ThirdCard() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        // Set the initial state: order2 -> order1 -> null
        customers.addCustomerOrder();
        customers.addCustomerOrder();

        // Initial State Sanity Checks: If one of these checks fails, there is a problem with various state accessing methods. Fix them first!
        // Alternatively, there is a problem with adding the first two orders. Check the previous tests.
        assertEquals(2, customers.size());
        assertEquals(3, customerDeck.size());
        assertTrue(customerDeck.contains(order3));
        assertEquals(0, inactiveCustomers.size());

        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertNull(customers.peek()); 

        // ADD!
        assertNull(customers.addCustomerOrder());

        // State after addCustomerOrder() should be: order3 -> order2 -> order1
        assertEquals(3, customers.size());
        assertEquals(2, customerDeck.size());
        assertFalse(customerDeck.contains(order3));
        assertEquals(0, inactiveCustomers.size());

        assertTrue(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek()); 
    }

    @Test
    public void testAddCustomerOrder__FourthCard() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        // Set the initial state: order3 -> order2 -> order1
        customers.addCustomerOrder();
        customers.addCustomerOrder();
        customers.addCustomerOrder();

        // Initial State Sanity Checks: If one of these checks fails, there is a problem with various state accessing methods. Fix them first!
        // Alternatively, there is a problem with adding the first three orders. Check the previous tests.
        assertEquals(3, customers.size());
        assertEquals(2, customerDeck.size());
        assertTrue(customerDeck.contains(order4));
        assertEquals(0, inactiveCustomers.size());

        assertTrue(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek()); 

        // ADD!
        assertEquals(order1, customers.addCustomerOrder());

        // State after addCustomerOrder() should be: order4 -> order3 -> order2
        assertEquals(3, customers.size());
        assertEquals(1, customerDeck.size());
        assertFalse(customerDeck.contains(order4));
        assertEquals(1, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order1));

        assertTrue(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order2, customers.peek()); 
    }

    @Test
    public void testAddCustomerOrder__LastCard() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        // Set the initial state: order4 -> order3 -> order2
        customers.addCustomerOrder();
        customers.addCustomerOrder();
        customers.addCustomerOrder();
        customers.addCustomerOrder();

        // Initial State Sanity Checks: If one of these checks fails, there is a problem with various state accessing methods. Fix them first!
        // Alternatively, there is a problem with adding the first four orders. Check the previous tests.
        assertEquals(3, customers.size());
        assertEquals(1, customerDeck.size());
        assertTrue(customerDeck.contains(order5));
        assertEquals(1, inactiveCustomers.size());
        assertFalse(inactiveCustomers.contains(order2));

        assertTrue(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order2, customers.peek()); 

        // ADD!
        assertEquals(order2, customers.addCustomerOrder());

        // State after addCustomerOrder() should be: order5 -> order4 -> order3
        assertEquals(3, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(2, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order2));

        assertTrue(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order3, customers.peek()); 
    }

    @Test
    public void testAddCustomerOrder__Draining1() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        // Set the initial state: order5 -> order4 -> order3
        customers.addCustomerOrder();
        customers.addCustomerOrder();
        customers.addCustomerOrder();
        customers.addCustomerOrder();
        customers.addCustomerOrder();

        // Initial State Sanity Checks: If one of these checks fails, there is a problem with various state accessing methods. Fix them first!
        // Alternatively, there is a problem with adding the first five orders. Check the previous tests.
        assertEquals(3, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(2, inactiveCustomers.size());
        assertFalse(inactiveCustomers.contains(order3));

        assertTrue(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order3, customers.peek()); 

        // ADD!
        assertThrows(EmptyStackException.class, () -> {customers.addCustomerOrder();});

        // State after addCustomerOrder() should be: null -> order5 -> order4
        assertEquals(2, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(3, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order3));

        assertTrue(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order4, customers.peek()); 
    }

    @Test
    public void testAddCustomerOrder__Draining2() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        // Set the initial state: null -> order5 -> order4
        customers.addCustomerOrder();
        customers.addCustomerOrder();
        customers.addCustomerOrder();
        customers.addCustomerOrder();
        customers.addCustomerOrder();
        assertThrows(EmptyStackException.class, () -> {customers.addCustomerOrder();});

        // Initial State Sanity Checks: If one of these checks fails, there is a problem with various state accessing methods. Fix them first!
        // Alternatively, there is a problem with adding orders. Check the previous tests.
        assertEquals(2, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(3, inactiveCustomers.size());
        assertFalse(inactiveCustomers.contains(order4));

        assertTrue(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order4, customers.peek()); 

        // ADD!
        assertThrows(EmptyStackException.class, () -> {customers.addCustomerOrder();});

        // State after addCustomerOrder() should be: null -> null -> order5
        assertEquals(1, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(4, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order4));

        assertTrue(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order5, customers.peek()); 
    }

    @Test
    public void testAddCustomerOrder__Draining3() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        // Set the initial state: null -> null -> order5
        customers.addCustomerOrder();
        customers.addCustomerOrder();
        customers.addCustomerOrder();
        customers.addCustomerOrder();
        customers.addCustomerOrder();
        
        assertThrows(EmptyStackException.class, () -> {customers.addCustomerOrder();});
        assertThrows(EmptyStackException.class, () -> {customers.addCustomerOrder();});

        // Initial State Sanity Checks: If one of these checks fails, there is a problem with various state accessing methods. Fix them first!
        // Alternatively, there is a problem with adding orders. Check the previous tests.
        assertEquals(1, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(4, inactiveCustomers.size());
        assertFalse(inactiveCustomers.contains(order5));

        assertTrue(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order5, customers.peek()); 

        // ADD!
        assertThrows(EmptyStackException.class, () -> {customers.addCustomerOrder();});

        // State after addCustomerOrder() should be: null -> null -> null
        assertEquals(0, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(5, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order5));

        assertFalse(customers.customerWillLeaveSoon());
        assertTrue(customers.isEmpty());
        assertNull(customers.peek()); 
    }

    @Test
    public void testAddCustomerOrder__PDFExample1() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set

        // Active customers should be order3 -> order2 -> order1

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls or the various state functions. Fix them first
        assertEquals(3, customers.size());
        assertEquals(2, customerDeck.size());
        assertEquals(0, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order1));
        assertTrue(customers.getActiveCustomers().contains(order2));
        assertTrue(customers.getActiveCustomers().contains(order3));
        assertFalse(customers.getActiveCustomers().contains(order4));
        

        assertTrue(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());

        // ADD!
        assertEquals(order1, customers.addCustomerOrder());

        // Active customers should be order4 -> order3 -> order2 || order1 is now inactive
        assertEquals(3, customers.size());
        assertEquals(1, customerDeck.size());
        assertEquals(1, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(customers.getActiveCustomers().contains(order4));

        assertFalse(customers.isEmpty());
        assertEquals(order2, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.GIVEN_UP, order1.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order2.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order3.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order4.getStatus());
    }

    @Test
    public void testAddCustomerOrder__PDFExample2() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.remove(order1);

        // Active customers should be order3 -> order2 -> null || order1 is inactive

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls, the remove(), or the various state functions. Fix them first
        assertEquals(2, customers.size());
        assertEquals(2, customerDeck.size());
        assertEquals(1, inactiveCustomers.size());
        assertFalse(customers.getActiveCustomers().contains(order1));
        assertTrue(customers.getActiveCustomers().contains(order2));
        assertTrue(customers.getActiveCustomers().contains(order3));
        assertFalse(customers.getActiveCustomers().contains(order4));
        assertTrue(inactiveCustomers.contains(order1));

        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertNull(customers.peek());

        // ADD!
        assertNull(customers.addCustomerOrder());

        // Active customers should be order4 -> order3 -> order2
        assertEquals(3, customers.size());
        assertEquals(1, customerDeck.size());
        assertEquals(1, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order4));

        assertFalse(customers.isEmpty());
        assertEquals(order2, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order2.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order3.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order4.getStatus());
    }

    @Test
    public void testAddCustomerOrder__PDFExample3() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.remove(order2);

        // Active customers should be order3 -> null -> order1 || order2 is inactive

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls, the remove(), or the various state functions. Fix them first
        assertEquals(2, customers.size());
        assertEquals(2, customerDeck.size());
        assertEquals(1, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order1));
        assertFalse(customers.getActiveCustomers().contains(order2));
        assertTrue(customers.getActiveCustomers().contains(order3));
        assertFalse(customers.getActiveCustomers().contains(order4));
        assertTrue(inactiveCustomers.contains(order2));

        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());

        // ADD!
        assertNull(customers.addCustomerOrder());

        // Active customers should be order4 -> order3 -> order1
        assertEquals(3, customers.size());
        assertEquals(1, customerDeck.size());
        assertEquals(1, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order4));

        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order1.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order3.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order4.getStatus());
    }

    @Test
    public void testAddCustomerOrder__PDFExample4() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.remove(order3);

        // Active customers should be null -> order2 -> order1 || order3 is inactive

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls, the remove(), or the various state functions. Fix them first
        assertEquals(2, customers.size());
        assertEquals(2, customerDeck.size());
        assertEquals(1, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order1));
        assertTrue(customers.getActiveCustomers().contains(order2));
        assertFalse(customers.getActiveCustomers().contains(order3));
        assertFalse(customers.getActiveCustomers().contains(order4));
        assertTrue(inactiveCustomers.contains(order3));

        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());

        // ADD!
        assertNull(customers.addCustomerOrder());

        // Active customers should be order4 -> order2 -> order1
        assertEquals(3, customers.size());
        assertEquals(1, customerDeck.size());
        assertEquals(1, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order4));

        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order1.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order2.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order3.getStatus());
    }

    @Test
    public void testAddCustomerOrder__PDFExample5() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.remove(order1);
        customers.remove(order2);

        // Active customers should be order3 -> null -> null || order1 and order2 are inactive

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls, the remove(), or the various state functions. Fix them first
        assertEquals(1, customers.size());
        assertEquals(2, customerDeck.size());
        assertEquals(2, inactiveCustomers.size());
        assertFalse(customers.getActiveCustomers().contains(order1));
        assertFalse(customers.getActiveCustomers().contains(order2));
        assertTrue(customers.getActiveCustomers().contains(order3));
        assertFalse(customers.getActiveCustomers().contains(order4));
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));

        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertNull(customers.peek());

        // ADD!
        assertNull(customers.addCustomerOrder());

        // Active customers should be order4 -> order3 -> null
        assertEquals(2, customers.size());
        assertEquals(1, customerDeck.size());
        assertEquals(2, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order4));

        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order3.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order4.getStatus());
    }

    @Test
    public void testAddCustomerOrder__PDFExample6() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.remove(order1);
        customers.remove(order3);

        // Active customers should be null -> order2 -> null || order1 and order3 are inactive

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls, the remove(), or the various state functions. Fix them first
        assertEquals(1, customers.size());
        assertEquals(2, customerDeck.size());
        assertEquals(2, inactiveCustomers.size());
        assertFalse(customers.getActiveCustomers().contains(order1));
        assertTrue(customers.getActiveCustomers().contains(order2));
        assertFalse(customers.getActiveCustomers().contains(order3));
        assertFalse(customers.getActiveCustomers().contains(order4));
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order3));

        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertNull(customers.peek());

        // ADD!
        assertNull(customers.addCustomerOrder());

        // Active customers should be order4 -> order2 -> null
        assertEquals(2, customers.size());
        assertEquals(1, customerDeck.size());
        assertEquals(2, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order4));

        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order2.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order4.getStatus());
    }

    @Test
    public void testAddCustomerOrder__PDFExample7() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.remove(order2);
        customers.remove(order3);

        // Active customers should be null -> null -> order1 || order1 and order2 are inactive

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls, the remove(), or the various state functions. Fix them first
        assertEquals(1, customers.size());
        assertEquals(2, customerDeck.size());
        assertEquals(2, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order1));
        assertFalse(customers.getActiveCustomers().contains(order2));
        assertFalse(customers.getActiveCustomers().contains(order3));
        assertFalse(customers.getActiveCustomers().contains(order4));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order3));

        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());

        // ADD!
        assertNull(customers.addCustomerOrder());

        // Active customers should be order4 -> null -> order1
        assertEquals(2, customers.size());
        assertEquals(1, customerDeck.size());
        assertEquals(2, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order4));

        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order1.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order4.getStatus());
    }

    @Test
    public void testAddCustomerOrder__PDFExample8() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.remove(order1);
        customers.remove(order2);
        customers.remove(order3);

        // Active customers should be null -> null -> null || order1, order2 and order3 are inactive

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls, the remove(), or the various state functions. Fix them first
        assertEquals(0, customers.size());
        assertEquals(2, customerDeck.size());
        assertEquals(3, inactiveCustomers.size());
        assertFalse(customers.getActiveCustomers().contains(order1));
        assertFalse(customers.getActiveCustomers().contains(order2));
        assertFalse(customers.getActiveCustomers().contains(order3));
        assertFalse(customers.getActiveCustomers().contains(order4));
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order3));

        assertFalse(customers.customerWillLeaveSoon());
        assertTrue(customers.isEmpty());
        assertNull(customers.peek());

        // ADD!
        assertNull(customers.addCustomerOrder());

        // Active customers should be order4 -> null -> null
        assertEquals(1, customers.size());
        assertEquals(1, customerDeck.size());
        assertEquals(3, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order4));

        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order4.getStatus());
    }


    @Test
    public void testAddCustomerOrder__PDFExample9() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive

        // Active customers should be order5 -> order4 -> order3 || order1 and order2 are inactive || The deck is empty

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls or the various state functions. Fix them first
        assertEquals(3, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(2, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order3));
        assertTrue(customers.getActiveCustomers().contains(order4));
        assertTrue(customers.getActiveCustomers().contains(order5));
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));

        assertTrue(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order3, customers.peek());

        // ADD!
        assertThrows(EmptyStackException.class, () -> {customers.addCustomerOrder();});

        // Active customers should be null -> order5 -> order4 || order3 is now inactive
        assertEquals(2, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(3, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order3));

        assertFalse(customers.isEmpty());
        assertEquals(order4, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.GIVEN_UP, order3.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order4.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order5.getStatus());
    }

    @Test
    public void testAddCustomerOrder__PDFExample10() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive
        customers.remove(order3);

        // Active customers should be order5 -> order4 -> null || order1, order2, and order3 are inactive || The deck is empty

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls or the various state functions. Fix them first
        assertEquals(2, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(3, inactiveCustomers.size());
        assertFalse(customers.getActiveCustomers().contains(order3));
        assertTrue(customers.getActiveCustomers().contains(order4));
        assertTrue(customers.getActiveCustomers().contains(order5));
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order3));

        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertNull(customers.peek());

        // ADD!
        assertThrows(EmptyStackException.class, () -> {customers.addCustomerOrder();});

        // Active customers should be null -> order5 -> order4
        assertEquals(2, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(3, inactiveCustomers.size());

        assertFalse(customers.isEmpty());
        assertEquals(order4, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order4.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order5.getStatus());
    }

    @Test
    public void testAddCustomerOrder__PDFExample11() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive
        customers.remove(order4);

        // Active customers should be order5 -> null -> order3 || order1, order2, and order4 are inactive || The deck is empty

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls or the various state functions. Fix them first
        assertEquals(2, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(3, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order3));
        assertFalse(customers.getActiveCustomers().contains(order4));
        assertTrue(customers.getActiveCustomers().contains(order5));
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order4));

        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order3, customers.peek());

        // ADD!
        assertThrows(EmptyStackException.class, () -> {customers.addCustomerOrder();});

        // Active customers should be null -> order5 -> order3
        assertEquals(2, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(3, inactiveCustomers.size());

        assertFalse(customers.isEmpty());
        assertEquals(order3, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order3.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order5.getStatus());
    }

    @Test
    public void testAddCustomerOrder__PDFExample12() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive
        customers.remove(order5);

        // Active customers should be null -> order4 -> order3 || order1, order2, and order5 are inactive || The deck is empty

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls or the various state functions. Fix them first
        assertEquals(2, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(3, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order3));
        assertTrue(customers.getActiveCustomers().contains(order4));
        assertFalse(customers.getActiveCustomers().contains(order5));
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order5));

        assertTrue(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order3, customers.peek());

        // ADD!
        assertThrows(EmptyStackException.class, () -> {customers.addCustomerOrder();});

        // Active customers should be null -> null -> order4 || order3 becomes inactive
        assertEquals(1, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(4, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order3));

        assertFalse(customers.isEmpty());
        assertEquals(order4, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.GIVEN_UP, order3.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order4.getStatus());
    }

    @Test
    public void testAddCustomerOrder__PDFExample13() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive
        customers.remove(order3);
        customers.remove(order4);

        // Active customers should be order5 -> null -> null || order1, order2, order3, and order4 are inactive || The deck is empty

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls or the various state functions. Fix them first
        assertEquals(1, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(4, inactiveCustomers.size());
        assertFalse(customers.getActiveCustomers().contains(order3));
        assertFalse(customers.getActiveCustomers().contains(order4));
        assertTrue(customers.getActiveCustomers().contains(order5));
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order3));
        assertTrue(inactiveCustomers.contains(order4));

        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertNull(customers.peek());

        // ADD!
        assertThrows(EmptyStackException.class, () -> {customers.addCustomerOrder();});

        // Active customers should be null -> order5 -> null
        assertEquals(1, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(4, inactiveCustomers.size());

        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order5.getStatus());
    }

    @Test
    public void testAddCustomerOrder__PDFExample14() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive
        customers.remove(order3);
        customers.remove(order5);

        // Active customers should be null -> order4 -> null || order1, order2, order3, and order5 are inactive || The deck is empty

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls or the various state functions. Fix them first
        assertEquals(1, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(4, inactiveCustomers.size());
        assertFalse(customers.getActiveCustomers().contains(order3));
        assertTrue(customers.getActiveCustomers().contains(order4));
        assertFalse(customers.getActiveCustomers().contains(order5));
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order3));
        assertTrue(inactiveCustomers.contains(order5));

        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertNull(customers.peek());

        // ADD!
        assertThrows(EmptyStackException.class, () -> {customers.addCustomerOrder();});

        // Active customers should be null -> null -> order4
        assertEquals(1, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(4, inactiveCustomers.size());

        assertFalse(customers.isEmpty());
        assertEquals(order4, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order4.getStatus());
    }

    @Test
    public void testAddCustomerOrder__PDFExample15() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive
        customers.remove(order4);
        customers.remove(order5);

        // Active customers should be null -> null -> order3 || order1, order2, order4, and order5 are inactive || The deck is empty

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls or the various state functions. Fix them first
        assertEquals(1, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(4, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order3));
        assertFalse(customers.getActiveCustomers().contains(order4));
        assertFalse(customers.getActiveCustomers().contains(order5));
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order4));
        assertTrue(inactiveCustomers.contains(order5));

        assertTrue(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order3, customers.peek());

        // ADD!
        assertThrows(EmptyStackException.class, () -> {customers.addCustomerOrder();});

        // Active customers should be null -> null -> null || order3 became inactive
        assertEquals(0, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(5, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order3));

        assertTrue(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.GIVEN_UP, order3.getStatus());
    }

    @Test
    public void testAddCustomerOrder__PDFExample16() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive
        customers.remove(order3);
        customers.remove(order4);
        customers.remove(order5);

        // Active customers should be null -> null -> null || all orders are inactive || The deck is empty

        // Sanity checking: if there is an error before the next addCustomerOrder(), something is wrong with the earlier addCustomer() calls or the various state functions. Fix them first
        assertEquals(0, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(5, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order3));
        assertTrue(inactiveCustomers.contains(order4));
        assertTrue(inactiveCustomers.contains(order5));

        assertFalse(customers.customerWillLeaveSoon());
        assertTrue(customers.isEmpty());
        assertNull(customers.peek());

        // ADD!
        assertThrows(EmptyStackException.class, () -> {customers.addCustomerOrder();});

        // Active customers should be null -> null -> null
        assertEquals(0, customers.size());
        assertEquals(0, customerDeck.size());
        assertEquals(5, inactiveCustomers.size());

        assertTrue(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
    }

    @Test
    public void testRemoveCustomerOrder() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder();
        customers.addCustomerOrder();
        customers.addCustomerOrder();

        customers.remove(order3);
        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());
        assertEquals(2, customers.size());
        assertEquals(1, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order3));

        customers.remove(order1);
        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertEquals(1, customers.size());
        assertEquals(2, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order1));

        assertNull(customers.addCustomerOrder());
        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertEquals(2, customers.size());
        assertFalse(customerDeck.contains(order4));
        assertEquals(1, customerDeck.size());

        customers.remove(order2);
        assertFalse(customers.customerWillLeaveSoon());
        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertEquals(1, customers.size());
        assertEquals(3, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order2));

        customers.remove(order4);
        assertFalse(customers.customerWillLeaveSoon());
        assertTrue(customers.isEmpty());
        assertNull(customers.peek());
        assertEquals(0, customers.size());
        assertEquals(4, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order4));
    }

    @Test
    public void testTimePasses__PDFExample1() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set

        // Active customers should be order3 -> order2 -> order1

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(3, customers.size());
        assertEquals(0, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order1));
        assertTrue(customers.getActiveCustomers().contains(order2));
        assertTrue(customers.getActiveCustomers().contains(order3));

        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order1.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order2.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order3.getStatus());

        // Time passes!
        assertEquals(order1, customers.timePasses());

        // Active customers should be empty -> order3 -> order2 || order1 is now inactive
        assertEquals(2, customers.size());
        assertEquals(1, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order1));

        assertFalse(customers.isEmpty());
        assertEquals(order2, customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.GIVEN_UP, order1.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order2.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order3.getStatus());
    }

    @Test
    public void testTimePasses__PDFExample2() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.remove(order1);

        // Active customers should be order3 -> order2 -> null || order1 is now inactive

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(2, customers.size());
        assertEquals(1, inactiveCustomers.size());
        assertFalse(customers.getActiveCustomers().contains(order1));
        assertTrue(customers.getActiveCustomers().contains(order2));
        assertTrue(customers.getActiveCustomers().contains(order3));

        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());

        // Time passes!
        assertNull(customers.timePasses());

        // Active customers should be null -> order3 -> order2 
        assertEquals(2, customers.size());
        assertEquals(1, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order1));

        assertFalse(customers.isEmpty());
        assertEquals(order2, customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order2.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order3.getStatus());
    }

    @Test
    public void testTimePasses__PDFExample3() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.remove(order2);

        // Active customers should be order3 -> null -> order1 || order2 is now inactive

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(2, customers.size());
        assertEquals(1, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order1));
        assertFalse(customers.getActiveCustomers().contains(order2));
        assertTrue(customers.getActiveCustomers().contains(order3));

        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());
        assertFalse(customers.customerWillLeaveSoon());

        // Time passes!
        assertNull(customers.timePasses());

        // Active customers should be null -> order3 -> order1 
        assertEquals(2, customers.size());
        assertEquals(1, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order2));

        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order1.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order3.getStatus());
    }

    @Test
    public void testTimePasses__PDFExample4() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.remove(order3);

        // Active customers should be null -> order2 -> order1 || order3 is now inactive

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(2, customers.size());
        assertEquals(1, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order1));
        assertTrue(customers.getActiveCustomers().contains(order2));
        assertFalse(customers.getActiveCustomers().contains(order3));

        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());
        assertFalse(customers.customerWillLeaveSoon());

        // Time passes!
        assertNull(customers.timePasses());

        // Active customers should be null -> order2 -> order1
        assertEquals(2, customers.size());
        assertEquals(1, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order3));

        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order1.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order2.getStatus());
    }

    @Test
    public void testTimePasses__PDFExample5() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.remove(order1);
        customers.remove(order2);

        // Active customers should be order3 -> null -> null || order1 and order2 are now inactive

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(1, customers.size());
        assertEquals(2, inactiveCustomers.size());
        assertFalse(customers.getActiveCustomers().contains(order1));
        assertFalse(customers.getActiveCustomers().contains(order2));
        assertTrue(customers.getActiveCustomers().contains(order3));

        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());

        // Time passes!
        assertNull(customers.timePasses());

        // Active customers should be null -> order3 -> null 
        assertEquals(1, customers.size());
        assertEquals(2, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));

        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order3.getStatus());
    }

    @Test
    public void testTimePasses__PDFExample6() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.remove(order1);
        customers.remove(order3);

        // Active customers should be null -> order2 -> null || order1 and order3 are now inactive

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(1, customers.size());
        assertEquals(2, inactiveCustomers.size());
        assertFalse(customers.getActiveCustomers().contains(order1));
        assertTrue(customers.getActiveCustomers().contains(order2));
        assertFalse(customers.getActiveCustomers().contains(order3));

        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());

        // Time passes!
        assertNull(customers.timePasses());

        // Active customers should be null -> order2 -> null 
        assertEquals(1, customers.size());
        assertEquals(2, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order3));

        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order2.getStatus());
    }

    @Test
    public void testTimePasses__PDFExample7() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.remove(order2);
        customers.remove(order3);

        // Active customers should be null -> null -> order1 || order2 and order3 are now inactive

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(1, customers.size());
        assertEquals(2, inactiveCustomers.size());
        assertTrue(customers.getActiveCustomers().contains(order1));
        assertFalse(customers.getActiveCustomers().contains(order2));
        assertFalse(customers.getActiveCustomers().contains(order3));

        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());
        assertFalse(customers.customerWillLeaveSoon());

        // Time passes!
        assertNull(customers.timePasses());

        // Active customers should be null -> null -> order1 
        assertEquals(1, customers.size());
        assertEquals(2, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order3));

        assertFalse(customers.isEmpty());
        assertEquals(order1, customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order1.getStatus());
    }

    @Test
    public void testTimePasses__PDFExample8() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.remove(order1);
        customers.remove(order2);
        customers.remove(order3);

        // Active customers should be null -> null -> null || order1, order2, and order3 are now inactive

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(0, customers.size());
        assertEquals(3, inactiveCustomers.size());

        assertTrue(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());

        // Time passes!
        assertNull(customers.timePasses());

        // Active customers should be null -> null -> null 
        assertEquals(0, customers.size());
        assertEquals(3, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order3));

        assertTrue(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
    }

    @Test
    public void testTimePasses__PDFExample9() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive

        // Active customers should be order5 -> order4 -> order3 || order1 and order2 are inactive || the customerDeck is empty

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(3, customers.size());
        assertEquals(2, inactiveCustomers.size());
        assertTrue(customers.getCustomerDeck().isEmpty());
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(customers.getActiveCustomers().contains(order3));
        assertTrue(customers.getActiveCustomers().contains(order4));
        assertTrue(customers.getActiveCustomers().contains(order5));

        assertFalse(customers.isEmpty());
        assertEquals(order3, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order3.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order4.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order5.getStatus());

        // Time passes!
        assertEquals(order3, customers.timePasses());

        // Active customers should be null -> order5 -> order4 || order3 is now also inactive
        assertEquals(2, customers.size());
        assertEquals(3, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order3));

        assertFalse(customers.isEmpty());
        assertEquals(order4, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.GIVEN_UP, order3.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order4.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order5.getStatus());
    }

    @Test
    public void testTimePasses__PDFExample10() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive
        customers.remove(order3);

        // Active customers should be order5 -> order4 -> null || order1, order2, and order3 are inactive || the customerDeck is empty

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(2, customers.size());
        assertEquals(3, inactiveCustomers.size());
        assertTrue(customers.getCustomerDeck().isEmpty());
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order3));
        assertTrue(customers.getActiveCustomers().contains(order4));
        assertTrue(customers.getActiveCustomers().contains(order5));

        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());

        // Time passes!
        assertNull(customers.timePasses());

        // Active customers should be null -> order5 -> order4
        assertEquals(2, customers.size());
        assertEquals(3, inactiveCustomers.size());

        assertFalse(customers.isEmpty());
        assertEquals(order4, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order4.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order5.getStatus());
    }

    @Test
    public void testTimePasses__PDFExample11() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive
        customers.remove(order4);

        // Active customers should be order5 -> null -> order3 || order1, order2, and order4 are inactive || the customerDeck is empty

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(2, customers.size());
        assertEquals(3, inactiveCustomers.size());
        assertTrue(customers.getCustomerDeck().isEmpty());
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order4));
        assertTrue(customers.getActiveCustomers().contains(order3));
        assertTrue(customers.getActiveCustomers().contains(order5));

        assertFalse(customers.isEmpty());
        assertEquals(order3, customers.peek());
        assertFalse(customers.customerWillLeaveSoon());

        // Time passes!
        assertNull(customers.timePasses());

        // Active customers should be null -> order5 -> order3
        assertEquals(2, customers.size());
        assertEquals(3, inactiveCustomers.size());

        assertFalse(customers.isEmpty());
        assertEquals(order3, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order3.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order5.getStatus());
    }

    @Test
    public void testTimePasses__PDFExample12() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive
        customers.remove(order5);

        // Active customers should be null -> order4 -> order3 || order1, order2, and order5 are inactive || the customerDeck is empty

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(2, customers.size());
        assertEquals(3, inactiveCustomers.size());
        assertTrue(customers.getCustomerDeck().isEmpty());
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order5));
        assertTrue(customers.getActiveCustomers().contains(order3));
        assertTrue(customers.getActiveCustomers().contains(order4));

        assertFalse(customers.isEmpty());
        assertEquals(order3, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());

        // Time passes!
        assertEquals(order3, customers.timePasses());

        // Active customers should be null -> null -> order4 || order3 is now also inactive
        assertEquals(1, customers.size());
        assertEquals(4, inactiveCustomers.size());
        assertTrue(inactiveCustomers.contains(order3));

        assertFalse(customers.isEmpty());
        assertEquals(order4, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.GIVEN_UP, order3.getStatus());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order4.getStatus());
    }

    @Test
    public void testTimePasses__PDFExample13() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive
        customers.remove(order3);
        customers.remove(order4);

        // Active customers should be order5 -> null -> null || order1, order2, order3, and order4 are inactive || the customerDeck is empty

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(1, customers.size());
        assertEquals(4, inactiveCustomers.size());
        assertTrue(customers.getCustomerDeck().isEmpty());
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order3));
        assertTrue(inactiveCustomers.contains(order4));
        assertTrue(customers.getActiveCustomers().contains(order5));

        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());

        // Time passes!
        assertNull(customers.timePasses());

        // Active customers should be null -> order5 -> null
        assertEquals(1, customers.size());
        assertEquals(4, inactiveCustomers.size());

        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, order5.getStatus());
    }

    @Test
    public void testTimePasses__PDFExample14() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive
        customers.remove(order3);
        customers.remove(order5);

        // Active customers should be null -> order4 -> null || order1, order2, order3, and order5 are inactive || the customerDeck is empty

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(1, customers.size());
        assertEquals(4, inactiveCustomers.size());
        assertTrue(customers.getCustomerDeck().isEmpty());
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order3));
        assertTrue(inactiveCustomers.contains(order5));
        assertTrue(customers.getActiveCustomers().contains(order4));

        assertFalse(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());

        // Time passes!
        assertNull(customers.timePasses());

        // Active customers should be null -> null -> order4
        assertEquals(1, customers.size());
        assertEquals(4, inactiveCustomers.size());

        assertFalse(customers.isEmpty());
        assertEquals(order4, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, order4.getStatus());
    }

    @Test
    public void testTimePasses__PDFExample15() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive
        customers.remove(order4);
        customers.remove(order5);

        // Active customers should be null -> null -> order3 || order1, order2, order4, and order5 are inactive || the customerDeck is empty

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(1, customers.size());
        assertEquals(4, inactiveCustomers.size());
        assertTrue(customers.getCustomerDeck().isEmpty());
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order4));
        assertTrue(inactiveCustomers.contains(order5));
        assertTrue(customers.getActiveCustomers().contains(order3));

        assertFalse(customers.isEmpty());
        assertEquals(order3, customers.peek());
        assertTrue(customers.customerWillLeaveSoon());

        // Time passes!
        assertEquals(order3, customers.timePasses());

        // Active customers should be null -> null -> null || order3 is now inactive too
        assertEquals(0, customers.size());
        assertEquals(5, inactiveCustomers.size());

        assertTrue(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
        assertEquals(CustomerOrder.CustomerOrderStatus.GIVEN_UP, order3.getStatus());
    }

    @Test
    public void testTimePasses__PDFExample16() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();
        // customerDeck from top to bottom: order1 -> order2 -> order3 -> order4 -> order5

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

        customers.addCustomerOrder(); // order1 in active set
        customers.addCustomerOrder(); // order2 in active set
        customers.addCustomerOrder(); // order3 in active set
        customers.addCustomerOrder(); // order4 in active set, order1 becomes inactive
        customers.addCustomerOrder(); // order5 in active set, order2 becomes inactive
        customers.remove(order3);
        customers.remove(order4);
        customers.remove(order5);

        // Active customers should be null -> null -> null || all orders are inactive || the customerDeck is empty || the game should end soon

        // Sanity checking: if there is an error before timePasses(), something is wrong with addCustomer(), remove(), or the various state functions. Fix them first
        assertEquals(0, customers.size());
        assertEquals(5, inactiveCustomers.size());
        assertTrue(customers.getCustomerDeck().isEmpty());
        assertTrue(inactiveCustomers.contains(order1));
        assertTrue(inactiveCustomers.contains(order2));
        assertTrue(inactiveCustomers.contains(order3));
        assertTrue(inactiveCustomers.contains(order4));
        assertTrue(inactiveCustomers.contains(order5));

        assertTrue(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());

        // Time passes!
        assertNull(customers.timePasses());

        // Active customers should still be null -> null -> null
        assertEquals(0, customers.size());
        assertEquals(5, inactiveCustomers.size());

        assertTrue(customers.isEmpty());
        assertNull(customers.peek());
        assertFalse(customers.customerWillLeaveSoon());
    }
    
    // --- drawCustomer() ---

    @Test
    public void testDrawCustomer__Init() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

        assertEquals(5, customerDeck.size());

        assertEquals(order1, customers.drawCustomer());
        assertEquals(4, customerDeck.size());
        assertFalse(customerDeck.contains(order1));

        assertEquals(order2, customers.drawCustomer());
        assertEquals(3, customerDeck.size());
        assertFalse(customerDeck.contains(order2));

        assertEquals(order3, customers.drawCustomer());
        assertEquals(2, customerDeck.size());
        assertFalse(customerDeck.contains(order3));

        assertEquals(order4, customers.drawCustomer());
        assertEquals(1, customerDeck.size());
        assertFalse(customerDeck.contains(order4));

        assertEquals(order5, customers.drawCustomer());
        assertEquals(0, customerDeck.size());
        assertFalse(customerDeck.contains(order5));
    }

	@Test
	public void testDrawCustomer__AddAll() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
        Customers customers = new Customers("./io/customers.csv", new Random(12345), layers, 4);

        @SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

		CustomerOrder[] deckArray = customerDeck.toArray(new CustomerOrder[0]);
		for (int i = deckArray.length - 1; i >= 0; i--) {
			assertEquals(deckArray[i], customers.drawCustomer());
			assertEquals(i, customerDeck.size());
		}
	}

    // --- getFulfillable() ---

    

    @Test
    public void testGetFulfillable() throws ClassNotFoundException, FileNotFoundException, InvocationTargetException, IOException, NoSuchFieldException, IllegalAccessException {
        Customers customers = getDeterministicCustomers();

		Collection<CustomerOrder> fulfillable = getFulfilableWrapper(customers, pantry);
        assertNotNull(fulfillable);
        assertTrue(fulfillable.isEmpty());

        customers.addCustomerOrder();

        fulfillable = getFulfilableWrapper(customers, pantry);
        assertNotNull(fulfillable);
        assertTrue(fulfillable.isEmpty());

        customers.addCustomerOrder();

        fulfillable = getFulfilableWrapper(customers, pantry);
        assertNotNull(fulfillable);
        assertEquals(1, fulfillable.size());
        assertEquals(order2, fulfillable.toArray()[0]);

        customers.addCustomerOrder();
        
        fulfillable = getFulfilableWrapper(customers, pantry);
        assertNotNull(fulfillable);
        assertEquals(2, fulfillable.size());
        assertTrue(fulfillable.contains(order2));
        assertTrue(fulfillable.contains(order3));

        customers.remove(order2);

        fulfillable = getFulfilableWrapper(customers, pantry);
        assertNotNull(fulfillable);
        assertEquals(1, fulfillable.size());
        assertEquals(order3, fulfillable.toArray()[0]);

        customers.remove(order3);

        fulfillable = getFulfilableWrapper(customers, pantry);
        assertNotNull(fulfillable);
        assertTrue(fulfillable.isEmpty());
    }

    // --- getInactiveCustomersWithStatus() ---
    @Test
	public void testGetInactiveCustomersWithStatus__Empty() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		Customers customers = getDeterministicCustomers();

		@SuppressWarnings("unchecked")
		List<CustomerOrder> inactiveCustomers = (List<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");
		inactiveCustomers.clear();

		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.FULFILLED).isEmpty());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GARNISHED).isEmpty());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GIVEN_UP).isEmpty());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.IMPATIENT).isEmpty());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.WAITING).isEmpty());
	}


	@Test
	public void testGetInactiveCustomersWithStatus__FulfilledOnly() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		Customers customers = getDeterministicCustomers();

		@SuppressWarnings("unchecked")
		List<CustomerOrder> inactiveCustomers = (List<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");
		inactiveCustomers.clear();

        order1.setStatus(CustomerOrderStatus.FULFILLED);
        inactiveCustomers.add(order1);

        order2.setStatus(CustomerOrderStatus.FULFILLED);
        inactiveCustomers.add(order2);

        order3.setStatus(CustomerOrderStatus.FULFILLED);
        inactiveCustomers.add(order3);

		assertEquals(3, customers.getInactiveCustomersWithStatus(CustomerOrderStatus.FULFILLED).size());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GARNISHED).isEmpty());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GIVEN_UP).isEmpty());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.IMPATIENT).isEmpty());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.WAITING).isEmpty());
	}

	@Test
	public void testGetInactiveCustomersWithStatus__GarnishedOnly() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		Customers customers = getDeterministicCustomers();

		@SuppressWarnings("unchecked")
		List<CustomerOrder> inactiveCustomers = (List<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");
		inactiveCustomers.clear();

        order1.setStatus(CustomerOrderStatus.GARNISHED);
        inactiveCustomers.add(order1);

        order2.setStatus(CustomerOrderStatus.GARNISHED);
        inactiveCustomers.add(order2);

		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.FULFILLED).isEmpty());
		assertEquals(2, customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GARNISHED).size());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GIVEN_UP).isEmpty());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.IMPATIENT).isEmpty());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.WAITING).isEmpty());
	}

	@Test
	public void testGetInactiveCustomersWithStatus__GivenUpOnly() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		Customers customers = getDeterministicCustomers();

		@SuppressWarnings("unchecked")
		List<CustomerOrder> inactiveCustomers = (List<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");
		inactiveCustomers.clear();

        order1.setStatus(CustomerOrderStatus.GIVEN_UP);
        inactiveCustomers.add(order1);

        order2.setStatus(CustomerOrderStatus.GIVEN_UP);
        inactiveCustomers.add(order2);

        order3.setStatus(CustomerOrderStatus.GIVEN_UP);
        inactiveCustomers.add(order3);

		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.FULFILLED).isEmpty());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GARNISHED).isEmpty());
		assertEquals(3, customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GIVEN_UP).size());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.IMPATIENT).isEmpty());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.WAITING).isEmpty());
	}

	@Test
	public void testGetInactiveCustomersWithStatus__Mixed1() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		Customers customers = getDeterministicCustomers();

		@SuppressWarnings("unchecked")
		List<CustomerOrder> inactiveCustomers = (List<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");
		inactiveCustomers.clear();

        order1.setStatus(CustomerOrderStatus.GIVEN_UP);
        inactiveCustomers.add(order1);

        order2.setStatus(CustomerOrderStatus.FULFILLED);
        inactiveCustomers.add(order2);

        order3.setStatus(CustomerOrderStatus.FULFILLED);
        inactiveCustomers.add(order3);

        order4.setStatus(CustomerOrderStatus.GARNISHED);
        inactiveCustomers.add(order4);

        order5.setStatus(CustomerOrderStatus.IMPATIENT);
        inactiveCustomers.add(order5);


		assertEquals(2, customers.getInactiveCustomersWithStatus(CustomerOrderStatus.FULFILLED).size());
		assertEquals(1, customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GARNISHED).size());
		assertEquals(1, customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GIVEN_UP).size());
		assertEquals(1, customers.getInactiveCustomersWithStatus(CustomerOrderStatus.IMPATIENT).size());
		assertTrue(customers.getInactiveCustomersWithStatus(CustomerOrderStatus.WAITING).isEmpty());
	}

    @Test
	public void testGetInactiveCustomersWithStatus__Mixed2() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		Customers customers = getDeterministicCustomers();

		@SuppressWarnings("unchecked")
		List<CustomerOrder> inactiveCustomers = (List<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");
		inactiveCustomers.clear();

        order1.setStatus(CustomerOrderStatus.GIVEN_UP);
        inactiveCustomers.add(order1);

        order2.setStatus(CustomerOrderStatus.WAITING);
        inactiveCustomers.add(order2);

        order3.setStatus(CustomerOrderStatus.FULFILLED);
        inactiveCustomers.add(order3);

        order4.setStatus(CustomerOrderStatus.GARNISHED);
        inactiveCustomers.add(order4);

        order5.setStatus(CustomerOrderStatus.IMPATIENT);
        inactiveCustomers.add(order5);


		assertEquals(1, customers.getInactiveCustomersWithStatus(CustomerOrderStatus.FULFILLED).size());
		assertEquals(1, customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GARNISHED).size());
		assertEquals(1, customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GIVEN_UP).size());
		assertEquals(1, customers.getInactiveCustomersWithStatus(CustomerOrderStatus.IMPATIENT).size());
		assertEquals(1, customers.getInactiveCustomersWithStatus(CustomerOrderStatus.WAITING).size());
	}


    @Test
	public void testCustomerDeckIsShuffledCorrectly__TwoPlayers__EulerSeed() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
        Customers customers = new Customers("./io/customers.csv", new Random(271828), layers, 2);
        ArrayList<CustomerOrder> customerDeck = new ArrayList<CustomerOrder>(customers.getCustomerDeck());

        assertEquals("millionaire's shortbread", customerDeck.get(0).toString());
        assertEquals("viennese whirls", customerDeck.get(1).toString());
        assertEquals("chocolate bombe", customerDeck.get(2).toString());
        assertEquals("custard tart", customerDeck.get(3).toString());
        assertEquals("chocolate chip scones", customerDeck.get(4).toString());
        assertEquals("pistachio ganache macarons", customerDeck.get(5).toString());
        assertEquals("mille feuille", customerDeck.get(6).toString());
    }

    @Test
	public void testCustomerDeckIsShuffledCorrectly__FivePlayers__EulerSeed() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
        Customers customers = new Customers("./io/customers.csv", new Random(271828), layers, 5);
        ArrayList<CustomerOrder> customerDeck = new ArrayList<CustomerOrder>(customers.getCustomerDeck());

        assertEquals("viennese whirls", customerDeck.get(0).toString());
        assertEquals("empire biscuit", customerDeck.get(1).toString());
        assertEquals("danish pastries", customerDeck.get(2).toString());
        assertEquals("cocoa crème doughnuts", customerDeck.get(3).toString());
        assertEquals("mille feuille", customerDeck.get(4).toString());
        assertEquals("almond & chocolate torte", customerDeck.get(5).toString());
        assertEquals("old fashioned trifle", customerDeck.get(6).toString());
    }

    @Test
	public void testCustomerDeckIsShuffledCorrectly__FivePlayers__AvoSeed() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
        Customers customers = new Customers("./io/customers.csv", new Random(602214076), layers, 5);
        ArrayList<CustomerOrder> customerDeck = new ArrayList<CustomerOrder>(customers.getCustomerDeck());

        assertEquals("danish pastries", customerDeck.get(0).toString());
        assertEquals("swiss roll", customerDeck.get(1).toString());
        assertEquals("showstopper cake (2 tiered)", customerDeck.get(2).toString());
        assertEquals("empire biscuit", customerDeck.get(3).toString());
        assertEquals("cocoa crème doughnuts", customerDeck.get(4).toString());
        assertEquals("almond & chocolate torte", customerDeck.get(5).toString());
        assertEquals("frasier cake", customerDeck.get(6).toString());
    }

    @Test
	public void testCustomerDeckIsShuffledCorrectly__FivePlayers__TauSeed() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
        Customers customers = new Customers("./io/customers.csv", new Random(628318530), layers, 5);
        ArrayList<CustomerOrder> customerDeck = new ArrayList<CustomerOrder>(customers.getCustomerDeck());

        assertEquals("frasier cake", customerDeck.get(0).toString());
        assertEquals("showstopper cake (2 tiered)", customerDeck.get(1).toString());
        assertEquals("danish pastries", customerDeck.get(2).toString());
        assertEquals("viennese whirls", customerDeck.get(3).toString());
        assertEquals("empire biscuit", customerDeck.get(4).toString());
        assertEquals("old fashioned trifle", customerDeck.get(5).toString());
        assertEquals("lemon drizzle cake", customerDeck.get(6).toString());
    }
}
