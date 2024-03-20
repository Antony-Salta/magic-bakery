package test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import bakery.CustomerOrder;
import bakery.Customers;
import bakery.Ingredient;
import bakery.Layer;
import bakery.MagicBakery;
import bakery.Player;
import bakery.CustomerOrder.CustomerOrderStatus;

@Tag("functional")
@Tag("MagicBakery")
public class MagicBakeryTest {

	static List<String> playerNames;

	private MagicBakery bakeryFactory() throws IOException, FileNotFoundException {
		return new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
	}

	private Ingredient stringToIngredient(Collection<Layer> layers, String name) {
		for (Layer layer: layers) {
			if (layer.toString().equals(name)) {
				return layer;
			}
		}
		return new Ingredient(name);
	}

	private void setupActiveCustomers(MagicBakery bakery, List<CustomerOrder> customerList) throws NoSuchFieldException, IllegalAccessException {
		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> deck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");
		deck.clear();
		deck.addAll(customerList);

		for (int i = 0; i < customerList.size(); i++) {
			customers.addCustomerOrder();
		}
	}

	private List<Ingredient> setupCurrentHand(MagicBakery bakery, String[] ingredients) throws NoSuchFieldException, IllegalAccessException {
		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(bakery.getCurrentPlayer(), "hand");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		hand.clear();
		for (String ing: ingredients) {
			Ingredient ingredient = stringToIngredient(layers, ing);
			if (ingredient instanceof Layer) {
				layers.remove((Layer)ingredient);
			}
			hand.add(ingredient);
		}
		return hand;
	}

	private CustomerOrder createCustomerOrder(Collection<Layer> layers, String name, String[] recipe, String[] garnish) {
		List<Ingredient> recipeList = new ArrayList<>();
		for (String ingStr: recipe) {
			recipeList.add(stringToIngredient(layers, ingStr));
		}

		List<Ingredient> garnishList = new ArrayList<>();
		for (String ingStr: garnish) {
			garnishList.add(stringToIngredient(layers, ingStr));
		}
		return new CustomerOrder(name, recipeList, garnishList, 1);
	}

	public Collection<CustomerOrder> getFulfilableCustomersWrapper(MagicBakery bakery) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		Method mtd = FunctionalHelper.getMethod(bakery, "getFulfillableCustomers");
        if (mtd == null) {
		    // there is no getFulfillableCustomers in MagicBakery, let's try getFulfilable
		    mtd = FunctionalHelper.getMethod(bakery, "getFulfilableCustomers");
        }

        if (mtd == null) return null;

        // This is a complicated way for saying: bakery.getFulfillableCustomers();
        @SuppressWarnings("unchecked")
        Collection<CustomerOrder> fulfillable = (Collection<CustomerOrder>)mtd.invoke(bakery);
        return fulfillable;
    }

	@BeforeAll
	public static void setUp() throws IOException, FileNotFoundException {
		playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		playerNames.add("PlayerC");
	}

	@Test
	public void testConstructor() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException {
		MagicBakery bakery = bakeryFactory();

		Collection<Player> players = bakery.getPlayers();
		assertEquals(0, players.size());

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDeck = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDeck");
		assertEquals(63, pantryDeck.size());

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");
		assertTrue(pantryDiscard.isEmpty());

		Collection<Ingredient> pantry = bakery.getPantry();
		assertTrue(pantry.isEmpty());

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");
		assertEquals(24, layers.size());
	}

	@Test
	public void testConstructor__SeedIsUsedToSetInitialRandomState() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException {
		MagicBakery bakery1 = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		MagicBakery bakery2 = new MagicBakery(58214, "./io/ingredients.csv", "./io/layers.csv");
		MagicBakery bakery3 = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");

		Random random1 = (Random)FunctionalHelper.getFieldValue(bakery1, "random");
		Random random2 = (Random)FunctionalHelper.getFieldValue(bakery2, "random");
		Random random3 = (Random)FunctionalHelper.getFieldValue(bakery3, "random");

		assertNotEquals(random1.nextInt(), random2.nextInt());
		random3.nextInt();
		assertEquals(random1.nextInt(), random3.nextInt());
	}

	@Test
	public void testBakeLayer__Simple() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		int layersOrig = layers.size();

		String[] ingredients = {"flour", "eggs", "sugar", "butter", "oil"};
		List<Ingredient> hand = setupCurrentHand(bakery, ingredients);

		Layer layer = (Layer)stringToIngredient(layers, "sponge");
		bakery.bakeLayer(layer);
		assertEquals(layersOrig - 1, layers.size());

		assertEquals(2, hand.size());
		assertFalse(hand.contains(new Ingredient("flour")));
		assertFalse(hand.contains(new Ingredient("eggs")));
		assertFalse(hand.contains(new Ingredient("sugar")));
		assertFalse(hand.contains(new Ingredient("butter")));
		assertTrue(hand.contains(new Ingredient("oil")));
		assertTrue(hand.contains(layer));

		assertEquals(4, pantryDiscard.size());
		assertTrue(pantryDiscard.contains(new Ingredient("flour")));
		assertTrue(pantryDiscard.contains(new Ingredient("eggs")));
		assertTrue(pantryDiscard.contains(new Ingredient("butter")));
		assertTrue(pantryDiscard.contains(new Ingredient("sugar")));

		int actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();
		assertEquals(1, actionsTaken);
	}

	@Test
	public void testBakeLayer__UsingDuck() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		int layersOrig = layers.size();

		String[] ingredients = {"flour", "eggs", "sugar", "oil"};
		List<Ingredient> hand = setupCurrentHand(bakery, ingredients);
		hand.add(Ingredient.HELPFUL_DUCK);

		Layer layer = (Layer)stringToIngredient(layers, "sponge");
		bakery.bakeLayer(layer);
		assertEquals(layersOrig - 1, layers.size());

		assertEquals(2, hand.size());
		assertFalse(hand.contains(new Ingredient("flour")));
		assertFalse(hand.contains(new Ingredient("sugar")));
		assertFalse(hand.contains(new Ingredient("eggs")));
		assertFalse(hand.contains(new Ingredient("butter")));
		assertFalse(hand.contains(Ingredient.HELPFUL_DUCK));
		assertTrue(hand.contains(new Ingredient("oil")));
		assertTrue(hand.contains(layer));

		assertEquals(4, pantryDiscard.size());
		assertTrue(pantryDiscard.contains(new Ingredient("flour")));
		assertTrue(pantryDiscard.contains(Ingredient.HELPFUL_DUCK));
		assertTrue(pantryDiscard.contains(new Ingredient("sugar")));
		assertTrue(pantryDiscard.contains(new Ingredient("eggs")));

		int actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();
		assertEquals(1, actionsTaken);
	}

	@Test
	public void testBakeLayer__BakeTwo() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		String[] ingredients = {"flour", "eggs", "sugar", "butter", "oil", "butter", "eggs", "sugar"};
		List<Ingredient> hand = setupCurrentHand(bakery, ingredients);

		Layer layer1 = (Layer)stringToIngredient(layers, "sponge");
		Layer layer2 = (Layer)stringToIngredient(layers, "crème pât");

		bakery.bakeLayer(layer1);

		assertEquals(5, hand.size());
		assertFalse(hand.contains(new Ingredient("flour")));
		assertTrue(hand.contains(new Ingredient("butter")));
		assertTrue(hand.contains(new Ingredient("oil")));
		assertTrue(hand.contains(new Ingredient("eggs")));
		assertTrue(hand.contains(new Ingredient("sugar")));
		assertTrue(hand.contains(layer1));

		assertEquals(4, pantryDiscard.size());
		assertTrue(pantryDiscard.contains(new Ingredient("flour")));
		assertTrue(pantryDiscard.contains(new Ingredient("butter")));
		assertTrue(pantryDiscard.contains(new Ingredient("sugar")));
		assertTrue(pantryDiscard.contains(new Ingredient("eggs")));

		int actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();
		assertEquals(1, actionsTaken);

		bakery.bakeLayer(layer2);

		assertEquals(3, hand.size());
		assertFalse(hand.contains(new Ingredient("flour")));
		assertFalse(hand.contains(new Ingredient("butter")));
		assertFalse(hand.contains(new Ingredient("eggs")));
		assertFalse(hand.contains(new Ingredient("sugar")));
		assertTrue(hand.contains(new Ingredient("oil")));
		assertTrue(hand.contains(layer1));
		assertTrue(hand.contains(layer2));

		assertEquals(7, pantryDiscard.size());
		assertTrue(pantryDiscard.contains(new Ingredient("flour")));
		assertTrue(pantryDiscard.contains(new Ingredient("butter")));
		assertTrue(pantryDiscard.contains(new Ingredient("sugar")));
		assertTrue(pantryDiscard.contains(new Ingredient("eggs")));

		actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();
		assertEquals(2, actionsTaken);
	}

	@Test
	public void testPantryDeck__HasLIFOSemantics() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDeck = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDeck");

		// We check that pantryDeck is used with LIFO semantics in the test below. Here we only test that pantryDeck itself supports LIFO semantics
		// Should have a push()
		assertDoesNotThrow(() -> {FunctionalHelper.getMethod(pantryDeck.getClass(), "push");});
		// Should have a pop()
		assertDoesNotThrow(() -> {FunctionalHelper.getMethod(pantryDeck.getClass(), "pop");});
		// Should have a peek()
		assertDoesNotThrow(() -> {FunctionalHelper.getMethod(pantryDeck.getClass(), "peek");});
	}

	@Test
	public void testPantryDiscard__HasLIFOSemantics() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		// We check that pantryDiscard is used with LIFO semantics in the tests below. Here we only test that pantryDiscard itself supports LIFO semantics
		// Should have a push()
		assertDoesNotThrow(() -> {FunctionalHelper.getMethod(pantryDiscard.getClass(), "push");});
		// Should have a pop()
		assertDoesNotThrow(() -> {FunctionalHelper.getMethod(pantryDiscard.getClass(), "pop");});
		// Should have a peek()
		assertDoesNotThrow(() -> {FunctionalHelper.getMethod(pantryDiscard.getClass(), "peek");});
	}


	@Test
	public void testDrawFromPantryDeck__Normal() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDeck = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDeck");

		Ingredient[] deckArray = pantryDeck.toArray(new Ingredient[0]);
		
		// pantryDeck has LIFO semantics: latest items added should be the first returned by drawFromPantryDeck
		Method mtd = FunctionalHelper.getMethod(MagicBakery.class, "drawFromPantryDeck");
		for (int i = deckArray.length - 1; i >= 0; i--) {
			// Compare position i with the return value of drawFromPantryDeck()
			assertEquals(deckArray[i], mtd.invoke(bakery));
			// pantryDeck should be one item smaller
			assertEquals(i, pantryDeck.size());
		}
	}

	HashMap<Ingredient, Integer> countIngredients(Collection<Ingredient> deck) {
		HashMap<Ingredient, Integer> counts = new HashMap<>();
		for (Ingredient ing: deck) {
			counts.merge(ing, 1, Integer::sum);
		}
		return counts;
	}

	@Test
	public void testDrawFromPantryDeck__Refill() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDeck = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDeck");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		HashMap<Ingredient, Integer> countsOriginal = countIngredients(pantryDeck);
		
		// pantryDeck has LIFO semantics: latest items added should be the first returned by drawFromPantryDeck
		Method mtd = FunctionalHelper.getMethod(MagicBakery.class, "drawFromPantryDeck");

		Ingredient[] deckArray = pantryDeck.toArray(new Ingredient[0]);
		for (int i = deckArray.length - 1; i >= 0; i--) {
			assertEquals(deckArray[i], mtd.invoke(bakery));
			assertEquals(i, pantryDeck.size());
			pantryDiscard.add(deckArray[i]);
		}

		// This should trigger a refill
		Ingredient ingredient = (Ingredient) mtd.invoke(bakery);

		// Fetch these objects again just in case the developer created new ones instead of manipulating the old ones

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard2 = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		// PantryDiscard should be empty again
		assertTrue(pantryDiscard2.isEmpty());

		@SuppressWarnings("unchecked")
		ArrayList<Ingredient> pantryDeck2 = new ArrayList<Ingredient>((Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDeck"));

		// While the pantryDeck should be full minus one
		assertEquals(deckArray.length - 1, pantryDeck2.size());

		HashMap<Ingredient, Integer> countsRefill = countIngredients(pantryDeck2);
		countsRefill.merge(ingredient, 1, Integer::sum);

		// Make sure the new pantryDeck contains all the cards from the original pantryDeck
		assertEquals(countsOriginal, countsRefill);

		// The new pantryDeck should be shuffled in the way specified in the specs
		assertEquals("butter", pantryDeck2.get(0).toString());
		assertEquals("flour", pantryDeck2.get(4).toString());
		assertEquals("butter", pantryDeck2.get(8).toString());
		assertEquals("sugar", pantryDeck2.get(12).toString());
		assertEquals("sugar", pantryDeck2.get(17).toString());
		assertEquals("sugar", pantryDeck2.get(33).toString());
		assertEquals("eggs", pantryDeck2.get(37).toString());
	}

	@Test
	public void testDrawFromPantry__EmptyInitialState() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Collection<Ingredient> pantry = bakery.getPantry();

		Player player = bakery.getCurrentPlayer();
		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");

		int actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();

		pantry.clear();
		pantry.add(new Ingredient("flour"));
		pantry.add(new Ingredient("butter"));
		pantry.add(new Ingredient("sugar"));
		pantry.add(new Ingredient("eggs"));
		pantry.add(new Ingredient("oil"));

		hand.clear();

		assertTrue(hand.isEmpty());
		assertEquals(5, pantry.size());
		assertEquals(0, actionsTaken);

		bakery.drawFromPantry(new Ingredient("flour"));
		assertEquals(1, hand.size());
		assertTrue(hand.contains(new Ingredient("flour")));
		assertEquals(5, pantry.size());
		actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();
		assertEquals(1, actionsTaken);

		bakery.drawFromPantry(new Ingredient("oil"));
		assertEquals(2, hand.size());
		assertTrue(hand.contains(new Ingredient("oil")));
		assertEquals(5, pantry.size());
		actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();
		assertEquals(2, actionsTaken);

		bakery.drawFromPantry(new Ingredient("eggs"));
		assertEquals(3, hand.size());
		assertTrue(hand.contains(new Ingredient("eggs")));
		assertEquals(5, pantry.size());
		actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();
		assertEquals(3, actionsTaken);
	}

	@Test
	public void testDrawFromPantry__NormalInitialState() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Player player = bakery.getCurrentPlayer();
		int handSize = player.getHand().size();
		int pantrySize = bakery.getPantry().size();
		int actionsCount = 0;

		assertEquals(actionsCount, bakery.getActionsPermitted() - bakery.getActionsRemaining());

		for (int repeat = 0; repeat < 3; repeat++) {
			HashMap<Ingredient, Integer> countsBefore = countIngredients(bakery.getPantry());

			Ingredient selected = bakery.getPantry().toArray(new Ingredient[0])[3];
			bakery.drawFromPantry(selected);
			actionsCount++;
			handSize++;
			
			assertEquals(handSize, player.getHand().size());
			assertTrue(player.getHand().contains(selected));
			assertEquals(pantrySize, bakery.getPantry().size());
			assertEquals(actionsCount, bakery.getActionsPermitted() - bakery.getActionsRemaining());

			HashMap<Ingredient, Integer> countsAfter = countIngredients(bakery.getPantry());

			// There was one selected ingredient, it should be none left (or perhaps there's one if replaced by another ing)
			if (countsBefore.get(selected) == 1) {
				if (countsAfter.containsKey(selected)) {
					assertEquals(1, countsAfter.get(selected));
				}
			} else {
				assertTrue(countsAfter.get(selected) == countsBefore.get(selected) - 1 || countsAfter.get(selected) == countsBefore.get(selected));
			}
		}
	}

	@Test
	public void testDrawFromPantry__NormalInitialState2() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Player player = bakery.getCurrentPlayer();
		int handSize = player.getHand().size();
		int pantrySize = bakery.getPantry().size();
		int actionsCount = 0;

		assertEquals(actionsCount, bakery.getActionsPermitted() - bakery.getActionsRemaining());

		for (int repeat = 0; repeat < 3; repeat++) {
			HashMap<Ingredient, Integer> countsBefore = countIngredients(bakery.getPantry());

			Ingredient selected = bakery.getPantry().toArray(new Ingredient[0])[3];
			bakery.drawFromPantry(selected.toString());
			actionsCount++;
			handSize++;
			
			assertEquals(handSize, player.getHand().size());
			assertTrue(player.getHand().contains(selected));
			assertEquals(pantrySize, bakery.getPantry().size());
			assertEquals(actionsCount, bakery.getActionsPermitted() - bakery.getActionsRemaining());

			HashMap<Ingredient, Integer> countsAfter = countIngredients(bakery.getPantry());

			// There was one selected ingredient, it should be none left (or perhaps there's one if replaced by another ing)
			if (countsBefore.get(selected) == 1) {
				if (countsAfter.containsKey(selected)) {
					assertEquals(1, countsAfter.get(selected));
				}
			} else {
				assertTrue(countsAfter.get(selected) == countsBefore.get(selected) - 1 || countsAfter.get(selected) == countsBefore.get(selected));
			}
		}
	}

	@Test
	public void testEndTurn__FirstRound() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		
		Player[] players = bakery.getPlayers().toArray(new Player[0]);

		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

		Ingredient[] pantry = bakery.getPantry().toArray(new Ingredient[0]);

		int deckSizeOriginal = customerDeck.size();
		int customersOriginal = customers.size();

		bakery.drawFromPantry(pantry[0]);
		assertEquals(1, bakery.getActionsPermitted() - bakery.getActionsRemaining());
		assertEquals(players[0], bakery.getCurrentPlayer());

		for (int i = 1; i < players.length; i++) {
			bakery.endTurn();
			assertEquals(0, bakery.getActionsPermitted() - bakery.getActionsRemaining());
			assertEquals(players[i], bakery.getCurrentPlayer());
			assertEquals(deckSizeOriginal, customerDeck.size());
			assertEquals(customersOriginal, customers.size());
		}
	}

	@Test
	public void testEndTurn__EndRound() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Player[] players = bakery.getPlayers().toArray(new Player[0]);

		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

		int deckSizeOriginal = customerDeck.size();
		int customersOriginal = customers.size();

		for (int i = 1; i < players.length; i++) {
			bakery.endTurn();
		}

		// State just before the end of the round
		assertEquals(0, bakery.getActionsPermitted() - bakery.getActionsRemaining());
		assertEquals(players[players.length - 1], bakery.getCurrentPlayer());
		assertEquals(deckSizeOriginal, customerDeck.size());
		assertEquals(customersOriginal, customers.size());

		// Round ends
		bakery.endTurn();

		assertEquals(0, bakery.getActionsPermitted() - bakery.getActionsRemaining());
		assertEquals(players[0], bakery.getCurrentPlayer());
		assertEquals(deckSizeOriginal - 1, customerDeck.size());
		assertEquals(customersOriginal + 1, customers.size());
	}

	@Test
	public void testEndTurn__EndGame() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Player[] players = bakery.getPlayers().toArray(new Player[0]);

		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

		int deckSizeOriginal = customerDeck.size();
		int customersOriginal = customers.size();

		// Force the game to use all remaining customer cards
		int turns = deckSizeOriginal * players.length;

		// and then progress to the last turn of the next round 
		turns += players.length - 1;

		for (int i = 0; i < turns; i++) {
			bakery.endTurn();
		}

		assertEquals(0, bakery.getActionsPermitted() - bakery.getActionsRemaining());
		assertEquals(players[players.length - 1], bakery.getCurrentPlayer());
		assertEquals(0, customerDeck.size());

		assertEquals(3, customers.size());

		// Three more rounds to push out all remaining customers
		for (int i = 0; i < players.length; i++) {
			bakery.endTurn();
		}
		assertEquals(2, customers.size());

		for (int i = 0; i < players.length; i++) {
			bakery.endTurn();
		}
		assertEquals(1, customers.size());

		for (int i = 0; i < players.length; i++) {
			bakery.endTurn();
		}
		assertEquals(0, customers.size());

		assertEquals(deckSizeOriginal + customersOriginal, inactiveCustomers.size());
	}

	@Test
	public void testFulfillOrder__NoLayersNoGarnish() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");
		
		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		int layersOrig = layers.size();

		String[] recipe = {"flour", "butter", "sugar"};
		String[] garnish = {"chocolate", "walnuts"};
		CustomerOrder customer = createCustomerOrder(layers, "some recipe", recipe, garnish);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(customer);

		setupActiveCustomers(bakery, customCustomers);

		String[] ingredients = {"flour", "sugar", "butter",  "chocolate", "walnuts"};
		List<Ingredient> hand = setupCurrentHand(bakery, ingredients);

		List<Ingredient> drawn = bakery.fulfillOrder(customer, false);
		assertTrue(drawn.isEmpty());
		assertEquals(2, hand.size());
		assertEquals(2, customers.size());
		assertEquals(1, inactiveCustomers.size());
		assertTrue(inactiveCustomers.contains(customer));
		assertEquals(layersOrig, layers.size());
		assertEquals(3, pantryDiscard.size());
		assertTrue(pantryDiscard.contains(new Ingredient("flour")));
		assertTrue(pantryDiscard.contains(new Ingredient("butter")));
		assertTrue(pantryDiscard.contains(new Ingredient("sugar")));
		int actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();
		assertEquals(1, actionsTaken);
	}

	@Test
	public void testFulfillOrder__NoLayersNoGarnish2() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");
		
		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		int layersOrig = layers.size();

		String[] recipe = {"flour", "butter", "sugar"};
		String[] garnish = {"chocolate", "strawberries"};
		CustomerOrder customer = createCustomerOrder(layers, "some recipe", recipe, garnish);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(customer);

		setupActiveCustomers(bakery, customCustomers);

		String[] ingredients = {"flour", "sugar", "butter",  "chocolate", "walnuts"};
		List<Ingredient> hand = setupCurrentHand(bakery, ingredients);

		List<Ingredient> drawn = bakery.fulfillOrder(customer, false);
		assertTrue(drawn.isEmpty());
		assertEquals(2, hand.size());
		assertEquals(2, customers.size());
		assertEquals(1, inactiveCustomers.size());
		assertTrue(inactiveCustomers.contains(customer));
		assertEquals(layersOrig, layers.size());
		assertEquals(3, pantryDiscard.size());
		assertTrue(pantryDiscard.contains(new Ingredient("flour")));
		assertTrue(pantryDiscard.contains(new Ingredient("butter")));
		assertTrue(pantryDiscard.contains(new Ingredient("sugar")));
		int actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();
		assertEquals(1, actionsTaken);	
	}

	@Test
	public void testFulfillOrder__NoLayersWithGarnish() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");
		
		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		int layersOrig = layers.size();

		String[] recipe = {"flour", "butter", "sugar"};
		String[] garnish = {"chocolate", "walnuts"};
		CustomerOrder customer = createCustomerOrder(layers, "some recipe", recipe, garnish);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(customer);

		setupActiveCustomers(bakery, customCustomers);

		String[] ingredients = {"flour", "sugar", "butter",  "chocolate", "walnuts"};
		List<Ingredient> hand = setupCurrentHand(bakery, ingredients);

		List<Ingredient> drawn = bakery.fulfillOrder(customer, true);
		assertEquals(2, drawn.size());
		assertEquals(2, hand.size());
		assertEquals(hand, drawn);
		assertEquals(2, customers.size());
		assertEquals(1, inactiveCustomers.size());
		assertTrue(inactiveCustomers.contains(customer));
		assertEquals(layersOrig, layers.size());
		assertEquals(5, pantryDiscard.size());
		assertTrue(pantryDiscard.contains(new Ingredient("flour")));
		assertTrue(pantryDiscard.contains(new Ingredient("butter")));
		assertTrue(pantryDiscard.contains(new Ingredient("sugar")));
		assertTrue(pantryDiscard.contains(new Ingredient("chocolate")));
		assertTrue(pantryDiscard.contains(new Ingredient("walnuts")));
		int actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();
		assertEquals(1, actionsTaken);
	}

	@Test
	public void testFulfillOrder__WithLayersWithGarnish() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");
		
		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		int layersOrig = layers.size();

		String[] recipe = {"flour", "butter", "sugar", "biscuit"};
		String[] garnish = {"chocolate", "walnuts"};
		CustomerOrder customer = createCustomerOrder(layers, "some recipe", recipe, garnish);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(customer);

		setupActiveCustomers(bakery, customCustomers);

		String[] ingredients = {"flour", "sugar", "butter",  "chocolate", "walnuts", "biscuit"};
		List<Ingredient> hand = setupCurrentHand(bakery, ingredients);
		
		List<Ingredient> drawn = bakery.fulfillOrder(customer, true);
		assertEquals(2, drawn.size());
		assertEquals(2, hand.size());
		assertEquals(hand, drawn);
		assertEquals(2, customers.size());
		assertEquals(1, inactiveCustomers.size());
		assertTrue(inactiveCustomers.contains(customer));
		assertEquals(layersOrig, layers.size());
		assertEquals(5, pantryDiscard.size());
		assertTrue(pantryDiscard.contains(new Ingredient("flour")));
		assertTrue(pantryDiscard.contains(new Ingredient("butter")));
		assertTrue(pantryDiscard.contains(new Ingredient("sugar")));
		assertTrue(pantryDiscard.contains(new Ingredient("chocolate")));
		assertTrue(pantryDiscard.contains(new Ingredient("walnuts")));
		int actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();
		assertEquals(1, actionsTaken);
	}

	@Test
	public void testFulfillOrder__WithLayersWithGarnishBakeLayer() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");
		
		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		int layersOrig = layers.size();

		String[] recipe = {"flour", "butter", "sugar", "biscuit"};
		String[] garnish = {"chocolate", "walnuts"};
		CustomerOrder customer = createCustomerOrder(layers, "some recipe", recipe, garnish);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(customer);

		setupActiveCustomers(bakery, customCustomers);

		String[] ingredients = {"flour", "flour", "butter", "eggs", "sugar", "sugar",  "chocolate", "walnuts"};
		List<Ingredient> hand = setupCurrentHand(bakery, ingredients);

		Layer layer = (Layer)stringToIngredient(layers, "biscuit");
		bakery.bakeLayer(layer);
		assertEquals(layersOrig - 1, layers.size());

		List<Ingredient> drawn = bakery.fulfillOrder(customer, true);
		assertEquals(2, drawn.size());
		assertEquals(2, hand.size());
		assertEquals(hand, drawn);
		assertEquals(2, customers.size());
		assertEquals(1, inactiveCustomers.size());
		assertTrue(inactiveCustomers.contains(customer));
		assertEquals(layersOrig, layers.size());
		assertEquals(8, pantryDiscard.size());
		assertTrue(pantryDiscard.contains(new Ingredient("flour")));
		assertTrue(pantryDiscard.contains(new Ingredient("butter")));
		assertTrue(pantryDiscard.contains(new Ingredient("sugar")));
		assertTrue(pantryDiscard.contains(new Ingredient("chocolate")));
		assertTrue(pantryDiscard.contains(new Ingredient("walnuts")));
		int actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();
		assertEquals(2, actionsTaken);
	}

	@Test
	public void testFulfillOrder__OldestCustomerNotImpatient() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");
		
		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		int layersOrig = layers.size();

		String[] recipe1 = {"flour", "butter", "sugar"};
		String[] garnish1 = {"chocolate", "walnuts"};
		CustomerOrder customer1 = createCustomerOrder(layers, "some recipe", recipe1, garnish1);
		CustomerOrder customer2 = createCustomerOrder(layers, "some other recipe", recipe1, garnish1);
		CustomerOrder customer3 = createCustomerOrder(layers, "yet another recipe", recipe1, garnish1);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(customer1);
		customCustomers.add(customer2);
		customCustomers.add(customer3);

		setupActiveCustomers(bakery, customCustomers);

		// BUGFIX: add one more order in the deck to make sure we trigger the right behaviour
		CustomerOrder customer4 = createCustomerOrder(layers, "yet one more recipe", recipe1, garnish1);

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> deck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");
		deck.add(customer4);

		// Oldest Customer should be impatient since the list of active customers is full
		assertEquals(CustomerOrderStatus.IMPATIENT, customers.peek().getStatus());

		String[] ingredients = {"flour", "sugar", "butter",  "chocolate", "walnuts"};
		List<Ingredient> hand = setupCurrentHand(bakery, ingredients);

		int inactiveBefore = inactiveCustomers.size();

		List<Ingredient> drawn = bakery.fulfillOrder(customer1, false);
		assertTrue(drawn.isEmpty());
		assertEquals(2, hand.size());
		assertEquals(2, customers.size());
		assertEquals(inactiveBefore + 1, inactiveCustomers.size());
		assertTrue(inactiveCustomers.contains(customer1));
		assertEquals(layersOrig, layers.size());
		assertEquals(3, pantryDiscard.size());
		assertTrue(pantryDiscard.contains(new Ingredient("flour")));
		assertTrue(pantryDiscard.contains(new Ingredient("butter")));
		assertTrue(pantryDiscard.contains(new Ingredient("sugar")));
		int actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();
		assertEquals(1, actionsTaken);

		// Oldest Customer should not be impatient now
		assertEquals(CustomerOrderStatus.WAITING, customers.peek().getStatus());

		// ------------------------------------------------------------
		// Well, this should be an extra test, but let's keep it simple
		// Same case, but the deck is empty, so the front customer
		// should not go to waiting
		// ------------------------------------------------------------

		customer1 = createCustomerOrder(layers, "some recipe", recipe1, garnish1);
		customer2 = createCustomerOrder(layers, "some other recipe", recipe1, garnish1);
		customer3 = createCustomerOrder(layers, "yet another recipe", recipe1, garnish1);

		customCustomers = new ArrayList<>();
		customCustomers.add(customer1);
		customCustomers.add(customer2);
		customCustomers.add(customer3);

		setupActiveCustomers(bakery, customCustomers);

		// Oldest Customer should be impatient since the list of active customers is full
		assertEquals(CustomerOrderStatus.IMPATIENT, customers.peek().getStatus());

		hand = setupCurrentHand(bakery, ingredients);

		inactiveBefore = inactiveCustomers.size();

		drawn = bakery.fulfillOrder(customer1, false);
		assertTrue(drawn.isEmpty());
		assertEquals(2, hand.size());
		assertEquals(2, customers.size());
		assertEquals(inactiveBefore + 1, inactiveCustomers.size());
		assertTrue(inactiveCustomers.contains(customer1));
		assertEquals(layersOrig, layers.size());
		actionsTaken = bakery.getActionsPermitted() - bakery.getActionsRemaining();
		assertEquals(2, actionsTaken);

		// Oldest Customer should not be impatient now
		assertEquals(CustomerOrderStatus.IMPATIENT, customers.peek().getStatus());


	}

	@Test
	public void testGetActionsPermitted__TwoPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("A");
		playerNames.add("B");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");
		assertEquals(3, bakery.getActionsPermitted());
	}

	@Test
	public void testGetActionsPermitted__ThreePlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("A");
		playerNames.add("B");
		playerNames.add("C");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");
		assertEquals(3, bakery.getActionsPermitted());
	}

	@Test
	public void testGetActionsPermitted__FourPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("A");
		playerNames.add("B");
		playerNames.add("C");
		playerNames.add("D");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");
		assertEquals(2, bakery.getActionsPermitted());
	}

	@Test
	public void testGetActionsPermitted__FivePlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("A");
		playerNames.add("B");
		playerNames.add("C");
		playerNames.add("D");
		playerNames.add("E");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");
		assertEquals(2, bakery.getActionsPermitted());
	}

	@Test
	public void testGetActionsRemaining__TwoPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("A");
		playerNames.add("B");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");
		assertEquals(3, bakery.getActionsRemaining());

		Ingredient[] pantry = bakery.getPantry().toArray(new Ingredient[0]);

		bakery.drawFromPantry(pantry[0]);
		assertEquals(2, bakery.getActionsRemaining());

		bakery.drawFromPantry(pantry[1]);
		assertEquals(1, bakery.getActionsRemaining());

		bakery.drawFromPantry(pantry[2]);
		assertEquals(0, bakery.getActionsRemaining());
	}

	@Test
	public void testGetActionsRemaining__FourPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("A");
		playerNames.add("B");
		playerNames.add("C");
		playerNames.add("D");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");
		assertEquals(2, bakery.getActionsRemaining());

		Ingredient[] pantry = bakery.getPantry().toArray(new Ingredient[0]);

		bakery.drawFromPantry(pantry[0]);
		assertEquals(1, bakery.getActionsRemaining());

		bakery.drawFromPantry(pantry[1]);
		assertEquals(0, bakery.getActionsRemaining());
	}

	@Test
	public void testGetBakeableLayers__All() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		String[] ingredients = {"flour", "sugar", "eggs", "butter", "fruit"};
		setupCurrentHand(bakery, ingredients);

		Collection<Layer> layers = bakery.getBakeableLayers();
		assertEquals(6, layers.size());

		List<String> layerNames = new ArrayList<String>();
		for (Layer layer: layers) {
			layerNames.add(layer.toString());
		}
		assertTrue(layerNames.contains("biscuit"));
		assertTrue(layerNames.contains("crème pât"));
		assertTrue(layerNames.contains("icing"));
		assertTrue(layerNames.contains("jam"));
		assertTrue(layerNames.contains("pastry"));
		assertTrue(layerNames.contains("sponge"));
	}

	@Test
	public void testGetBakeableLayers__Most() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		String[] ingredients = {"flour", "sugar", "eggs", "butter"};
		setupCurrentHand(bakery, ingredients);

		Collection<Layer> layers = bakery.getBakeableLayers();
		assertEquals(5, layers.size());

		List<String> layerNames = new ArrayList<String>();
		for (Layer layer: layers) {
			layerNames.add(layer.toString());
		}
		assertTrue(layerNames.contains("biscuit"));
		assertTrue(layerNames.contains("crème pât"));
		assertTrue(layerNames.contains("icing"));
		assertTrue(layerNames.contains("pastry"));
		assertTrue(layerNames.contains("sponge"));
	}

	@Test
	public void testGetBakeableLayers__One() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		String[] ingredients = {"sugar", "butter"};
		setupCurrentHand(bakery, ingredients);


		Collection<Layer> layers = bakery.getBakeableLayers();
		assertEquals(1, layers.size());

		List<String> layerNames = new ArrayList<String>();
		for (Layer layer: layers) {
			layerNames.add(layer.toString());
		}
		assertTrue(layerNames.contains("icing"));
	}

	@Test
	public void testGetBakeableLayers__None() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		String[] ingredients = {"sugar"};
		setupCurrentHand(bakery, ingredients);

		Collection<Layer> layers = bakery.getBakeableLayers();
		assertEquals(0, layers.size());
	}


	@Test
	public void testGetCurrentPlayer() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		ArrayList<String> names = new ArrayList<String>();
		names.add(bakery.getCurrentPlayer().toString());
		bakery.endTurn();
		names.add(bakery.getCurrentPlayer().toString());
		bakery.endTurn();
		names.add(bakery.getCurrentPlayer().toString());
		bakery.endTurn();

		// We have three players so over three turns we should have seen three different players
		HashSet<String> uniqueNames = new HashSet<String>(names);
		assertEquals(3, uniqueNames.size());

		// In the second round, we should see the same players in the exact same order
		assertEquals(names.get(0), bakery.getCurrentPlayer().toString());
		bakery.endTurn();
		assertEquals(names.get(1), bakery.getCurrentPlayer().toString());
		bakery.endTurn();
		assertEquals(names.get(2), bakery.getCurrentPlayer().toString());
		bakery.endTurn();
	}

	@Test
	public void testGetFulfillableCustomers() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");


		String[] recipe1 = {"flour", "butter", "sugar", "eggs"};
		String[] garnish1 = {"chocolate"};
		CustomerOrder customer1 = createCustomerOrder(layers, "cake", recipe1, garnish1);

		String[] recipe2 = {"flour", "butter", "eggs"};
		String[] garnish2 = {};
		CustomerOrder customer2 = createCustomerOrder(layers, "pancake", recipe2, garnish2);

		String[] recipe3 = {"bread flour", "salt", "yeast", "olive oil"};
		String[] garnish3 = {"olive oil", "rosemary"};
		CustomerOrder customer3 = createCustomerOrder(layers, "focaccia", recipe3, garnish3);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(customer1);
		customCustomers.add(customer2);
		customCustomers.add(customer3);

		setupActiveCustomers(bakery, customCustomers);

		String[] ingredients = {"flour", "sugar", "eggs", "butter", "fruit", "chocolate"};
		setupCurrentHand(bakery, ingredients);

		Collection<CustomerOrder> fulfillable = getFulfilableCustomersWrapper(bakery);
		assertNotNull(fulfillable);
		assertEquals(2, fulfillable.size());
		assertTrue(fulfillable.contains(customer1));
		assertTrue(fulfillable.contains(customer2));
	}

	@Test
	public void testGetFulfillableCustomers__None() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");


		String[] recipe1 = {"self-raising flour", "butter", "sugar", "eggs"};
		String[] garnish1 = {"chocolate"};
		CustomerOrder customer1 = createCustomerOrder(layers, "cake", recipe1, garnish1);

		String[] recipe2 = {"flour", "unsalted butter", "eggs"};
		String[] garnish2 = {};
		CustomerOrder customer2 = createCustomerOrder(layers, "pancake", recipe2, garnish2);

		String[] recipe3 = {"bread flour", "salt", "yeast", "olive oil"};
		String[] garnish3 = {"olive oil", "rosemary"};
		CustomerOrder customer3 = createCustomerOrder(layers, "focaccia", recipe3, garnish3);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(customer1);
		customCustomers.add(customer2);
		customCustomers.add(customer3);

		setupActiveCustomers(bakery, customCustomers);

		String[] ingredients = {"flour", "sugar", "eggs", "butter", "fruit", "chocolate"};
		setupCurrentHand(bakery, ingredients);
		
		Collection<CustomerOrder> fulfillable = getFulfilableCustomersWrapper(bakery);
		assertNotNull(fulfillable);
		assertEquals(0, fulfillable.size());
	}

	@Test
	public void testGetFulfillableCustomers__EmptyCustomers() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();

		setupActiveCustomers(bakery, customCustomers);

		String[] ingredients = {"flour", "sugar", "eggs", "butter", "fruit", "chocolate"};
		setupCurrentHand(bakery, ingredients);
		
		Collection<CustomerOrder> fulfillable = getFulfilableCustomersWrapper(bakery);
		assertNotNull(fulfillable);
		assertEquals(0, fulfillable.size());
	}

	@Test
	public void testGetFulfillableCustomers__EmptyHand() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");


		String[] recipe1 = {"self-raising flour", "butter", "sugar", "eggs"};
		String[] garnish1 = {"chocolate"};
		CustomerOrder customer1 = createCustomerOrder(layers, "cake", recipe1, garnish1);

		String[] recipe2 = {"flour", "unsalted butter", "eggs"};
		String[] garnish2 = {};
		CustomerOrder customer2 = createCustomerOrder(layers, "pancake", recipe2, garnish2);

		String[] recipe3 = {"bread flour", "salt", "yeast", "olive oil"};
		String[] garnish3 = {"olive oil", "rosemary"};
		CustomerOrder customer3 = createCustomerOrder(layers, "focaccia", recipe3, garnish3);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(customer1);
		customCustomers.add(customer2);
		customCustomers.add(customer3);

		setupActiveCustomers(bakery, customCustomers);

		String[] ingredients = {};
		setupCurrentHand(bakery, ingredients);
		
		Collection<CustomerOrder> fulfillable = getFulfilableCustomersWrapper(bakery);
		assertNotNull(fulfillable);
		assertEquals(0, fulfillable.size());
	}

	@Test
	public void testGetGarnishableCustomers__One() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");


		String[] recipe1 = {"flour", "butter", "sugar", "eggs"};
		String[] garnish1 = {"chocolate"};
		CustomerOrder customer1 = createCustomerOrder(layers, "cake", recipe1, garnish1);

		String[] recipe2 = {"flour", "butter", "eggs"};
		String[] garnish2 = {"maple syrup"};
		CustomerOrder customer2 = createCustomerOrder(layers, "pancake", recipe2, garnish2);

		String[] recipe3 = {"bread flour", "salt", "yeast", "olive oil"};
		String[] garnish3 = {"olive oil", "rosemary"};
		CustomerOrder customer3 = createCustomerOrder(layers, "focaccia", recipe3, garnish3);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(customer1);
		customCustomers.add(customer2);
		customCustomers.add(customer3);

		setupActiveCustomers(bakery, customCustomers);

		String[] ingredients = {"flour", "sugar", "eggs", "butter", "fruit", "chocolate"};
		setupCurrentHand(bakery, ingredients);
		
		Collection<CustomerOrder> garnishable = bakery.getGarnishableCustomers();
		assertEquals(1, garnishable.size());
		assertTrue(garnishable.contains(customer1));
	}

	@Test
	public void testGetGarnishableCustomers__None() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		String[] recipe1 = {"flour", "butter", "sugar", "eggs"};
		String[] garnish1 = {"chocolate ganache"};
		CustomerOrder customer1 = createCustomerOrder(layers, "cake", recipe1, garnish1);

		String[] recipe2 = {"flour", "butter", "eggs"};
		String[] garnish2 = {"maple syrup"};
		CustomerOrder customer2 = createCustomerOrder(layers, "pancake", recipe2, garnish2);

		String[] recipe3 = {"bread flour", "salt", "yeast", "olive oil"};
		String[] garnish3 = {"sugar"};
		CustomerOrder customer3 = createCustomerOrder(layers, "bread", recipe3, garnish3);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(customer1);
		customCustomers.add(customer2);
		customCustomers.add(customer3);

		setupActiveCustomers(bakery, customCustomers);

		String[] ingredients = {"flour", "sugar", "eggs", "butter", "fruit", "chocolate"};
		setupCurrentHand(bakery, ingredients);
		
		Collection<CustomerOrder> garnishable = bakery.getGarnishableCustomers();
		assertEquals(0, garnishable.size());
	}

	@Test
	public void testGetLayers__AllLayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Collection<Layer> layers2 = bakery.getLayers();

		HashMap<String, Integer> counts = new HashMap<>();
		for (Ingredient ing: layers2) {
			counts.merge(ing.toString(), 1, Integer::sum);
		}

		assertEquals(6, counts.size());
		assertEquals(1, counts.get("biscuit"));
		assertEquals(1, counts.get("crème pât"));
		assertEquals(1, counts.get("icing"));
		assertEquals(1, counts.get("jam"));
		assertEquals(1, counts.get("pastry"));
		assertEquals(1, counts.get("sponge"));
	}

	@Test
	public void testGetLayers__NoLayer() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");
		layers.clear();

		Collection<Layer> layers2 = bakery.getLayers();

		HashMap<String, Integer> counts = new HashMap<>();
		for (Ingredient ing: layers2) {
			counts.merge(ing.toString(), 1, Integer::sum);
		}

		assertEquals(0, counts.size());
	}

	@Test
	public void testGetLayers__SomeLayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");
		layers.removeIf(layer -> (layer.toString().equals("biscuit")));
		layers.removeIf(layer -> (layer.toString().equals("jam")));
		layers.removeIf(layer -> (layer.toString().equals("pastry")));

		Collection<Layer> layers2 = bakery.getLayers();

		HashMap<String, Integer> counts = new HashMap<>();
		for (Ingredient ing: layers2) {
			counts.merge(ing.toString(), 1, Integer::sum);
		}

		assertEquals(3, counts.size());
		assertEquals(1, counts.get("crème pât"));
		assertEquals(1, counts.get("icing"));
		assertEquals(1, counts.get("sponge"));
	}

	@Test
	public void testGetLayers__CustomLayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");
		layers.clear();
		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>();
		recipe.add(new Ingredient("butter"));
		recipe.add(new Ingredient("flour"));
		recipe.add(new Ingredient("eggs"));
		layers.add(new Layer("choux", recipe));

		recipe = new ArrayList<Ingredient>();
		recipe.add(new Ingredient("butter"));
		recipe.add(new Ingredient("flour"));
		recipe.add(new Ingredient("eggs"));
		recipe.add(new Ingredient("buttermilk"));
		recipe.add(new Ingredient("cocoa"));
		layers.add(new Layer("red velvet cake", recipe));

		Collection<Layer> layers2 = bakery.getLayers();

		HashMap<String, Integer> counts = new HashMap<>();
		for (Ingredient ing: layers2) {
			counts.merge(ing.toString(), 1, Integer::sum);
		}

		assertEquals(2, counts.size());
		assertEquals(1, counts.get("choux"));
		assertEquals(1, counts.get("red velvet cake"));
	}

	@Test
	public void testGetPantry() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantry = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantry");
		assertEquals(pantry, bakery.getPantry());
	}

	@Test
	public void testGetPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Player> players = (Collection<Player>)FunctionalHelper.getFieldValue(bakery, "players");
		assertEquals(players, bakery.getPlayers());
	}

	@Test
	public void testGetCustomers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Customers waiting = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");
		assertEquals(waiting, bakery.getCustomers());
	}

	@Test
	public void testGetPassCard1() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Player[] players = bakery.getPlayers().toArray(new Player[0]);
		Player sourcePlayer = players[0];
		Player targetPlayer = players[2];

		@SuppressWarnings("unchecked")
		List<Ingredient> sourceHand = (List<Ingredient>)FunctionalHelper.getFieldValue(sourcePlayer, "hand");
		@SuppressWarnings("unchecked")
		List<Ingredient> targetHand = (List<Ingredient>)FunctionalHelper.getFieldValue(targetPlayer, "hand");

		sourceHand.clear();
		sourceHand.add(new Ingredient("flour"));
		sourceHand.add(new Ingredient("sugar"));
		sourceHand.add(new Ingredient("sugar"));

		targetHand.clear();
		targetHand.add(new Ingredient("flour"));
		targetHand.add(new Ingredient("butter"));
		targetHand.add(new Ingredient("eggs"));

		bakery.passCard(new Ingredient("sugar"), targetPlayer);

		assertEquals(2, sourceHand.size());
		assertTrue(sourceHand.contains(new Ingredient("flour")));
		assertTrue(sourceHand.contains(new Ingredient("sugar")));

		assertEquals(4, targetHand.size());
		assertTrue(targetHand.contains(new Ingredient("flour")));
		assertTrue(targetHand.contains(new Ingredient("sugar")));
		assertTrue(targetHand.contains(new Ingredient("butter")));
		assertTrue(targetHand.contains(new Ingredient("eggs")));

		assertEquals(1, bakery.getActionsPermitted() - bakery.getActionsRemaining());
	}

	@Test
	public void testGetPassCard2() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		bakery.endTurn();

		Player[] players = bakery.getPlayers().toArray(new Player[0]);
		Player sourcePlayer = players[1];
		Player targetPlayer = players[2];
		
		@SuppressWarnings("unchecked")
		List<Ingredient> sourceHand = (List<Ingredient>)FunctionalHelper.getFieldValue(sourcePlayer, "hand");
		@SuppressWarnings("unchecked")
		List<Ingredient> targetHand = (List<Ingredient>)FunctionalHelper.getFieldValue(targetPlayer, "hand");

		sourceHand.clear();
		sourceHand.add(new Ingredient("flour"));
		sourceHand.add(new Ingredient("fruit"));

		targetHand.clear();
		targetHand.add(new Ingredient("flour"));
		targetHand.add(new Ingredient("eggs"));

		bakery.passCard(new Ingredient("flour"), targetPlayer);

		assertEquals(1, sourceHand.size());
		assertTrue(sourceHand.contains(new Ingredient("fruit")));

		assertEquals(3, targetHand.size());
		assertTrue(targetHand.contains(new Ingredient("flour")));
		assertTrue(targetHand.contains(new Ingredient("eggs")));
		int countFlour = 0;
		for (Ingredient ing: targetHand) {
			if (ing.toString().equals("flour")){
				countFlour++;
			}
		}
		assertEquals(2, countFlour);
		assertEquals(1, bakery.getActionsPermitted() - bakery.getActionsRemaining());

		bakery.passCard(new Ingredient("fruit"), targetPlayer);

		assertEquals(0, sourceHand.size());

		assertEquals(4, targetHand.size());
		assertTrue(targetHand.contains(new Ingredient("flour")));
		assertTrue(targetHand.contains(new Ingredient("eggs")));
		assertTrue(targetHand.contains(new Ingredient("fruit")));
		
		countFlour = 0;
		for (Ingredient ing: targetHand) {
			if (ing.toString().equals("flour")){
				countFlour++;
			}
		}
		assertEquals(2, countFlour);
		assertEquals(2, bakery.getActionsPermitted() - bakery.getActionsRemaining());
	}

	@Test
	public void testPrintCustomerServiceRecord__OnlyFulfilled() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

		@SuppressWarnings("unchecked")
		ArrayList<CustomerOrder> inactiveCustomers = (ArrayList<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

		for (CustomerOrder customer: customerDeck) {
			customer.setStatus(CustomerOrder.CustomerOrderStatus.FULFILLED);
			inactiveCustomers.add(customer);
		}

		PrintStream stdout = System.out;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output));
		bakery.printCustomerServiceRecord();
		System.setOut(stdout);

		String expected = """
			Happy customers eating baked goods: 5 (0 garnished) 
			Gone to Greggs instead: 0
			""";

		//assertEquals(expected, output.toString());
		String txt = output.toString().toLowerCase();
		assertTrue((txt.contains("0") || txt.contains("zero")));
		assertTrue((txt.contains("5") || txt.contains("five")));
		
	}

	@Test
	public void testPrintCustomerServiceRecord__OnlyGarnished() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

		@SuppressWarnings("unchecked")
		ArrayList<CustomerOrder> inactiveCustomers = (ArrayList<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

		for (CustomerOrder customer: customerDeck) {
			customer.setStatus(CustomerOrder.CustomerOrderStatus.GARNISHED);
			inactiveCustomers.add(customer);
		}

		PrintStream stdout = System.out;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output));
		bakery.printCustomerServiceRecord();
		System.setOut(stdout);

		String expected = """
			Happy customers eating baked goods: 5 (5 garnished) 
			Gone to Greggs instead: 0
			""";

		//assertEquals(expected, output.toString());
		String txt = output.toString().toLowerCase();
		assertTrue((txt.contains("0") || txt.contains("zero")));
		assertTrue((txt.contains("5") || txt.contains("five")));
	}

	@Test
	public void testPrintCustomerServiceRecord__Mixed() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");

		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> customerDeck = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "customerDeck");

		@SuppressWarnings("unchecked")
		ArrayList<CustomerOrder> inactiveCustomers = (ArrayList<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

		for (CustomerOrder customer: customerDeck) {
			inactiveCustomers.add(customer);
		}

		for (int i = 0; i < 5; i++) {
			if ((i % 3) == 0) inactiveCustomers.get(i).setStatus(CustomerOrder.CustomerOrderStatus.GIVEN_UP);
			if ((i % 3) == 1) inactiveCustomers.get(i).setStatus(CustomerOrder.CustomerOrderStatus.FULFILLED);
			if ((i % 3) == 2) inactiveCustomers.get(i).setStatus(CustomerOrder.CustomerOrderStatus.GARNISHED);
		}

		PrintStream stdout = System.out;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output));
		bakery.printCustomerServiceRecord();
		System.setOut(stdout);

		String expected = """
			Happy customers eating baked goods: 3 (1 garnished) 
			Gone to Greggs instead: 2
			""";

		//assertEquals(expected, output.toString());
		String txt = output.toString().toLowerCase();
		assertTrue((txt.contains("1") || txt.contains("one")));
		assertTrue((txt.contains("2") || txt.contains("two")));
		assertTrue((txt.contains("3") || txt.contains("three")));
	}

	@Test
	public void testPrintGameState__WaitingCustomers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		// Setup pantry

		Collection<Ingredient> pantry = bakery.getPantry();

		String[] pantryIngredients = {"flour", "eggs", "oil", "butter", "eggs"};
		pantry.clear();
		for (String ing: pantryIngredients) {
			pantry.add(stringToIngredient(layers, ing));
		}

		// Setup active customers
		String[] recipe1 = {"biscuit", "butter", "chocolate"};
		String[] garnish1 = {"chocolate"};
		CustomerOrder customer1 = createCustomerOrder(layers, "chocolate bombe", recipe1, garnish1);

		String[] recipe2 = {"biscuit", "butter", "chocolate", "sugar"};
		String[] garnish2 = {};
		CustomerOrder customer2 = createCustomerOrder(layers, "millionaire's shortbread", recipe2, garnish2);

		String[] recipe3 = {"icing", "sponge", "chocolate"};
		String[] garnish3 = {"chocolate"};
		CustomerOrder customer3 = createCustomerOrder(layers, "almond & chocolate torte", recipe3, garnish3);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(customer1);
		customCustomers.add(customer2);
		customCustomers.add(customer3);
		setupActiveCustomers(bakery, customCustomers);

		// Setup Hand
		
		bakery.endTurn();
		bakery.endTurn();
		
		String[] handIngredients = {"flour", "flour", "butter", "butter", "butter", "eggs", "sugar", "sugar", "chocolate", "walnuts"};
		setupCurrentHand(bakery, handIngredients);

		bakery.bakeLayer((Layer)stringToIngredient(layers, "biscuit"));
		bakery.bakeLayer((Layer)stringToIngredient(layers, "icing"));
		bakery.fulfillOrder(customer1, false);

		String expected = """
				Layers:
				  | ---------------- || ---------------- || ---------------- || ---------------- || ---------------- || ---------------- |
				  |     BISCUIT      ||    CRÈME PÂT     ||      ICING       ||       JAM        ||      PASTRY      ||      SPONGE      |
				  | Recipe:          || Recipe:          || Recipe:          || Recipe:          || Recipe:          || Recipe:          |
				  |   Eggs, Flour,   ||  Butter, Eggs,   ||  Butter, Sugar   ||   Fruit, Sugar   ||  Butter, Flour   ||  Butter, Eggs,   |
				  |      Sugar       ||      Sugar       ||                  ||                  ||                  ||   Flour, Sugar   |
				  | ---------------- || ---------------- || ---------------- || ---------------- || ---------------- || ---------------- |
				Pantry:
				  | -------------------- || -------------------- || -------------------- || -------------------- || -------------------- |
				  |        FLOUR         ||         EGGS         ||         OIL          ||        BUTTER        ||         EGGS         |
				  | -------------------- || -------------------- || -------------------- || -------------------- || -------------------- |
				Waiting for service:
				  | ------------------------------------ || ------------------------------------ || ------------------------------------ |
				  |                                      ||       MILLIONAIRE'S SHORTBREAD       ||       ALMOND & CHOCOLATE TORTE       |
				  |                                      || Recipe:                              || Recipe:                              |
				  |                                      ||  Biscuit, Butter, Chocolate, Sugar   ||       Icing, Sponge, Chocolate       |
				  |                                      ||                                      || Garnish:                             |
				  |                                      ||                                      ||              Chocolate               |
				  | ------------------------------------ || ------------------------------------ || ------------------------------------ |
				
				Happy customers eating baked goods: 1 (0 garnished)
				Gone to Greggs instead: 2
				
				PlayerC it's your turn. Your hand contains: Butter, Flour, Icing, Walnuts
				""";

		PrintStream stdout = System.out;
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		System.setOut(new PrintStream(output));
		bakery.printGameState();
		System.setOut(stdout);

		//assertEquals(expected, output.toString());

		// Ideally we would test whether what you printed matches the expected string
		// but you are free to not use the StringUtils
		// So, instead I'll just check whether you print every single word we would expect in the output

		assertTrue(output.toString().length() > 10);
		assertTrue(output.toString().toLowerCase().contains("biscuit"));
		assertTrue(output.toString().toLowerCase().contains("CRÈME PÂT".toLowerCase()));
		assertTrue(output.toString().toLowerCase().contains("icing"));
		assertTrue(output.toString().toLowerCase().contains("jam"));
		assertTrue(output.toString().toLowerCase().contains("pastry"));
		assertTrue(output.toString().toLowerCase().contains("sponge"));
		assertTrue(output.toString().toLowerCase().contains("flour"));
		assertTrue(output.toString().toLowerCase().contains("eggs"));
		assertTrue(output.toString().toLowerCase().contains("oil"));
		assertTrue(output.toString().toLowerCase().contains("butter"));
		assertTrue(output.toString().toLowerCase().contains("eggs"));
		assertTrue(output.toString().toLowerCase().contains("millionaire's shortbread"));
		assertTrue(output.toString().toLowerCase().contains("almond & chocolate torte"));
	}

	@Test
	public void testPrintGameState__WaitingCustomersGarnished() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		// Setup pantry

		Collection<Ingredient> pantry = bakery.getPantry();

		String[] pantryIngredients = {"flour", "eggs", "oil", "butter", "eggs"};
		pantry.clear();
		for (String ing: pantryIngredients) {
			pantry.add(stringToIngredient(layers, ing));
		}

		// Setup active customers
		String[] recipe1 = {"biscuit", "butter", "chocolate"};
		String[] garnish1 = {"chocolate"};
		CustomerOrder customer1 = createCustomerOrder(layers, "chocolate bombe", recipe1, garnish1);

		String[] recipe2 = {"biscuit", "butter", "chocolate", "sugar"};
		String[] garnish2 = {};
		CustomerOrder customer2 = createCustomerOrder(layers, "millionaire's shortbread", recipe2, garnish2);

		String[] recipe3 = {"icing", "sponge", "chocolate"};
		String[] garnish3 = {"chocolate"};
		CustomerOrder customer3 = createCustomerOrder(layers, "almond & chocolate torte", recipe3, garnish3);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(customer1);
		customCustomers.add(customer2);
		customCustomers.add(customer3);
		setupActiveCustomers(bakery, customCustomers);

		// Setup Hand
		
		bakery.endTurn();
		bakery.endTurn();
		
		String[] handIngredients = {"flour", "butter", "butter", "eggs", "sugar", "sugar", "chocolate", "chocolate", "walnuts"};
		setupCurrentHand(bakery, handIngredients);

		bakery.bakeLayer((Layer)stringToIngredient(layers, "biscuit"));
		bakery.bakeLayer((Layer)stringToIngredient(layers, "icing"));
		bakery.fulfillOrder(customer1, true);

		String expected = """
				Layers:
				  | ---------------- || ---------------- || ---------------- || ---------------- || ---------------- || ---------------- |
				  |     BISCUIT      ||    CRÈME PÂT     ||      ICING       ||       JAM        ||      PASTRY      ||      SPONGE      |
				  | Recipe:          || Recipe:          || Recipe:          || Recipe:          || Recipe:          || Recipe:          |
				  |   Eggs, Flour,   ||  Butter, Eggs,   ||  Butter, Sugar   ||   Fruit, Sugar   ||  Butter, Flour   ||  Butter, Eggs,   |
				  |      Sugar       ||      Sugar       ||                  ||                  ||                  ||   Flour, Sugar   |
				  | ---------------- || ---------------- || ---------------- || ---------------- || ---------------- || ---------------- |
				Pantry:
				  | -------------------- || -------------------- || -------------------- || -------------------- || -------------------- |
				  |        FLOUR         ||         EGGS         ||         OIL          ||        BUTTER        ||         EGGS         |
				  | -------------------- || -------------------- || -------------------- || -------------------- || -------------------- |
				Waiting for service:
				  | ------------------------------------ || ------------------------------------ || ------------------------------------ |
				  |                                      ||       MILLIONAIRE'S SHORTBREAD       ||       ALMOND & CHOCOLATE TORTE       |
				  |                                      || Recipe:                              || Recipe:                              |
				  |                                      ||  Biscuit, Butter, Chocolate, Sugar   ||       Icing, Sponge, Chocolate       |
				  |                                      ||                                      || Garnish:                             |
				  |                                      ||                                      ||              Chocolate               |
				  | ------------------------------------ || ------------------------------------ || ------------------------------------ |
				
				Happy customers eating baked goods: 1 (1 garnished)
				Gone to Greggs instead: 2
				
				PlayerC it's your turn. Your hand contains:""";

		PrintStream stdout = System.out;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output));
		bakery.printGameState();
		System.setOut(stdout);

		//assertEquals(expected, output.toString().substring(0, output.toString().lastIndexOf(":") + 1));

		// Ideally we would test whether what you printed matches the expected string
		// but you are free to not use the StringUtils
		// So, instead I'll just check whether you print every single word we would expect in the output

		assertTrue(output.toString().length() > 10);
		assertTrue(output.toString().toLowerCase().contains("biscuit"));
		assertTrue(output.toString().toLowerCase().contains("CRÈME PÂT".toLowerCase()));
		assertTrue(output.toString().toLowerCase().contains("icing"));
		assertTrue(output.toString().toLowerCase().contains("jam"));
		assertTrue(output.toString().toLowerCase().contains("pastry"));
		assertTrue(output.toString().toLowerCase().contains("sponge"));
		assertTrue(output.toString().toLowerCase().contains("flour"));
		assertTrue(output.toString().toLowerCase().contains("eggs"));
		assertTrue(output.toString().toLowerCase().contains("oil"));
		assertTrue(output.toString().toLowerCase().contains("butter"));
		assertTrue(output.toString().toLowerCase().contains("eggs"));
		assertTrue(output.toString().toLowerCase().contains("millionaire's shortbread"));
		assertTrue(output.toString().toLowerCase().contains("almond & chocolate torte"));
	}


	@Test
	public void testPrintGameState__NoWaitingCustomers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		// Setup pantry

		Collection<Ingredient> pantry = bakery.getPantry();

		String[] pantryIngredients = {"flour", "eggs", "oil", "butter", "eggs"};
		pantry.clear();
		for (String ing: pantryIngredients) {
			pantry.add(stringToIngredient(layers, ing));
		}

		// Setup active customers
		String[] recipe1 = {"biscuit", "butter", "chocolate"};
		String[] garnish1 = {"chocolate"};
		CustomerOrder customer1 = createCustomerOrder(layers, "chocolate bombe", recipe1, garnish1);

		String[] recipe2 = {"biscuit", "butter", "chocolate", "sugar"};
		String[] garnish2 = {};
		CustomerOrder customer2 = createCustomerOrder(layers, "millionaire's shortbread", recipe2, garnish2);

		String[] recipe3 = {"icing", "sponge", "chocolate"};
		String[] garnish3 = {"chocolate"};
		CustomerOrder customer3 = createCustomerOrder(layers, "almond & chocolate torte", recipe3, garnish3);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(customer1);
		customCustomers.add(customer2);
		customCustomers.add(customer3);
		setupActiveCustomers(bakery, customCustomers);

		// Setup Hand
		
		bakery.endTurn();
		bakery.endTurn();
		bakery.endTurn();
		bakery.endTurn();
		bakery.endTurn();
		bakery.endTurn();
		
		String[] handIngredients = {"flour", "flour", "butter", "butter", "butter", "eggs", "sugar", "sugar", "chocolate", "walnuts"};
		setupCurrentHand(bakery, handIngredients);

		bakery.bakeLayer((Layer)stringToIngredient(layers, "biscuit"));
		bakery.bakeLayer((Layer)stringToIngredient(layers, "icing"));
		bakery.fulfillOrder(customer1, false);

		String expected = """
		Layers:
		  | ---------------- || ---------------- || ---------------- || ---------------- || ---------------- || ---------------- |
		  |     BISCUIT      ||    CRÈME PÂT     ||      ICING       ||       JAM        ||      PASTRY      ||      SPONGE      |
		  | Recipe:          || Recipe:          || Recipe:          || Recipe:          || Recipe:          || Recipe:          |
		  |   Eggs, Flour,   ||  Butter, Eggs,   ||  Butter, Sugar   ||   Fruit, Sugar   ||  Butter, Flour   ||  Butter, Eggs,   |
		  |      Sugar       ||      Sugar       ||                  ||                  ||                  ||   Flour, Sugar   |
		  | ---------------- || ---------------- || ---------------- || ---------------- || ---------------- || ---------------- |
		Pantry:
		  | -------------------- || -------------------- || -------------------- || -------------------- || -------------------- |
		  |        FLOUR         ||         EGGS         ||         OIL          ||        BUTTER        ||         EGGS         |
		  | -------------------- || -------------------- || -------------------- || -------------------- || -------------------- |
		No customers waiting -- time for a brew :)
		  
		Happy customers eating baked goods: 1 (0 garnished)
		Gone to Greggs instead: 4
		  
		PlayerA it's your turn. Your hand contains: Butter, Flour, Icing, Walnuts
		""";

		PrintStream stdout = System.out;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output));
		bakery.printGameState();
		System.setOut(stdout);

		//assertEquals(expected, output.toString());
		
		// Ideally we would test whether what you printed matches the expected string
		// but you are free to not use the StringUtils
		// So, instead I'll just check whether you print every single word we would expect in the output

		assertTrue(output.toString().length() > 10);
		assertTrue(output.toString().toLowerCase().contains("biscuit"));
		assertTrue(output.toString().toLowerCase().contains("CRÈME PÂT".toLowerCase()));
		assertTrue(output.toString().toLowerCase().contains("icing"));
		assertTrue(output.toString().toLowerCase().contains("jam"));
		assertTrue(output.toString().toLowerCase().contains("pastry"));
		assertTrue(output.toString().toLowerCase().contains("sponge"));
		assertTrue(output.toString().toLowerCase().contains("flour"));
		assertTrue(output.toString().toLowerCase().contains("eggs"));
		assertTrue(output.toString().toLowerCase().contains("oil"));
		assertTrue(output.toString().toLowerCase().contains("butter"));
		assertTrue(output.toString().toLowerCase().contains("eggs"));
		assertTrue(output.toString().toLowerCase().contains("walnuts"));
	}
	

	@Test
	public void testRefreshPantry__InitialState() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");
		assertTrue(pantryDiscard.isEmpty());

		Collection<Ingredient> pantry = bakery.getPantry();

		assertEquals(5, pantry.size());

		ArrayList<Ingredient> pantryCopy = new ArrayList<Ingredient>(pantry);
		ArrayList<Ingredient> pantryDiscardCopy = new ArrayList<Ingredient>(pantryDiscard);

		bakery.refreshPantry();
		assertEquals(5, pantry.size());
		
		assertEquals(pantryCopy.size() + pantryDiscardCopy.size(), pantryDiscard.size());
		for (Ingredient ing: pantryCopy) {
			assertTrue(pantryDiscard.contains(ing));
		}
		assertEquals(1, bakery.getActionsPermitted() - bakery.getActionsRemaining());
	}

	@Test
	public void testRefreshPantry__EmptyPantry() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");
		assertTrue(pantryDiscard.isEmpty());

		Collection<Ingredient> pantry = bakery.getPantry();
		pantry.clear();

		ArrayList<Ingredient> pantryCopy = new ArrayList<Ingredient>(pantry);
		ArrayList<Ingredient> pantryDiscardCopy = new ArrayList<Ingredient>(pantryDiscard);

		bakery.refreshPantry();
		assertEquals(5, pantry.size());
		
		assertEquals(pantryCopy.size() + pantryDiscardCopy.size(), pantryDiscard.size());
		for (Ingredient ing: pantryCopy) {
			assertTrue(pantryDiscard.contains(ing));
		}
		assertEquals(1, bakery.getActionsPermitted() - bakery.getActionsRemaining());
	}

	@Test
	public void testRefreshPantry__SingleIngredientInPantry() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");
		assertTrue(pantryDiscard.isEmpty());

		Collection<Ingredient> pantry = bakery.getPantry();
		pantry.clear();
		pantry.add(new Ingredient("flour"));

		ArrayList<Ingredient> pantryCopy = new ArrayList<Ingredient>(pantry);
		ArrayList<Ingredient> pantryDiscardCopy = new ArrayList<Ingredient>(pantryDiscard);

		bakery.refreshPantry();
		assertEquals(5, pantry.size());
		
		assertEquals(pantryCopy.size() + pantryDiscardCopy.size(), pantryDiscard.size());
		for (Ingredient ing: pantryCopy) {
			assertTrue(pantryDiscard.contains(ing));
		}
		assertEquals(1, bakery.getActionsPermitted() - bakery.getActionsRemaining());
	}

	@Test
	public void testRefreshPantry__MultipleRefreshes() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		MagicBakery bakery = bakeryFactory();
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		bakery.refreshPantry();
		bakery.endTurn();
		bakery.refreshPantry();

		Collection<Ingredient> pantry = bakery.getPantry();
		ArrayList<Ingredient> pantryCopy = new ArrayList<Ingredient>(pantry);
		ArrayList<Ingredient> pantryDiscardCopy = new ArrayList<Ingredient>(pantryDiscard);

		bakery.refreshPantry();

		pantry = bakery.getPantry();
		assertEquals(5, pantry.size());
		
		assertEquals(pantryCopy.size() + pantryDiscardCopy.size(), pantryDiscard.size());
		for (Ingredient ing: pantryCopy) {
			assertTrue(pantryDiscard.contains(ing));
		}
		assertEquals(2, bakery.getActionsPermitted() - bakery.getActionsRemaining());
	}

	@Test
	public void testStartGame__TwoPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("A");
		playerNames.add("B");
		MagicBakery bakery = new MagicBakery(314159265, "./io/ingredients.csv", "./io/layers.csv");

		// This is what we test
		bakery.startGame(playerNames, "./io/customers.csv");

		// ======== customers ========

		// customers was initialised and 1 Customer is ready
		assertEquals(1, bakery.getCustomers().size());

		// ======== pantryDeck ========

		// pantryDeck has the right number of cards: numIngredientCards - numPlayers * 3 cards - 5
		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDeck = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDeck");
		assertEquals(63 - 5 - 3 * playerNames.size(), pantryDeck.size());

		// pantryDeck was properly shuffled
		Ingredient[] pantryDeckArray = pantryDeck.toArray(new Ingredient[0]);
		assertEquals("sugar", pantryDeckArray[0].toString());
		assertEquals("chocolate", pantryDeckArray[4].toString());
		assertEquals("eggs", pantryDeckArray[8].toString());
		assertEquals("sugar", pantryDeckArray[12].toString());

		// ======== pantry ========

		// The pantry has 5 cards
		Collection<Ingredient> pantry = bakery.getPantry();
		assertEquals(5, pantry.size());

		// The pantry was populated after shuffling pantryDeck, but before the players get their cards
		HashMap<Ingredient, Integer> counts = countIngredients(pantry);
		assertEquals(2, counts.get(new Ingredient("butter")));
		assertEquals(2, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));

		// ======== players ========

		// We have two players
		Player[] players = bakery.getPlayers().toArray(new Player[0]);
		assertEquals(2, players.length);

		// Player names are correct
		for (int i = 0; i < players.length; ++i) {
			assertEquals(playerNames.get(i), players[i].toString());
		}

		// Each player has three Ingredient cards
		for (Player player: players) {
			assertEquals(3, player.getHand().size());
		}

		// Players took cards in the right order
		counts = countIngredients(players[0].getHand());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));

		counts = countIngredients(players[1].getHand());
		assertEquals(1, counts.get(new Ingredient("flour")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));		
	}

	@Test
	public void testStartGame__ThreePlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("Jill");
		playerNames.add("Jack");
		playerNames.add("Joe");
		MagicBakery bakery = new MagicBakery(314159265, "./io/ingredients.csv", "./io/layers.csv");

		// This is what we test
		bakery.startGame(playerNames, "./io/customers.csv");

		// ======== customers ========

		// customers was initialised and 2 Customer are ready
		assertEquals(2, bakery.getCustomers().size());

		// ======== pantryDeck ========

		// pantryDeck has the right number of cards: numIngredientCards - numPlayers * 3 cards - 5
		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDeck = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDeck");
		assertEquals(63 - 5 - 3 * playerNames.size(), pantryDeck.size());

		// pantryDeck was properly shuffled
		Ingredient[] pantryDeckArray = pantryDeck.toArray(new Ingredient[0]);
		assertEquals("sugar", pantryDeckArray[1].toString());
		assertEquals("sugar", pantryDeckArray[7].toString());
		assertEquals("sugar", pantryDeckArray[13].toString());
		assertEquals("chocolate", pantryDeckArray[22].toString());

		// ======== pantry ========

		// The pantry has 5 cards
		Collection<Ingredient> pantry = bakery.getPantry();
		assertEquals(5, pantry.size());

		// The pantry was populated after shuffling pantryDeck, but before the players get their cards
		HashMap<Ingredient, Integer> counts = countIngredients(pantry);
		assertEquals(2, counts.get(new Ingredient("butter")));
		assertEquals(2, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));

		// ======== players ========

		// We have three players
		Player[] players = bakery.getPlayers().toArray(new Player[0]);
		assertEquals(3, players.length);

		// Player names are correct
		for (int i = 0; i < players.length; ++i) {
			assertEquals(playerNames.get(i), players[i].toString());
		}

		// Each player has three Ingredient cards
		for (Player player: players) {
			assertEquals(3, player.getHand().size());
		}

		// Players took cards in the right order
		counts = countIngredients(players[0].getHand());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));

		counts = countIngredients(players[1].getHand());
		assertEquals(1, counts.get(new Ingredient("flour")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));

		counts = countIngredients(players[2].getHand());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(2, counts.get(new Ingredient("fruit")));
	}

	@Test
	public void testStartGame__FourPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("P1");
		playerNames.add("P2");
		playerNames.add("P3");
		playerNames.add("P4");
		MagicBakery bakery = new MagicBakery(314159265, "./io/ingredients.csv", "./io/layers.csv");

		// This is what we test
		bakery.startGame(playerNames, "./io/customers.csv");

		// ======== customers ========

		// customers was initialised and 1 Customer is ready
		assertEquals(1, bakery.getCustomers().size());

		// ======== pantryDeck ========

		// pantryDeck has the right number of cards: numIngredientCards - numPlayers * 3 cards - 5
		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDeck = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDeck");
		assertEquals(63 - 5 - 3 * playerNames.size(), pantryDeck.size());

		// pantryDeck was properly shuffled
		Ingredient[] pantryDeckArray = pantryDeck.toArray(new Ingredient[0]);
		assertEquals("sugar", pantryDeckArray[0].toString());
		assertEquals("sugar", pantryDeckArray[12].toString());
		assertEquals("eggs", pantryDeckArray[17].toString());
		assertEquals("flour", pantryDeckArray[29].toString());

		// ======== pantry ========

		// The pantry has 5 cards
		Collection<Ingredient> pantry = bakery.getPantry();
		assertEquals(5, pantry.size());

		// The pantry was populated after shuffling pantryDeck, but before the players get their cards
		HashMap<Ingredient, Integer> counts = countIngredients(pantry);
		assertEquals(2, counts.get(new Ingredient("butter")));
		assertEquals(2, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));

		// ======== players ========

		// We have four players
		Player[] players = bakery.getPlayers().toArray(new Player[0]);
		assertEquals(4, players.length);

		// Player names are correct
		for (int i = 0; i < players.length; ++i) {
			assertEquals(playerNames.get(i), players[i].toString());
		}

		// Each player has three Ingredient cards
		for (Player player: players) {
			assertEquals(3, player.getHand().size());
		}

		// Players took cards in the right order
		counts = countIngredients(players[0].getHand());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));

		counts = countIngredients(players[1].getHand());
		assertEquals(1, counts.get(new Ingredient("flour")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));

		counts = countIngredients(players[2].getHand());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(2, counts.get(new Ingredient("fruit")));

		counts = countIngredients(players[3].getHand());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("chocolate")));
	}

	@Test
	public void testStartGame__FivePlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("One");
		playerNames.add("Two");
		playerNames.add("Four");
		playerNames.add("Eight");
		playerNames.add("Sixteen");
		MagicBakery bakery = new MagicBakery(314159265, "./io/ingredients.csv", "./io/layers.csv");

		// This is what we test
		bakery.startGame(playerNames, "./io/customers.csv");

		// ======== customers ========

		// customers was initialised and 2 Customers are ready
		assertEquals(2, bakery.getCustomers().size());

		// ======== pantryDeck ========

		// pantryDeck has the right number of cards: numIngredientCards - numPlayers * 3 cards - 5
		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDeck = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDeck");
		assertEquals(63 - 5 - 3 * playerNames.size(), pantryDeck.size());

		// pantryDeck was properly shuffled
		Ingredient[] pantryDeckArray = pantryDeck.toArray(new Ingredient[0]);
		assertEquals("sugar", pantryDeckArray[7].toString());
		assertEquals("eggs", pantryDeckArray[17].toString());
		assertEquals("eggs", pantryDeckArray[37].toString());
		assertEquals("butter", pantryDeckArray[41].toString());

		// ======== pantry ========

		// The pantry has 5 cards
		Collection<Ingredient> pantry = bakery.getPantry();
		assertEquals(5, pantry.size());

		// The pantry was populated after shuffling pantryDeck, but before the players get their cards
		HashMap<Ingredient, Integer> counts = countIngredients(pantry);
		assertEquals(2, counts.get(new Ingredient("butter")));
		assertEquals(2, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));

		// ======== players ========

		// We have five players
		Player[] players = bakery.getPlayers().toArray(new Player[0]);
		assertEquals(5, players.length);

		// Player names are correct
		for (int i = 0; i < players.length; ++i) {
			assertEquals(playerNames.get(i), players[i].toString());
		}

		// Each player has three Ingredient cards
		for (Player player: players) {
			assertEquals(3, player.getHand().size());
		}

		// Players took cards in the right order
		counts = countIngredients(players[0].getHand());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));

		counts = countIngredients(players[1].getHand());
		assertEquals(1, counts.get(new Ingredient("flour")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));

		counts = countIngredients(players[2].getHand());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(2, counts.get(new Ingredient("fruit")));

		counts = countIngredients(players[3].getHand());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("chocolate")));

		counts = countIngredients(players[4].getHand());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("flour")));
	}

	@Test
	public void testPantryDeckIsShuffledCorrectly__TwoPlayersPiSeed() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("A");
		playerNames.add("B");

		MagicBakery bakery = new MagicBakery(314159265, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		// First check the pantryDeck contents. If this is wrong, something is seriously wrong with either the pantryDeck itself, or pantryDeck is initialised before customers, or the seed is not used right

		@SuppressWarnings("unchecked")
		ArrayList<Ingredient> pantryDeck = new ArrayList<Ingredient>((Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDeck"));
		assertEquals("sugar", pantryDeck.get(0).toString());
		assertEquals("sugar", pantryDeck.get(7).toString());
		assertEquals("sugar", pantryDeck.get(13).toString());
		assertEquals("sugar", pantryDeck.get(23).toString());
		assertEquals("sugar", pantryDeck.get(31).toString());

		// Check the pantry. If pantryDeck was okay but pantry was not, it was probably initialised in the wrong order

		HashMap<Ingredient, Integer> counts = countIngredients(bakery.getPantry());
		assertEquals(2, counts.get(new Ingredient("butter")));
		assertEquals(2, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));

		// If the players hands are wrong, they got the cards in the wrong order

		Player[] players = bakery.getPlayers().toArray(new Player[0]);

		counts = countIngredients(players[0].getHand());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));

		counts = countIngredients(players[1].getHand());
		assertEquals(1, counts.get(new Ingredient("flour")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));
	}

	@Test
	public void testPantryDeckIsShuffledCorrectly__ThreePlayersAvoSeed() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("A");
		playerNames.add("B");
		playerNames.add("C");

		MagicBakery bakery = new MagicBakery(602214076, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		// First check the pantryDeck contents. If this is wrong, something is seriously wrong with either the pantryDeck itself, or pantryDeck is initialised before customers, or the seed is not used right

		@SuppressWarnings("unchecked")
		ArrayList<Ingredient> pantryDeck = new ArrayList<Ingredient>((Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDeck"));
		assertEquals("sugar", pantryDeck.get(0).toString());
		assertEquals("chocolate", pantryDeck.get(7).toString());
		assertEquals("sugar", pantryDeck.get(13).toString());
		assertEquals("sugar", pantryDeck.get(23).toString());
		assertEquals("butter", pantryDeck.get(31).toString());

		// Check the pantry. If pantryDeck was okay but pantry was not, it was probably initialised in the wrong order

		HashMap<Ingredient, Integer> counts = countIngredients(bakery.getPantry());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("fruit")));
		assertEquals(2, counts.get(new Ingredient("sugar")));

		// If the players hands are wrong, they got the cards in the wrong order

		Player[] players = bakery.getPlayers().toArray(new Player[0]);

		counts = countIngredients(players[0].getHand());
		assertEquals(2, counts.get(new Ingredient("butter")));
		assertEquals(1, counts.get(new Ingredient("eggs")));

		counts = countIngredients(players[1].getHand());
		assertEquals(1, counts.get(new Ingredient("flour")));
		assertEquals(2, counts.get(new Ingredient("eggs")));

		counts = countIngredients(players[2].getHand());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("sugar")));
	}

	@Test
	public void testPantryDeckIsShuffledCorrectly__FourPlayersEulerSeed() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("A");
		playerNames.add("B");
		playerNames.add("C");
		playerNames.add("D");

		MagicBakery bakery = new MagicBakery(271828, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		// First check the pantryDeck contents. If this is wrong, something is seriously wrong with either the pantryDeck itself, or pantryDeck is initialised before customers, or the seed is not used right

		@SuppressWarnings("unchecked")
		ArrayList<Ingredient> pantryDeck = new ArrayList<Ingredient>((Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDeck"));
		assertEquals("eggs", pantryDeck.get(0).toString());
		assertEquals("eggs", pantryDeck.get(7).toString());
		assertEquals("eggs", pantryDeck.get(13).toString());
		assertEquals("sugar", pantryDeck.get(23).toString());
		assertEquals("eggs", pantryDeck.get(31).toString());

		// Check the pantry. If pantryDeck was okay but pantry was not, it was probably initialised in the wrong order

		HashMap<Ingredient, Integer> counts = countIngredients(bakery.getPantry());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(1, counts.get(new Ingredient("flour")));
		assertEquals(3, counts.get(new Ingredient("sugar")));

		// If the players hands are wrong, the got the cards in the wrong order

		Player[] players = bakery.getPlayers().toArray(new Player[0]);

		counts = countIngredients(players[0].getHand());
		assertEquals(1, counts.get(new Ingredient("eggs")));
		assertEquals(1, counts.get(new Ingredient("flour")));
		assertEquals(1, counts.get(new Ingredient("sugar")));

		counts = countIngredients(players[1].getHand());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(1, counts.get(new Ingredient("chocolate")));
		assertEquals(1, counts.get(new Ingredient("flour")));

		counts = countIngredients(players[2].getHand());
		assertEquals(1, counts.get(new Ingredient("butter")));
		assertEquals(2, counts.get(new Ingredient("fruit")));

		counts = countIngredients(players[3].getHand());
		assertEquals(1, counts.get(new Ingredient("chocolate")));
		assertEquals(1, counts.get(new Ingredient("flour")));
		assertEquals(1, counts.get(new Ingredient("sugar")));
	}

	@Test
	public void testSerialisation__RecreatingOriginalBakery() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, ClassNotFoundException {
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("A");
		playerNames.add("B");
		playerNames.add("C");

		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");
		bakery.endTurn();
		bakery.endTurn();
		bakery.endTurn();
		bakery.endTurn();
		bakery.endTurn();

		File output = File.createTempFile("serial", ".bin");
		bakery.saveState(output);


		MagicBakery bakery2 = assertDoesNotThrow(() -> MagicBakery.loadState(output));

		assertEquals(bakery.getCurrentPlayer().toString(), bakery2.getCurrentPlayer().toString());
		//assertEquals(bakery.getCustomers().getCustomers(), bakery2.getCustomers().getCustomers());

		assertEquals(bakery.getLayers(), bakery2.getLayers());
		assertNotSame(bakery.getLayers(), bakery2.getLayers());
		assertEquals(bakery.getPantry(), bakery2.getPantry());
		assertNotSame(bakery.getPantry(), bakery2.getPantry());
		assertEquals(bakery.getPlayers().stream().map(Player::toString).collect(Collectors.toList()), bakery2.getPlayers().stream().map(Player::toString).collect(Collectors.toList()));
		assertNotSame(bakery.getPlayers(), bakery2.getPlayers());

		PrintStream stdout = System.out;

		ByteArrayOutputStream output1 = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output1));
		bakery.printCustomerServiceRecord();

		ByteArrayOutputStream output2 = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output2));
		bakery2.printCustomerServiceRecord();

		assertEquals(output1.toString(), output2.toString());

		output1 = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output1));
		bakery.printGameState();

		output2 = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output2));
		bakery2.printGameState();

		assertEquals(output1.toString(), output2.toString());

		System.setOut(stdout);
	}
}
