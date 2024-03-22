package test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import bakery.CustomerOrder;
import bakery.Ingredient;
import bakery.Layer;

@Tag("functional")
@Tag("CustomOrder")
public class CustomerOrderTest {
	static CustomerOrder order_single;
	static CustomerOrder order_multi;

	static List<Ingredient> recipe_single;
	static List<Ingredient> recipe_multi;

	static List<Ingredient> garnish_single;
	static List<Ingredient> garnish_multi;

	static List<Ingredient> pantry;
	static List<Ingredient> pantryWithOneDuck;
	static List<Ingredient> pantryWithTwoDucks;

	@BeforeAll
	public static void setUp() {
		recipe_multi = new ArrayList<Ingredient>();
		recipe_multi.add(new Ingredient("butter"));
		recipe_multi.add(new Ingredient("flour"));
		recipe_multi.add(new Ingredient("sugar"));

		garnish_multi = new ArrayList<Ingredient>();
		garnish_multi.add(new Ingredient("chocolate"));
		garnish_multi.add(new Ingredient("walnuts"));
		order_multi = new CustomerOrder("some recipe", recipe_multi, garnish_multi, 3);

		//

		recipe_single = new ArrayList<Ingredient>();
		recipe_single.add(new Ingredient("ready-made sponge"));

		garnish_single = new ArrayList<Ingredient>();
		garnish_single.add(new Ingredient("truffles"));
		order_single = new CustomerOrder("some cake", recipe_single, garnish_single, 1);

		pantry = new ArrayList<Ingredient>();
		pantry.add(new Ingredient("flour"));
		pantry.add(new Ingredient("sugar"));
		pantry.add(new Ingredient("salt"));
		pantry.add(new Ingredient("dessicated coconut"));
		pantry.add(new Ingredient("almond flour"));
		pantry.add(new Ingredient("butter"));
		pantry.add(new Ingredient("chocolate"));
		pantry.add(new Ingredient("walnuts"));

		pantryWithOneDuck = new ArrayList<Ingredient>(pantry);
		pantryWithOneDuck.add(Ingredient.HELPFUL_DUCK);

		pantryWithTwoDucks = new ArrayList<Ingredient>(pantry);
		pantryWithTwoDucks.add(Ingredient.HELPFUL_DUCK);
		pantryWithTwoDucks.add(Ingredient.HELPFUL_DUCK);
	}

	// Constructors

	@Test
	public void testConstructor1() throws NoSuchFieldException, IllegalAccessException {
		assertEquals("some recipe", FunctionalHelper.getFieldValue(order_multi, "name"));
		assertEquals(3, FunctionalHelper.getFieldValue(order_multi, "level"));
		assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, FunctionalHelper.getFieldValue(order_multi, "status"));

		@SuppressWarnings("unchecked")
		List<Ingredient> recipe = (List<Ingredient>)FunctionalHelper.getFieldValue(order_multi, "recipe");
		assertSame(recipe_multi, recipe);

		@SuppressWarnings("unchecked")
		List<Ingredient> garnish = (List<Ingredient>)FunctionalHelper.getFieldValue(order_multi, "garnish");
		assertSame(garnish_multi, garnish);
	}

	@Test
	public void testConstructor2() throws NoSuchFieldException, IllegalAccessException {
		assertEquals("some cake", FunctionalHelper.getFieldValue(order_single, "name"));
		assertEquals(1, FunctionalHelper.getFieldValue(order_single, "level"));
		assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, FunctionalHelper.getFieldValue(order_single, "status"));

		@SuppressWarnings("unchecked")
		List<Ingredient> recipe = (List<Ingredient>)FunctionalHelper.getFieldValue(order_single, "recipe");
		assertSame(recipe_single, recipe);

		@SuppressWarnings("unchecked")
		List<Ingredient> garnish = (List<Ingredient>)FunctionalHelper.getFieldValue(order_single, "garnish");
		assertSame(garnish_single, garnish);
	}

	@Test
	public void testConstructor__EmptyGarnish() throws NoSuchFieldException, IllegalAccessException {
		CustomerOrder order = new CustomerOrder("another order", recipe_multi, new ArrayList<Ingredient>(), 1);
		assertEquals("another order", FunctionalHelper.getFieldValue(order, "name"));
		assertEquals(1, FunctionalHelper.getFieldValue(order, "level"));
		assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, FunctionalHelper.getFieldValue(order, "status"));

		@SuppressWarnings("unchecked")
		List<Ingredient> recipe = (List<Ingredient>)FunctionalHelper.getFieldValue(order, "recipe");
		assertSame(recipe_multi, recipe);

		@SuppressWarnings("unchecked")
		List<Ingredient> garnish = (List<Ingredient>)FunctionalHelper.getFieldValue(order, "garnish");
		assertEquals(0, garnish.size());
	}

	// Getters
	@Test
	public void testGetGarnish() throws NoSuchFieldException, IllegalAccessException {
		@SuppressWarnings("unchecked")
		List<Ingredient> garnish = (List<Ingredient>)FunctionalHelper.getFieldValue(order_multi, "garnish");
		assertSame(order_multi.getGarnish(), garnish);
	}

	@Test
	public void testGetGarnishDescription() throws NoSuchFieldException, IllegalAccessException {
		assertEquals("chocolate, walnuts", order_multi.getGarnishDescription());
	}

	@Test
	public void testGetGarnishDescription_SingleIngredient() throws NoSuchFieldException, IllegalAccessException {
		assertEquals("truffles", order_single.getGarnishDescription());
	}

	@Test
	public void testGetLevel() throws NoSuchFieldException, IllegalAccessException {
		assertEquals(FunctionalHelper.getFieldValue(order_multi, "level"), order_multi.getLevel());
	}

	@Test
	public void testGetRecipe() throws NoSuchFieldException, IllegalAccessException {
		@SuppressWarnings("unchecked")
		List<Ingredient> recipe = (List<Ingredient>)FunctionalHelper.getFieldValue(order_multi, "recipe");
		assertSame(order_multi.getRecipe(), recipe);
	}

	@Test
	public void testGetRecipeDescription() throws NoSuchFieldException, IllegalAccessException {
		assertEquals("butter, flour, sugar", order_multi.getRecipeDescription());
	}

	@Test
	public void testGetRecipeDescription_SingleIngredient() throws NoSuchFieldException, IllegalAccessException {
		assertEquals("ready-made sponge", order_single.getRecipeDescription());
	}

	@Test
	public void testGetStatus() throws NoSuchFieldException, IllegalAccessException {
		CustomerOrder.CustomerOrderStatus status = (CustomerOrder.CustomerOrderStatus)FunctionalHelper.getFieldValue(order_multi, "status");
		assertEquals(status, order_multi.getStatus());
	}

	@Test
	public void testToString() throws NoSuchFieldException, IllegalAccessException {
		assertEquals("some recipe", order_multi.toString());
	}


	// Setters
	@Test
	public void testSetStatus() throws NoSuchFieldException, IllegalAccessException {
		order_multi.setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
		CustomerOrder.CustomerOrderStatus status = (CustomerOrder.CustomerOrderStatus)FunctionalHelper.getFieldValue(order_multi, "status");
		assertEquals(CustomerOrder.CustomerOrderStatus.IMPATIENT, status);

		order_multi.setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
		status = (CustomerOrder.CustomerOrderStatus)FunctionalHelper.getFieldValue(order_multi, "status");
		assertEquals(CustomerOrder.CustomerOrderStatus.WAITING, status);
	}

	@Test
	public void testAbandon() throws NoSuchFieldException, IllegalAccessException {
		order_multi.abandon();
		CustomerOrder.CustomerOrderStatus status = (CustomerOrder.CustomerOrderStatus)FunctionalHelper.getFieldValue(order_multi, "status");
		assertEquals(CustomerOrder.CustomerOrderStatus.GIVEN_UP, status);
		order_multi.setStatus(CustomerOrder.CustomerOrderStatus.WAITING);

	}

	// --- canFulfill()
	@Test
	public void testCanFulfill() throws NoSuchFieldException, IllegalAccessException {

		assertTrue(order_multi.canFulfill(pantry));
		assertFalse(order_single.canFulfill(pantry));
	}

	@Test
	public void testCanFulfill__DoubleIngredient() throws NoSuchFieldException, IllegalAccessException {
		// Does order_multi still can be fulfilled if it needs two butter cards?
		List<Ingredient> recipe = new ArrayList<Ingredient>(recipe_multi);
		recipe.add(new Ingredient("butter"));
		CustomerOrder order1 = new CustomerOrder("some recipe", recipe, garnish_multi, 3);
		assertFalse(order1.canFulfill(pantry));
	}

	@Test
	public void testCanFulfill__MissingLayerIngredient() throws NoSuchFieldException, IllegalAccessException {
		// What if the recipe contains a missing layer?
		List<Ingredient> layer_recipe = new ArrayList<Ingredient>();
		layer_recipe.add(new Ingredient("flour"));
		layer_recipe.add(new Ingredient("olive oil"));
		layer_recipe.add(new Ingredient("salt"));
		layer_recipe.add(new Ingredient("yeast"));

		List<Ingredient> recipe = new ArrayList<Ingredient>(recipe_multi);
		recipe.add(new Layer("bread", layer_recipe));
		CustomerOrder order2 = new CustomerOrder("some other recipe", recipe, garnish_multi, 3);
		assertFalse(order2.canFulfill(pantry));
	}

	// Let's check what happens if we have a Helpful Duck
	@Test
	public void testCanFulfill_WithOneDuck() throws NoSuchFieldException, IllegalAccessException {
		assertTrue(order_multi.canFulfill(pantryWithOneDuck));
		assertTrue(order_single.canFulfill(pantryWithOneDuck));
	}

	@Test
	public void testCanFulfill__DoubleIngredient_WithOneDuck() throws NoSuchFieldException, IllegalAccessException {
		List<Ingredient> recipe = new ArrayList<Ingredient>(recipe_multi);
		recipe.add(new Ingredient("butter"));
		CustomerOrder order1 = new CustomerOrder("some recipe", recipe, garnish_multi, 3);
		assertTrue(order1.canFulfill(pantryWithOneDuck));
	}

	@Test
	public void testCanFulfill__MissingLayerIngredient_WithOneDuck() throws NoSuchFieldException, IllegalAccessException {
		// What if the recipe contains a missing layer?
		List<Ingredient> layer_recipe = new ArrayList<Ingredient>();
		layer_recipe.add(new Ingredient("flour"));
		layer_recipe.add(new Ingredient("olive oil"));
		layer_recipe.add(new Ingredient("salt"));
		layer_recipe.add(new Ingredient("yeast"));

		List<Ingredient> recipe = new ArrayList<Ingredient>(recipe_multi);
		recipe.add(new Layer("bread", layer_recipe));
		CustomerOrder order2 = new CustomerOrder("some other recipe", recipe, garnish_multi, 3);
		assertFalse(order2.canFulfill(pantryWithOneDuck));
	}

	@Test
	public void testCanFulfill__ExtraIngredients_WithOneDuck() throws NoSuchFieldException, IllegalAccessException {
		List<Ingredient> recipe = new ArrayList<Ingredient>(recipe_multi);
		recipe.add(new Ingredient("butter"));
		recipe.add(new Ingredient("sour cream"));
		CustomerOrder order3 = new CustomerOrder("some recipe", recipe, garnish_multi, 3);
		assertFalse(order3.canFulfill(pantryWithOneDuck));
	}


	// -- canGarnish()
	@Test
	public void testCanGarnish() throws NoSuchFieldException, IllegalAccessException {
		assertTrue(order_multi.canGarnish(pantry));
		assertFalse(order_single.canGarnish(pantry));
	}

	@Test
	public void testCanGarnish__DoubleIngredient() throws NoSuchFieldException, IllegalAccessException {
		// Does order_multi still can be garnished if it needs two portions of walnuts?
		List<Ingredient> garnish = new ArrayList<Ingredient>(garnish_multi);
		garnish.add(new Ingredient("walnuts"));
		CustomerOrder order1 = new CustomerOrder("some recipe", recipe_multi, garnish, 3);
		assertFalse(order1.canGarnish(pantry));

		// What if we get more walnuts
		List<Ingredient> biggerPantry = new ArrayList<Ingredient>(pantry);
		biggerPantry.add(new Ingredient("walnuts"));
		assertTrue(order1.canGarnish(biggerPantry));
	}


	@Test
	public void testCanGarnish__MissingLayer() throws NoSuchFieldException, IllegalAccessException {
		// What if the garnish contains a layer?
		List<Ingredient> layer_recipe = new ArrayList<Ingredient>();
		layer_recipe.add(new Ingredient("pecans"));
		layer_recipe.add(new Ingredient("sugar"));
		layer_recipe.add(new Ingredient("milk"));
		layer_recipe.add(new Ingredient("butter"));

		List<Ingredient> garnish = new ArrayList<Ingredient>(garnish_multi);
		garnish.add(new Layer("pralines", layer_recipe));
		CustomerOrder order2 = new CustomerOrder("some other recipe", recipe_multi, garnish, 3);
		assertFalse(order2.canGarnish(pantry));
	}

	@Test
	public void testCanGarnish__WithOneDuck() throws NoSuchFieldException, IllegalAccessException {
		assertTrue(order_multi.canGarnish(pantryWithOneDuck));
		assertTrue(order_single.canGarnish(pantryWithOneDuck));
	}

	@Test
	public void testCanGarnish__DoubleIngredient_WithOneDuck() throws NoSuchFieldException, IllegalAccessException {
		// Does order_multi still can be garnished if it needs two portions of walnuts?
		List<Ingredient> garnish = new ArrayList<Ingredient>(garnish_multi);
		garnish.add(new Ingredient("walnuts"));
		CustomerOrder order1 = new CustomerOrder("some recipe", recipe_multi, garnish, 3);
		assertTrue(order1.canGarnish(pantryWithOneDuck));

		// What if we need even more ingredients
		garnish.add(new Ingredient("walnuts"));
		garnish.add(new Ingredient("vanilla extract"));
		order1 = new CustomerOrder("some recipe", recipe_multi, garnish, 3);
		assertFalse(order1.canGarnish(pantryWithOneDuck));
		assertFalse(order1.canGarnish(pantryWithTwoDucks));
	}

	@Test
	public void testCanGarnish__MissingLayer_WithOneDuck() throws NoSuchFieldException, IllegalAccessException {
		// What if the garnish contains a layer?
		List<Ingredient> layer_recipe = new ArrayList<Ingredient>();
		layer_recipe.add(new Ingredient("pecans"));
		layer_recipe.add(new Ingredient("sugar"));
		layer_recipe.add(new Ingredient("milk"));
		layer_recipe.add(new Ingredient("butter"));

		List<Ingredient> garnish = new ArrayList<Ingredient>(garnish_multi);
		garnish.add(new Layer("pralines", layer_recipe));
		CustomerOrder order2 = new CustomerOrder("some other recipe", recipe_multi, garnish, 3);
		assertFalse(order2.canGarnish(pantryWithOneDuck));
	}

	// --- fulfill()
	@Test
	public void testFulfill__NoGarnish() throws NoSuchFieldException, IllegalAccessException {
		Ingredient[] shouldUse = {new Ingredient("butter"), new Ingredient("flour"), new Ingredient("sugar")};

		List<Ingredient> garnish = new ArrayList<Ingredient>(garnish_multi);
		garnish.add(new Ingredient("sprinkles"));

		CustomerOrder order = new CustomerOrder("some other recipe", recipe_multi, garnish, 1);

		List<Ingredient> used = order.fulfill(pantry, true);
		Collections.sort(used);
		assertTrue(used.equals(Arrays.asList(shouldUse)));
		assertEquals(CustomerOrder.CustomerOrderStatus.FULFILLED, order.getStatus());
	}

	@Test
	public void testFulfill__Garnished() throws NoSuchFieldException, IllegalAccessException {
		Ingredient[] shouldUse = {
			new Ingredient("butter"),
			new Ingredient("chocolate"),
			new Ingredient("flour"),
			new Ingredient("sugar"),
			new Ingredient("walnuts"),
		};

		List<Ingredient> used = order_multi.fulfill(pantry, true);
		Collections.sort(used);
		assertTrue(used.equals(Arrays.asList(shouldUse)));
		assertEquals(CustomerOrder.CustomerOrderStatus.GARNISHED, order_multi.getStatus());
		order_multi.setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
	}

	@Test
	public void testFulfill__RespectsGarnishFlag() throws NoSuchFieldException, IllegalAccessException {
		Ingredient[] shouldUse = {
			new Ingredient("butter"),
			new Ingredient("flour"),
			new Ingredient("sugar"),
		};

		List<Ingredient> used = order_multi.fulfill(pantry, false);
		Collections.sort(used);
		assertTrue(used.equals(Arrays.asList(shouldUse)));
		assertEquals(CustomerOrder.CustomerOrderStatus.FULFILLED, order_multi.getStatus());
		order_multi.setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
	}

	@Test
	public void testFulfill__DoubleIngredient_NoGarnish() throws NoSuchFieldException, IllegalAccessException {
		Ingredient[] shouldUse = {
			new Ingredient("butter"),
			new Ingredient("flour"),
			new Ingredient("sugar"),
			new Ingredient("sugar")};

		List<Ingredient> recipe = new ArrayList<Ingredient>(recipe_multi);
		recipe.add(new Ingredient("sugar"));

		List<Ingredient> garnish = new ArrayList<Ingredient>(garnish_multi);
		garnish.add(new Ingredient("sprinkles"));

		CustomerOrder order = new CustomerOrder("some other recipe", recipe, garnish, 1);

		List<Ingredient> pantry_alt = new ArrayList<Ingredient>(pantry);
		pantry_alt.add(new Ingredient("sugar"));

		List<Ingredient> used = order.fulfill(pantry_alt, true);
		Collections.sort(used);
		assertTrue(used.equals(Arrays.asList(shouldUse)));
		assertEquals(CustomerOrder.CustomerOrderStatus.FULFILLED, order.getStatus());
	}

	@Test
	public void testFulfill__DoubleIngredient_Garnished() throws NoSuchFieldException, IllegalAccessException {
		Ingredient[] shouldUse = {
			new Ingredient("butter"),
			new Ingredient("chocolate"),
			new Ingredient("flour"),
			new Ingredient("sugar"),
			new Ingredient("sugar"),
			new Ingredient("walnuts"),
		};

		List<Ingredient> garnish = new ArrayList<Ingredient>(garnish_multi);
		garnish.add(new Ingredient("sugar"));

		CustomerOrder order = new CustomerOrder("some other recipe", recipe_multi, garnish, 1);

		List<Ingredient> pantry_alt = new ArrayList<Ingredient>(pantry);
		pantry_alt.add(new Ingredient("sugar"));

		List<Ingredient> used = order.fulfill(pantry_alt, true);
		Collections.sort(used);
		assertTrue(used.equals(Arrays.asList(shouldUse)));
		assertEquals(CustomerOrder.CustomerOrderStatus.GARNISHED, order.getStatus());
	}

	@Test
	public void testFulfill__RecipeBlocksGarnish() throws NoSuchFieldException, IllegalAccessException {
		Ingredient[] shouldUse = {
			new Ingredient("butter"),
			new Ingredient("flour"),
			new Ingredient("sugar"),
		};

		List<Ingredient> garnish = new ArrayList<Ingredient>(garnish_multi);
		garnish.add(new Ingredient("sugar"));

		CustomerOrder order = new CustomerOrder("some other recipe", recipe_multi, garnish, 1);

		List<Ingredient> used = order.fulfill(pantry, true);
		Collections.sort(used);
		assertTrue(used.equals(Arrays.asList(shouldUse)));
		assertEquals(CustomerOrder.CustomerOrderStatus.FULFILLED, order.getStatus());
	}

	@Test
	public void testFulfill__HasNoGarnish() throws NoSuchFieldException, IllegalAccessException {
		Ingredient[] shouldUse = {
			new Ingredient("butter"),
			new Ingredient("flour"),
			new Ingredient("sugar"),
		};

		List<Ingredient> garnish = new ArrayList<Ingredient>();
		CustomerOrder order = new CustomerOrder("some other recipe", recipe_multi, garnish, 1);

		List<Ingredient> used = order.fulfill(pantry, true);
		Collections.sort(used);
		assertTrue(used.equals(Arrays.asList(shouldUse)));
		assertEquals(CustomerOrder.CustomerOrderStatus.FULFILLED, order.getStatus());
	}

	@Test
	public void testFulfill__GarnishThanksToDucks() throws NoSuchFieldException, IllegalAccessException {
		Ingredient[] shouldUse = {
			new Ingredient("butter"),
			new Ingredient("chocolate"),
			new Ingredient("flour"),
			Ingredient.HELPFUL_DUCK,
			new Ingredient("sugar"),
			new Ingredient("walnuts"),
		};

		List<Ingredient> garnish = new ArrayList<Ingredient>(garnish_multi);
		garnish.add(new Ingredient("sprinkles"));

		CustomerOrder order = new CustomerOrder("some other recipe", recipe_multi, garnish, 1);

		List<Ingredient> used = order.fulfill(pantryWithOneDuck, true);
		Collections.sort(used);
		assertTrue(used.equals(Arrays.asList(shouldUse)));
		assertEquals(CustomerOrder.CustomerOrderStatus.GARNISHED, order.getStatus());
	}

	@Test
	public void testFulfill__GarnishWithoutUsingDucks() throws NoSuchFieldException, IllegalAccessException {
		Ingredient[] shouldUse = {
			new Ingredient("butter"),
			new Ingredient("chocolate"),
			new Ingredient("flour"),
			new Ingredient("sugar"),
			new Ingredient("walnuts"),
		};

		List<Ingredient> used = order_multi.fulfill(pantryWithOneDuck, true);
		Collections.sort(used);
		assertTrue(used.equals(Arrays.asList(shouldUse)));
		assertEquals(CustomerOrder.CustomerOrderStatus.GARNISHED, order_multi.getStatus());
		order_multi.setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
	}

	@Test
	public void testFulfill__DoubleIngredientUsingDucks() throws NoSuchFieldException, IllegalAccessException {
		Ingredient[] shouldUse = {
			new Ingredient("butter"),
			new Ingredient("chocolate"),
			new Ingredient("flour"),
			Ingredient.HELPFUL_DUCK,
			new Ingredient("sugar"),
			new Ingredient("walnuts"),
		};

		List<Ingredient> garnish = new ArrayList<Ingredient>(garnish_multi);
		garnish.add(new Ingredient("sugar"));

		CustomerOrder order = new CustomerOrder("some other recipe", recipe_multi, garnish, 1);

		List<Ingredient> used = order.fulfill(pantryWithOneDuck, true);
		Collections.sort(used);
		assertTrue(used.equals(Arrays.asList(shouldUse)));
		assertEquals(CustomerOrder.CustomerOrderStatus.GARNISHED, order.getStatus());
	}

	@Test
	public void testFulfill__DoubleIngredientWithoutUsingDucks() throws NoSuchFieldException, IllegalAccessException {
		Ingredient[] shouldUse = {
			new Ingredient("butter"),
			new Ingredient("chocolate"),
			new Ingredient("flour"),
			new Ingredient("sugar"),
			new Ingredient("sugar"),
			new Ingredient("walnuts"),
		};

		List<Ingredient> garnish = new ArrayList<Ingredient>(garnish_multi);
		garnish.add(new Ingredient("sugar"));

		CustomerOrder order = new CustomerOrder("some other recipe", recipe_multi, garnish, 1);

		List<Ingredient> pantry_alt = new ArrayList<Ingredient>(pantryWithOneDuck);
		pantry_alt.add(new Ingredient("sugar"));

		List<Ingredient> used = order.fulfill(pantry_alt, true);
		Collections.sort(used);
		assertTrue(used.equals(Arrays.asList(shouldUse)));
		assertEquals(CustomerOrder.CustomerOrderStatus.GARNISHED, order.getStatus());
	}
}

