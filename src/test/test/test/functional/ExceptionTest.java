package test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import bakery.CustomerOrder;
import bakery.Customers;
import bakery.EmptyPantryException;
import bakery.Ingredient;
import bakery.Layer;
import bakery.MagicBakery;
import bakery.Player;
import bakery.TooManyActionsException;
import bakery.WrongIngredientsException;
import util.CardUtils;
import util.ConsoleUtils;

@Tag("functional")
public class ExceptionTest {

	static ArrayList<Ingredient> pantry;

	@BeforeAll
	public static void setUp() {
		pantry = new ArrayList<Ingredient>();
		pantry.add(new Ingredient("flour"));
		pantry.add(new Ingredient("sugar"));
		pantry.add(new Ingredient("salt"));
		pantry.add(new Ingredient("dessicated coconut"));
		pantry.add(new Ingredient("almond flour"));
		pantry.add(new Ingredient("butter"));
		pantry.add(new Ingredient("chocolate"));
		pantry.add(new Ingredient("walnuts"));
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

	@Test
	void testCustomerOrderConstructor__EmptyRecipe(){
		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>();

		ArrayList<Ingredient> garnish = new ArrayList<Ingredient>();
		garnish.add(new Ingredient("truffles"));

		assertThrows(WrongIngredientsException.class, () -> {new CustomerOrder("empty recipe", recipe, garnish, 1);});
	}

	@Test
	void testCustomerOrderConstructor__NullRecipe(){
		ArrayList<Ingredient> recipe = null;

		ArrayList<Ingredient> garnish = new ArrayList<Ingredient>();
		garnish.add(new Ingredient("truffles"));

		assertThrows(WrongIngredientsException.class, () -> {new CustomerOrder("null recipe", recipe, garnish, 1);});
	}

	@Test
	void testCustomerOrderConstructor__EmptyRecipeAndGarnish(){
		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>();
		ArrayList<Ingredient> garnish = new ArrayList<Ingredient>();
		assertThrows(WrongIngredientsException.class, () -> {new CustomerOrder("empty recipe", recipe, garnish, 1);});
	}

	@Test
	public void testCustomerOrderFulfill__MissingIngredient() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>();
		recipe.add(new Ingredient("bread flour"));

		ArrayList<Ingredient> garnish = new ArrayList<Ingredient>();

		CustomerOrder order = new CustomerOrder("unleavened bread", recipe, garnish, 1);
		CustomerOrder.CustomerOrderStatus old_status = order.getStatus();

		assertThrows(WrongIngredientsException.class, () -> {order.fulfill(pantry, true);});
		assertEquals(old_status, order.getStatus());
	}

	@Test
	public void testCustomerOrderFulfill__DoubleIngredient() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>();
		recipe = new ArrayList<Ingredient>();
		recipe.add(new Ingredient("flour"));
		recipe.add(new Ingredient("butter"));
		recipe.add(new Ingredient("sugar"));
		recipe.add(new Ingredient("sugar"));

		ArrayList<Ingredient> garnish = new ArrayList<Ingredient>();

		CustomerOrder order = new CustomerOrder("brioche", recipe, garnish, 3);
		CustomerOrder.CustomerOrderStatus old_status = order.getStatus();

		assertThrows(WrongIngredientsException.class, () -> {order.fulfill(pantry, true);});
		assertEquals(old_status, order.getStatus());
	}

	@Test
	public void testCustomerOrderFulfill__MissingLayerIngredient() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> layer_recipe = new ArrayList<Ingredient>();
		layer_recipe.add(new Ingredient("flour"));
		layer_recipe.add(new Ingredient("olive oil"));
		layer_recipe.add(new Ingredient("salt"));
		layer_recipe.add(new Ingredient("yeast"));

		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>();
		recipe.add(new Ingredient("butter"));
		recipe.add(new Layer("bread", layer_recipe));

		ArrayList<Ingredient> garnish = new ArrayList<Ingredient>();

		CustomerOrder order = new CustomerOrder("toast", recipe, garnish, 1);
		CustomerOrder.CustomerOrderStatus old_status = order.getStatus();

		assertThrows(WrongIngredientsException.class, () -> {order.fulfill(pantry, true);});
		assertEquals(old_status, order.getStatus());
	}

	// Let's check what happens if we have a Helpful Duck
	@Test
	public void testCustomerOrderFulfill__MissingLayerIngredient_WithOneDuck() throws NoSuchFieldException, IllegalAccessException {
		// What if the recipe contains a missing layer?
		ArrayList<Ingredient> layer_recipe = new ArrayList<Ingredient>();
		layer_recipe.add(new Ingredient("flour"));
		layer_recipe.add(new Ingredient("olive oil"));
		layer_recipe.add(new Ingredient("salt"));
		layer_recipe.add(new Ingredient("yeast"));

		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>();
		recipe.add(new Ingredient("butter"));
		recipe.add(new Layer("bread", layer_recipe));

		ArrayList<Ingredient> garnish = new ArrayList<Ingredient>();

		CustomerOrder order = new CustomerOrder("toast", recipe, garnish, 1);
		CustomerOrder.CustomerOrderStatus old_status = order.getStatus();

		ArrayList<Ingredient> pantryWithDuck = new ArrayList<Ingredient>(pantry);
		pantryWithDuck.add(Ingredient.HELPFUL_DUCK);

		assertThrows(WrongIngredientsException.class, () -> {order.fulfill(pantryWithDuck, true);});
		assertEquals(old_status, order.getStatus());
	}

	@Test
	public void testCustomerOrderFulfill__ExtraIngredients_WithOneDuck() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>();
		recipe = new ArrayList<Ingredient>();
		recipe.add(new Ingredient("flour"));
		recipe.add(new Ingredient("butter"));
		recipe.add(new Ingredient("sugar"));
		recipe.add(new Ingredient("butter"));
		recipe.add(new Ingredient("sour cream"));

		ArrayList<Ingredient> garnish = new ArrayList<Ingredient>();

		CustomerOrder order = new CustomerOrder("brioche", recipe, garnish, 1);
		CustomerOrder.CustomerOrderStatus old_status = order.getStatus();

		ArrayList<Ingredient> pantryWithDuck = new ArrayList<Ingredient>(pantry);
		pantryWithDuck.add(Ingredient.HELPFUL_DUCK);

		assertThrows(WrongIngredientsException.class, () -> {order.fulfill(pantryWithDuck, true);});
		assertEquals(old_status, order.getStatus());
	}

	// MagicBakery Exceptions
	@Test
	public void testMagicBakeryCtor__WithOnePlayer() throws NoSuchFieldException, IllegalAccessException, FileNotFoundException, IOException {
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");

		assertThrows(IllegalArgumentException.class, () -> {bakery.startGame(playerNames, "./io/customers.csv");});
	}

	@Test
	public void testMagicBakeryCtor__WithSixPlayers() throws NoSuchFieldException, IllegalAccessException, FileNotFoundException, IOException {
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		playerNames.add("PlayerC");
		playerNames.add("PlayerD");
		playerNames.add("PlayerE");
		playerNames.add("PlayerF");
		assertThrows(IllegalArgumentException.class, () -> {bakery.startGame(playerNames, "./io/customers.csv");});
	}

	@Test
	public void testBakeLayer__TooManyActionsException__TwoPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		Ingredient[] pantry = bakery.getPantry().toArray(new Ingredient[0]);

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(bakery.getCurrentPlayer(), "hand");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		int layersOrig = layers.size();

		hand.clear();
		hand.add(new Ingredient("flour"));
		hand.add(new Ingredient("sugar"));
		hand.add(new Ingredient("butter"));
		hand.add(new Ingredient("eggs"));

		Layer layer = null;
		for (Layer l: layers) {
			if (l.toString().equals("sponge")) layer = l;
		}
		final Layer layerf = layer;

		bakery.drawFromPantry(pantry[0]);
		bakery.drawFromPantry(pantry[1]);
		bakery.drawFromPantry(pantry[2]);
		assertThrows(TooManyActionsException.class, () -> { bakery.bakeLayer(layerf); });

		assertEquals(layersOrig, layers.size());
		assertEquals(7, hand.size());
		assertEquals(0, pantryDiscard.size());
		assertEquals(0, bakery.getActionsRemaining());
	}

	@Test
	public void testBakeLayer__TooManyActionsException__FourPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		playerNames.add("PlayerC");
		playerNames.add("PlayerD");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		Ingredient[] pantry = bakery.getPantry().toArray(new Ingredient[0]);

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(bakery.getCurrentPlayer(), "hand");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		int layersOrig = layers.size();

		hand.clear();
		hand.add(new Ingredient("flour"));
		hand.add(new Ingredient("sugar"));
		hand.add(new Ingredient("butter"));
		hand.add(new Ingredient("eggs"));

		Layer layer = null;
		for (Layer l: layers) {
			if (l.toString().equals("sponge")) layer = l;
		}
		final Layer layerf = layer;

		bakery.drawFromPantry(pantry[0]);
		bakery.drawFromPantry(pantry[1]);
		assertThrows(TooManyActionsException.class, () -> { bakery.bakeLayer(layerf); });

		assertEquals(layersOrig, layers.size());
		assertEquals(6, hand.size());
		assertEquals(0, pantryDiscard.size());
		assertEquals(0, bakery.getActionsRemaining());
	}

	@Test
	public void testBakeLayer__WrongIngredients() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(bakery.getCurrentPlayer(), "hand");

		@SuppressWarnings("unchecked")
		Collection<Layer> layers = (Collection<Layer>)FunctionalHelper.getFieldValue(bakery, "layers");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		int layersOrig = layers.size();

		hand.clear();
		hand.add(new Ingredient("flour"));
		hand.add(new Ingredient("sugar"));
		hand.add(new Ingredient("butter"));
		hand.add(new Ingredient("oil"));

		Layer layer = null;
		for (Layer l: layers) {
			if (l.toString().equals("sponge")) layer = l;
		}
		final Layer layerf = layer;

		assertThrows(WrongIngredientsException.class, () -> {bakery.bakeLayer(layerf);});
		assertEquals(layersOrig, layers.size());
		assertEquals(4, hand.size());
		assertEquals(0, pantryDiscard.size());
		assertEquals(0, bakery.getActionsPermitted() - bakery.getActionsRemaining());
	}

	@Test
	public void testDrawFromPantryDeck__EmptyPantryException() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException {
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDeck = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDeck");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		// What if both the pantryDeck and pantryDiscard are empty?
		// Not sure this could ever happen organically, but that's what unit tests are for

		pantryDeck.clear();
		pantryDiscard.clear();

		Method mtd = FunctionalHelper.getMethod(MagicBakery.class, "drawFromPantryDeck");
		InvocationTargetException ex = assertThrows(InvocationTargetException.class, () -> {mtd.invoke(bakery);});
		assertEquals(EmptyPantryException.class, ex.getCause().getClass());
		assertEquals(0, pantryDeck.size());
	}

	@Test
	public void testDrawFromPantry__TooManyActionsException__TwoPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantry = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantry");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(bakery.getCurrentPlayer(), "hand");

		pantry.clear();
		pantry.add(new Ingredient("flour"));
		pantry.add(new Ingredient("sugar"));
		pantry.add(new Ingredient("eggs"));
		pantry.add(new Ingredient("butter"));
		
		hand.clear();

		bakery.drawFromPantry(new Ingredient("flour"));
		bakery.drawFromPantry(new Ingredient("sugar"));
		bakery.drawFromPantry(new Ingredient("eggs"));
		assertThrows(TooManyActionsException.class, () -> {bakery.drawFromPantry("butter");});
		assertThrows(TooManyActionsException.class, () -> {bakery.drawFromPantry(new Ingredient("butter"));});

		assertEquals(3, hand.size());
		assertEquals(0, bakery.getActionsRemaining());
	}

	@Test
	public void testDrawFromPantry__TooManyActionsException__FourPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		playerNames.add("PlayerC");
		playerNames.add("PlayerD");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantry = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantry");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(bakery.getCurrentPlayer(), "hand");

		pantry.clear();
		pantry.add(new Ingredient("flour"));
		pantry.add(new Ingredient("sugar"));
		pantry.add(new Ingredient("eggs"));
		pantry.add(new Ingredient("butter"));
		
		hand.clear();

		bakery.drawFromPantry(new Ingredient("flour"));
		bakery.drawFromPantry(new Ingredient("sugar"));
		assertThrows(TooManyActionsException.class, () -> {bakery.drawFromPantry("butter");});
		assertThrows(TooManyActionsException.class, () -> {bakery.drawFromPantry(new Ingredient("butter"));});

		assertEquals(2, hand.size());
		assertEquals(0, bakery.getActionsRemaining());
	}

	@Test
	public void testDrawFromPantry__WrongIngredientsException() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException {
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantry = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantry");

		Player player = bakery.getCurrentPlayer();
		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");

		pantry.clear();
		pantry.add(new Ingredient("flour"));
		pantry.add(new Ingredient("sugar"));
		pantry.add(new Ingredient("eggs"));
		
		hand.clear();
		
		assertThrows(WrongIngredientsException.class, () -> {bakery.drawFromPantry("fruit");});
		assertThrows(WrongIngredientsException.class, () -> {bakery.drawFromPantry(new Ingredient("fruit"));});

		assertEquals(0, hand.size());
		assertEquals(0, bakery.getActionsPermitted() - bakery.getActionsRemaining());
	}

	@Test
	public void testFulfillOrder__TooManyActionsException__TwoPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(bakery.getCurrentPlayer(), "hand");

		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");
		
		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantry = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantry");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		ArrayList<Ingredient> recipe_multi = new ArrayList<Ingredient>();
		recipe_multi.add(new Ingredient("flour"));
		recipe_multi.add(new Ingredient("butter"));
		recipe_multi.add(new Ingredient("sugar"));

		ArrayList<Ingredient> garnish_multi = new ArrayList<Ingredient>();
		CustomerOrder order_multi = new CustomerOrder("some recipe", recipe_multi, garnish_multi, 3);

		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(order_multi);
		setupActiveCustomers(bakery, customCustomers);

		hand.clear();

		pantry.clear();
		pantry.add(new Ingredient("sugar"));
		pantry.add(new Ingredient("flour"));
		pantry.add(new Ingredient("butter"));

		bakery.drawFromPantry(new Ingredient("butter"));
		bakery.drawFromPantry(new Ingredient("sugar"));
		bakery.drawFromPantry(new Ingredient("flour"));
		assertThrows(TooManyActionsException.class, () -> {bakery.fulfillOrder(order_multi, false);});

		assertEquals(3, hand.size());
		assertEquals(2, customers.size());
		assertEquals(0, inactiveCustomers.size());
		assertFalse(inactiveCustomers.contains(order_multi));
		assertEquals(0, pantryDiscard.size());
		assertEquals(0, bakery.getActionsRemaining());

	}

	@Test
	public void testFulfillOrder__TooManyActionsException__FourPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException, InvocationTargetException {
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		playerNames.add("PlayerC");
		playerNames.add("PlayerD");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(bakery.getCurrentPlayer(), "hand");

		Customers customers = (Customers)FunctionalHelper.getFieldValue(bakery, "customers");
		
		@SuppressWarnings("unchecked")
		Collection<CustomerOrder> inactiveCustomers = (Collection<CustomerOrder>)FunctionalHelper.getFieldValue(customers, "inactiveCustomers");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantry = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantry");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		ArrayList<Ingredient> recipe_multi = new ArrayList<Ingredient>();
		recipe_multi.add(new Ingredient("flour"));
		recipe_multi.add(new Ingredient("butter"));
		recipe_multi.add(new Ingredient("sugar"));

		ArrayList<Ingredient> garnish_multi = new ArrayList<Ingredient>();
		CustomerOrder order_multi = new CustomerOrder("some recipe", recipe_multi, garnish_multi, 3);
		
		ArrayList<CustomerOrder> customCustomers = new ArrayList<>();
		customCustomers.add(order_multi);
		setupActiveCustomers(bakery, customCustomers);

		pantry.clear();
		pantry.add(new Ingredient("sugar"));
		pantry.add(new Ingredient("eggs"));
		pantry.add(new Ingredient("butter"));

		hand.clear();
		hand.add(new Ingredient("flour"));

		bakery.drawFromPantry(new Ingredient("butter"));
		bakery.drawFromPantry(new Ingredient("sugar"));
		assertThrows(TooManyActionsException.class, () -> {bakery.fulfillOrder(order_multi, false);});

		assertEquals(3, hand.size());
		assertEquals(2, customers.size());
		assertEquals(0, inactiveCustomers.size());
		assertFalse(inactiveCustomers.contains(order_multi));
		assertEquals(0, pantryDiscard.size());
		assertEquals(0, bakery.getActionsRemaining());

	}

	@Test
	public void testPassCard__TooManyActionsException__TwoPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException {
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantry = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantry");

		Player[] players = bakery.getPlayers().toArray(new Player[0]);
		Player sourcePlayer = players[0];
		Player targetPlayer = players[1];

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

		pantry.clear();
		pantry.add(new Ingredient("sugar"));
		pantry.add(new Ingredient("eggs"));
		pantry.add(new Ingredient("butter"));

		bakery.drawFromPantry(new Ingredient("butter"));
		bakery.drawFromPantry(new Ingredient("sugar"));
		bakery.drawFromPantry(new Ingredient("eggs"));
		assertThrows(TooManyActionsException.class, () -> {bakery.passCard(new Ingredient("flour"), targetPlayer);});

		assertEquals(6, sourceHand.size());
		assertTrue(sourceHand.contains(new Ingredient("flour")));
		assertTrue(sourceHand.contains(new Ingredient("sugar")));
		assertTrue(sourceHand.contains(new Ingredient("butter")));

		assertEquals(3, targetHand.size());
		assertTrue(targetHand.contains(new Ingredient("flour")));
		assertTrue(targetHand.contains(new Ingredient("butter")));
		assertTrue(targetHand.contains(new Ingredient("eggs")));

		assertEquals(0, bakery.getActionsRemaining());
	}

	@Test
	public void testPassCard__TooManyActionsException__FourPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException {
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		playerNames.add("PlayerC");
		playerNames.add("PlayerD");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantry = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantry");

		Player[] players = bakery.getPlayers().toArray(new Player[0]);
		Player sourcePlayer = players[0];
		Player targetPlayer = players[1];

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

		pantry.clear();
		pantry.add(new Ingredient("sugar"));
		pantry.add(new Ingredient("eggs"));
		pantry.add(new Ingredient("butter"));

		bakery.drawFromPantry(new Ingredient("butter"));
		bakery.drawFromPantry(new Ingredient("sugar"));
		assertThrows(TooManyActionsException.class, () -> {bakery.passCard(new Ingredient("flour"), targetPlayer);});

		assertEquals(5, sourceHand.size());
		assertTrue(sourceHand.contains(new Ingredient("flour")));
		assertTrue(sourceHand.contains(new Ingredient("sugar")));

		assertEquals(3, targetHand.size());
		assertTrue(targetHand.contains(new Ingredient("flour")));
		assertTrue(targetHand.contains(new Ingredient("butter")));
		assertTrue(targetHand.contains(new Ingredient("eggs")));

		assertEquals(0, bakery.getActionsRemaining());
	}


	@Test
	public void testPassCard__WrongIngredientsException() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException {
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		playerNames.add("PlayerC");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		Player[] players = bakery.getPlayers().toArray(new Player[0]);
		Player sourcePlayer = players[0];
		Player targetPlayer = players[1];

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

		assertThrows(WrongIngredientsException.class, () -> {bakery.passCard(new Ingredient("fruit"), targetPlayer);});

		assertEquals(3, sourceHand.size());
		assertTrue(sourceHand.contains(new Ingredient("flour")));
		assertTrue(sourceHand.contains(new Ingredient("sugar")));

		assertEquals(3, targetHand.size());
		assertTrue(targetHand.contains(new Ingredient("flour")));
		assertTrue(targetHand.contains(new Ingredient("butter")));
		assertTrue(targetHand.contains(new Ingredient("eggs")));

		assertEquals(0, bakery.getActionsPermitted() - bakery.getActionsRemaining());
	}

	@Test
	public void testRefreshPantry__TooManyActionsException__FourPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException {
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		playerNames.add("PlayerC");
		playerNames.add("PlayerD");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantry = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantry");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		int discardOriginalSize = pantryDiscard.size();

		pantry.clear();
		pantry.add(new Ingredient("sugar"));
		pantry.add(new Ingredient("eggs"));
		pantry.add(new Ingredient("butter"));

		bakery.drawFromPantry(new Ingredient("butter"));
		bakery.drawFromPantry(new Ingredient("sugar"));
		assertThrows(TooManyActionsException.class, () -> { bakery.refreshPantry(); });

		assertEquals(discardOriginalSize, pantryDiscard.size());
		assertEquals(0, bakery.getActionsRemaining());
	}

	@Test
	public void testRefreshPantry__TooManyActionsException__TwoPlayers() throws NoSuchFieldException, IllegalAccessException, IOException, FileNotFoundException {
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add("PlayerA");
		playerNames.add("PlayerB");
		MagicBakery bakery = new MagicBakery(12854, "./io/ingredients.csv", "./io/layers.csv");
		bakery.startGame(playerNames, "./io/customers.csv");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantry = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantry");

		@SuppressWarnings("unchecked")
		Collection<Ingredient> pantryDiscard = (Collection<Ingredient>)FunctionalHelper.getFieldValue(bakery, "pantryDiscard");

		int discardOriginalSize = pantryDiscard.size();

		pantry.clear();
		pantry.add(new Ingredient("sugar"));
		pantry.add(new Ingredient("eggs"));
		pantry.add(new Ingredient("butter"));

		bakery.drawFromPantry(new Ingredient("butter"));
		bakery.drawFromPantry(new Ingredient("sugar"));
		bakery.drawFromPantry(new Ingredient("eggs"));
		assertThrows(TooManyActionsException.class, () -> { bakery.refreshPantry(); });

		assertEquals(discardOriginalSize, pantryDiscard.size());
		assertEquals(0, bakery.getActionsRemaining());
	}

	@Test
	public void testRemoveFromHand__MissingIngredient() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");

		hand.add(new Ingredient("butter"));
		hand.add(new Ingredient("sugar"));
		hand.add(new Ingredient("flour"));

		int oldHandSize = hand.size();
		
		assertThrows(WrongIngredientsException.class, () -> {player.removeFromHand(new Ingredient("double cream"));});
		assertEquals(oldHandSize, hand.size());
	}

	@Test
	public void testRemoveFromHand__SimilarButMissing1() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");

		hand.add(new Ingredient("butter"));
		hand.add(new Ingredient("sugar"));
		hand.add(new Ingredient("flour"));

		int oldHandSize = hand.size();
		
		assertThrows(WrongIngredientsException.class, () -> {player.removeFromHand(new Ingredient("flour OO"));});
		assertEquals(oldHandSize, hand.size());
	}

	@Test
	public void testRemoveFromHand__SimilarButMissing2() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");

		hand.add(new Ingredient("butter"));
		hand.add(new Ingredient("sugar"));
		hand.add(new Ingredient("flour"));

		int oldHandSize = hand.size();
		
		assertThrows(WrongIngredientsException.class, () -> {player.removeFromHand(new Ingredient("unsalted butter"));});
		assertEquals(oldHandSize, hand.size());
	}


	@Test
	public void testLayerConstructor__EmptyRecipe() throws NoSuchFieldException, IllegalAccessException {
		assertThrows(WrongIngredientsException.class, () -> {new Layer("empty", new ArrayList<Ingredient>());});
	}

	@Test
	public void testLayerConstructor__NullRecipe() throws NoSuchFieldException, IllegalAccessException {
		assertThrows(WrongIngredientsException.class, () -> {new Layer("null", null);});
	}

	@Test
	public void testReadCustomerFile__MissingFile() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>();
		recipe.add(new Ingredient("flour"));
		recipe.add(new Ingredient("butter"));

		Collection<Layer> layers = new ArrayList<Layer>();
		layers.add(new Layer("biscuit", recipe));

		assertThrows(FileNotFoundException.class, () -> {CardUtils.readCustomerFile("afd33kmoakfargnb.qqq", layers);});
	}

	@Test
	public void testReadLayerFile__MissingFile() throws NoSuchFieldException, IllegalAccessException {
		assertThrows(FileNotFoundException.class, () -> {CardUtils.readLayerFile(("kbafd749kmkfgnorb.qqq"));});
	}

	@Test
	public void testReadIngredientFile__MissingFile() throws NoSuchFieldException, IllegalAccessException {
		assertThrows(FileNotFoundException.class, () -> {CardUtils.readIngredientFile(("wcuad749kmkfgnorb.qqq"));});
	}

	@Test
	public void testCustomersConstructor__MissingFile() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>();
		recipe.add(new Ingredient("flour"));
		recipe.add(new Ingredient("butter"));

		Collection<Layer> layers = new ArrayList<Layer>();
		layers.add(new Layer("biscuit", recipe));

		assertThrows(FileNotFoundException.class, () -> {new Customers("afd43kmoakfargnb.qqq", new Random(0), layers, 20);});
	}


	@Test
	public void testMagicBakeryConstructor__MissingIngredientFile() throws NoSuchFieldException, IllegalAccessException {
		assertThrows(FileNotFoundException.class, () -> {new MagicBakery(0, "afd93kmoakfargnb.qqq", "./io/layers.csv");});
	}

	@Test
	public void testMagicBakeryConstructor__MissingLayerFile() throws NoSuchFieldException, IllegalAccessException {
		assertThrows(FileNotFoundException.class, () -> {new MagicBakery(0, "./io/ingredients.csv", "afd113kmoakfargnb.qqq");});
	}

	@Test
	public void testMagicBakeryStartGame__MissingCustomerFile() throws NoSuchFieldException, IllegalAccessException {

		MagicBakery bakery;
		try {
			bakery = new MagicBakery(0, "./io/ingredients.csv", "./io/layers.csv");
		} catch (IOException e) {
			fail();
			return;
		}
		List<String> playerNames = new ArrayList<String>();
		playerNames.add("A");
		playerNames.add("V");
		assertThrows(FileNotFoundException.class, () -> {bakery.startGame(playerNames, "adfplgkhyrteqambc.qqq");});
	}

	@Test
	public void testMagicBakerySaveState__InvalidPath() throws NoSuchFieldException, IllegalAccessException {
		MagicBakery bakery;

		List<String> playerNames = new ArrayList<String>();
		playerNames.add("A");
		playerNames.add("V");

		try {
			bakery = new MagicBakery(0, "./io/ingredients.csv", "./io/layers.csv");
			bakery.startGame(playerNames, "./io/customers.csv");
		} catch (IOException e) {
			fail();
			return;
		}
		assertThrows(FileNotFoundException.class, () -> {bakery.saveState(new File("/tmp/foo/bar/pop/push/peek/close/open/adfplgkhyrteqambc.qqq"));});
	}


	@Test
	public void testMagicBakeryLoadState__InvalidPath() throws NoSuchFieldException, IllegalAccessException {
		assertThrows(FileNotFoundException.class, () -> {MagicBakery.loadState(new File("/tmp/foo/bar/pop/push/peek/close/open/a2d1f7p8l4g1k6h8y9r.qqq"));});
	}

	@Test
	public void testMagicBakeryLoadState__InvalidData() throws NoSuchFieldException, IllegalAccessException {
		File file;
		try{
			file = File.createTempFile("bakery", ".bin");
			ObjectOutputStream objectStream = new ObjectOutputStream(new FileOutputStream(file));
        	objectStream.writeObject(new String("1234567"));
        	objectStream.close();
		} catch (IOException e){
			fail();
			return;
		}
		assertThrows(ClassCastException.class, () -> {MagicBakery.loadState(file);});

		File file2;
		try{
			file2 = File.createTempFile("bakery", ".bin");
			FileOutputStream fileStream = new FileOutputStream(file2);
			byte[] txt = {'1', '2', '3', '4', '5', '6', '7'};
        	fileStream.write(txt);
        	fileStream.close();
		} catch (IOException e){
			fail();
			return;
		}
		assertThrows(java.io.ObjectStreamException.class, () -> {MagicBakery.loadState(file2);});
	}
	
	@Test
	public void testPromptEnumerateCollection__EmptyCollection() throws NoSuchFieldException, IllegalAccessException {
		ConsoleUtils console = new ConsoleUtils();
		Method mtd = FunctionalHelper.getMethod(console,"promptEnumerateCollection", String.class, Collection.class);
		InvocationTargetException exc = assertThrows(InvocationTargetException.class, () -> {mtd.invoke(console, new String("xxxx"), new ArrayList<Object>());});
		assertEquals(IllegalArgumentException.class, exc.getCause().getClass());
	}

	@Test
	public void testPromptEnumerateCollection__NullCollection() throws NoSuchFieldException, IllegalAccessException {
		ConsoleUtils console = new ConsoleUtils();
		Method mtd = FunctionalHelper.getMethod(console,"promptEnumerateCollection", String.class, Collection.class);
		InvocationTargetException exc = assertThrows(InvocationTargetException.class, () -> {mtd.invoke(console, new String("xxxx"), null);});
		assertEquals(IllegalArgumentException.class, exc.getCause().getClass());
	}

}
