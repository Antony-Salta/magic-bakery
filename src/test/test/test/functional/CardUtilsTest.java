package test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import util.CardUtils;

import bakery.CustomerOrder;
import bakery.Ingredient;
import bakery.Layer;

@Tag("functional")
@Tag("CardUtils")
public class CardUtilsTest {

	static Method toOrder;
	static Method toLayers;
	static Method toIngredients;

	static List<Ingredient> ingredients;
	static List<Layer> layers;

	@BeforeAll
	public static void setUp() {
		toOrder = FunctionalHelper.getMethod(CardUtils.class, "stringToCustomerOrder", String.class, Collection.class);
		toLayers = FunctionalHelper.getMethod(CardUtils.class, "stringToLayers", String.class);
		toIngredients = FunctionalHelper.getMethod(CardUtils.class, "stringToIngredients", String.class);

		ingredients = new ArrayList<Ingredient>();
		ingredients.add(new Ingredient("A"));
		ingredients.add(new Ingredient("B"));
		ingredients.add(new Ingredient("C"));
		ingredients.add(new Ingredient("D"));
		ingredients.add(new Ingredient("E"));

		List<Ingredient> list;

		layers = new ArrayList<Layer>();

		list = new ArrayList<Ingredient>();
		list.add(new Ingredient("A"));
		list.add(new Ingredient("B"));
		layers.add(new Layer("layerA", list));

		list = new ArrayList<Ingredient>();
		list.add(new Ingredient("B"));
		list.add(new Ingredient("C"));
		layers.add(new Layer("layerB", list));

		list = new ArrayList<Ingredient>();
		list.add(new Ingredient("D"));
		list.add(new Ingredient("E"));
		layers.add(new Layer("layerC", list));

		list = new ArrayList<Ingredient>();
		list.add(new Ingredient("A"));
		list.add(new Ingredient("C"));
		list.add(new Ingredient("E"));
		layers.add(new Layer("layerD", list));
	}

	// Customer Order

	@Test
	public void testStringToCustomerOrder__SingleIngredient__NoGarnish__SingleWord() throws IllegalAccessException, InvocationTargetException {
		if (toOrder == null) fail();

		String line = "2,roulade,A,";
		CustomerOrder order = (CustomerOrder)toOrder.invoke(null, line, layers);
		assertEquals(2, order.getLevel());
		assertEquals("roulade", order.toString());

		List<Ingredient> recipe = order.getRecipe();
		assertEquals("A", recipe.get(0).toString());
		assertEquals(Ingredient.class, recipe.get(0).getClass());

		assertEquals("", order.getGarnishDescription());
	}

	@Test
	public void testStringToCustomerOrder__SingleIngredient__NoGarnish__MultiWord() throws IllegalAccessException, InvocationTargetException {
		if (toOrder == null) fail();

		String line = "2,meringue roulade,multi-word ingredient,";
		CustomerOrder order = (CustomerOrder)toOrder.invoke(null, line, layers);
		assertEquals(2, order.getLevel());
		assertEquals("meringue roulade", order.toString());

		List<Ingredient> recipe = order.getRecipe();
		assertEquals("multi-word ingredient", recipe.get(0).toString());
		assertEquals(Ingredient.class, recipe.get(0).getClass());

		assertEquals("", order.getGarnishDescription());
	}

	@Test
	public void testStringToCustomerOrder__SingleIngredient__Garnish__SingleWord() throws IllegalAccessException, InvocationTargetException {
		if (toOrder == null) fail();

		String line = "1,battenberg,flour,D";
		CustomerOrder order = (CustomerOrder)toOrder.invoke(null, line, layers);
		assertEquals(1, order.getLevel());
		assertEquals("battenberg", order.toString());

		List<Ingredient> recipe = order.getRecipe();
		assertEquals(1, recipe.size());
		assertEquals("flour", recipe.get(0).toString());
		assertEquals(Ingredient.class, recipe.get(0).getClass());

		List<Ingredient> garnish = order.getGarnish();
		assertEquals(1, garnish.size());
		assertEquals("D", garnish.get(0).toString());
		assertEquals(Ingredient.class, garnish.get(0).getClass());
	}

	@Test
	public void testStringToCustomerOrder__SingleLayer__NoGarnish__SingleWord() throws IllegalAccessException, InvocationTargetException {
		if (toOrder == null) fail();

		String line = "2,roulade,layerA,";
		CustomerOrder order = (CustomerOrder)toOrder.invoke(null, line, layers);
		assertEquals(2, order.getLevel());
		assertEquals("roulade", order.toString());

		List<Ingredient> recipe = order.getRecipe();
		assertEquals(1, recipe.size());
		assertEquals("layerA", recipe.get(0).toString());
		assertEquals(Layer.class, recipe.get(0).getClass());

		List<Ingredient> garnish = order.getGarnish();
		assertEquals(0, garnish.size());
	}

	@Test
	public void testStringToCustomerOrder__SingleLayer__LayerGarnish__SingleWord() throws IllegalAccessException, InvocationTargetException {
		if (toOrder == null) fail();

		String line = "1,cheesecake,layerC,layerD";
		CustomerOrder order = (CustomerOrder)toOrder.invoke(null, line, layers);
		assertEquals(1, order.getLevel());
		assertEquals("cheesecake", order.toString());

		List<Ingredient> recipe = order.getRecipe();
		assertEquals(1, recipe.size());
		assertEquals("layerC", recipe.get(0).toString());
		assertEquals(Layer.class, recipe.get(0).getClass());

		List<Ingredient> garnish = order.getGarnish();
		assertEquals(1, garnish.size());
		assertEquals("layerD", garnish.get(0).toString());
		assertEquals(Layer.class, garnish.get(0).getClass());
	}

	@Test
	public void testStringToCustomerOrder__LayerAndIngredient__MixedGarnish__SingleWord() throws IllegalAccessException, InvocationTargetException {
		if (toOrder == null) fail();

		String line = "2,celebration cake,eggs;layerC;milk,layerD;sprinkles";
		CustomerOrder order = (CustomerOrder)toOrder.invoke(null, line, layers);
		assertEquals(2, order.getLevel());
		assertEquals("celebration cake", order.toString());

		List<Ingredient> recipe = order.getRecipe();
		assertEquals(3, recipe.size());
		assertEquals("layerC", recipe.get(1).toString());
		assertEquals(Layer.class, recipe.get(1).getClass());
		assertEquals("eggs", recipe.get(0).toString());
		assertEquals(Ingredient.class, recipe.get(0).getClass());
		assertEquals("milk", recipe.get(2).toString());
		assertEquals(Ingredient.class, recipe.get(2).getClass());

		List<Ingredient> garnish = order.getGarnish();
		assertEquals(2, garnish.size());
		assertEquals("sprinkles", garnish.get(1).toString());
		assertEquals(Ingredient.class, garnish.get(1).getClass());
		assertEquals("layerD", garnish.get(0).toString());
		assertEquals(Layer.class, garnish.get(0).getClass());
	}

	@Test
	public void testStringToCustomerOrder__RedundantSpaces() throws IllegalAccessException, InvocationTargetException {
		if (toOrder == null) fail();

		String line = "2,   meringue roulade,multi-word ingredient   ,";
		CustomerOrder order = (CustomerOrder)toOrder.invoke(null, line, layers);
		assertEquals(2, order.getLevel());
		assertEquals("meringue roulade", order.toString());

		List<Ingredient> recipe = order.getRecipe();
		assertEquals("multi-word ingredient", recipe.get(0).toString());
		assertEquals(Ingredient.class, recipe.get(0).getClass());

		assertEquals("", order.getGarnishDescription());
	}

	// Layers

	@Test
	public void testStringToLayer__TwoIngredients__SingleWord() throws IllegalAccessException, InvocationTargetException {
		if (toLayers == null) fail();

		String line = "icing,butter;sugar";
		@SuppressWarnings("unchecked")
		List<Layer> layers = (List<Layer>)toLayers.invoke(null, line);
		assertTrue(layers.size() > 0);
		Layer layer = layers.get(0);
		assertEquals("icing", layer.toString());

		
		List<Ingredient> recipe = layer.getRecipe();
		assertEquals(2, recipe.size());
		assertEquals("butter", recipe.get(0).toString());
		assertEquals("sugar", recipe.get(1).toString());

		for (int i = 1; i < layers.size(); i++) {
			assertEquals(layer.toString(), layers.get(i).toString());
			assertEquals(layer.getRecipeDescription(), layers.get(i).getRecipeDescription());
		}
	}

	@Test
	public void testStringToLayer__testSortedRecipe() throws IllegalAccessException, InvocationTargetException {
		if (toLayers == null) fail();

		String line = "icing,butter;sugar";
		@SuppressWarnings("unchecked")
		List<Layer> layers = (List<Layer>)toLayers.invoke(null, line);
		assertTrue(layers.size() > 0);
		Layer layer = layers.get(0);
		assertEquals("icing", layer.toString());

		
		List<Ingredient> recipe = layer.getRecipe();
		assertEquals(2, recipe.size());
		assertEquals("butter", recipe.get(0).toString());
		assertEquals("sugar", recipe.get(1).toString());

		for (int i = 1; i < layers.size(); i++) {
			assertEquals(layer.toString(), layers.get(i).toString());
			assertEquals(layer.getRecipeDescription(), layers.get(i).getRecipeDescription());
		}
	}

	@Test	
	public void testStringToLayer__ManyIngredients__MultiWord() throws IllegalAccessException, InvocationTargetException {
		if (toLayers == null) fail();

		String line = "another icing,icing sugar;multi-coloured sprinkles;orange jest;tesco finest unsalted butter;vanilla extract";
		@SuppressWarnings("unchecked")
		List<Layer> layers = (List<Layer>)toLayers.invoke(null, line);
		assertTrue(layers.size() > 0);
		Layer layer = layers.get(0);
		assertEquals("another icing", layer.toString());
		
		List<Ingredient> recipe = layer.getRecipe();
		assertEquals(5, recipe.size());
		assertEquals("icing sugar", recipe.get(0).toString());
		assertEquals("multi-coloured sprinkles", recipe.get(1).toString());
		assertEquals("orange jest", recipe.get(2).toString());
		assertEquals("tesco finest unsalted butter", recipe.get(3).toString());
		assertEquals("vanilla extract", recipe.get(4).toString());
		
		for (int i = 1; i < layers.size(); i++) {
			assertEquals(layer.toString(), layers.get(i).toString());
			assertEquals(layer.getRecipeDescription(), layers.get(i).getRecipeDescription());
		}
	}

	@Test
	public void testStringToLayer__ReduntantSpacing() throws IllegalAccessException, InvocationTargetException {
		if (toLayers == null) fail();

		String line = " icing,  butter   ;sugar ";
		@SuppressWarnings("unchecked")
		List<Layer> layers = (List<Layer>)toLayers.invoke(null, line);
		assertTrue(layers.size() > 0);
		Layer layer = layers.get(0);
		assertEquals("icing", layer.toString());

		
		List<Ingredient> recipe = layer.getRecipe();
		assertEquals(2, recipe.size());
		assertEquals("butter", recipe.get(0).toString());
		assertEquals("sugar", recipe.get(1).toString());

		for (int i = 1; i < layers.size(); i++) {
			assertEquals(layer.toString(), layers.get(i).toString());
			assertEquals(layer.getRecipeDescription(), layers.get(i).getRecipeDescription());
		}
	}

	// Ingredients
	@Test
	public void testStringToIngredients__ZeroQuantity() throws IllegalAccessException, InvocationTargetException {
		if (toIngredients == null) fail();

		String line = "butter,0";
		@SuppressWarnings("unchecked")
		List<Ingredient> ingredients = (List<Ingredient>)toIngredients.invoke(null, line);
		assertEquals(0, ingredients.size());
	}

	@Test
	public void testStringToIngredients__OneQuantity() throws IllegalAccessException, InvocationTargetException {
		if (toIngredients == null) fail();

		String line = "buttermilk,1";
		@SuppressWarnings("unchecked")
		List<Ingredient> ingredients = (List<Ingredient>)toIngredients.invoke(null, line);
		assertEquals(1, ingredients.size());
		Ingredient ingredient = ingredients.get(0);
		assertEquals("buttermilk", ingredient.toString());
	}

	@Test
	public void testStringToIngredients__ManyQuantity() throws IllegalAccessException, InvocationTargetException {
		if (toIngredients == null) fail();

		String line = "milk,1000";
		@SuppressWarnings("unchecked")
		List<Ingredient> ingredients = (List<Ingredient>)toIngredients.invoke(null, line);
		assertEquals(1000, ingredients.size());
		for (Ingredient ingredient: ingredients) {
			assertEquals("milk", ingredient.toString());
		}
	}

	@Test
	public void testStringToIngredients__MultiWord() throws IllegalAccessException, InvocationTargetException {
		if (toIngredients == null) fail();

		String line = "Sainsbury's taste the difference sour cream,5";
		@SuppressWarnings("unchecked")
		List<Ingredient> ingredients = (List<Ingredient>)toIngredients.invoke(null, line);
		assertEquals(5, ingredients.size());
		for (Ingredient ingredient: ingredients) {
			assertEquals("Sainsbury's taste the difference sour cream", ingredient.toString());
		}
	}

	@Test
	public void testStringToIngredients__RedundantSpaces() throws IllegalAccessException, InvocationTargetException {
		if (toIngredients == null) fail();

		String line = " flour   ,3 ";
		@SuppressWarnings("unchecked")
		List<Ingredient> ingredients = (List<Ingredient>)toIngredients.invoke(null, line);
		assertEquals(3, ingredients.size());
		for (Ingredient ingredient: ingredients) {
			assertEquals("flour", ingredient.toString());
		}
	}

	// Customer Order File
	@Test
	public void testReadCustomerFile__ManyOrders() throws IllegalAccessException, InvocationTargetException, IOException {
		String txt = """
				LEVEL, NAME, RECIPE, GARNISH
				1, bombe, biscuit; butter; chocolate,
				2, scones, chocolate; layerA, fruit
				1, crumpets, butter; eggs; layerB; flour, jam
				""";

		File tmp = File.createTempFile("bakery_orders", ".tmp");
		Files.writeString(tmp.toPath(), txt);
		tmp.deleteOnExit();

		List<CustomerOrder> orders = CardUtils.readCustomerFile(tmp.toString(), layers);
		assertEquals(3, orders.size());
		
		assertEquals(1, orders.get(0).getLevel());
		assertEquals("bombe", orders.get(0).toString());
		assertEquals("biscuit, butter, chocolate", orders.get(0).getRecipeDescription());
		assertEquals("", orders.get(0).getGarnishDescription());

		assertEquals(2, orders.get(1).getLevel());
		assertEquals("scones", orders.get(1).toString());
		assertEquals("chocolate, layerA", orders.get(1).getRecipeDescription());
		assertEquals("fruit", orders.get(1).getGarnishDescription());

		assertEquals(1, orders.get(2).getLevel());
		assertEquals("crumpets", orders.get(2).toString());
		assertEquals("butter, eggs, layerB, flour", orders.get(2).getRecipeDescription());
		assertEquals("jam", orders.get(2).getGarnishDescription());
	}

	@Test
	public void testReadCustomerFile__OneOrder() throws IllegalAccessException, InvocationTargetException, IOException {
		String txt = """
				LEVEL, NAME, RECIPE, GARNISH
				1, bombe, biscuit; butter; chocolate,
				""";

		File tmp = File.createTempFile("bakery_orders_", ".tmp");
		Files.writeString(tmp.toPath(), txt);
		tmp.deleteOnExit();

		List<CustomerOrder> orders = CardUtils.readCustomerFile(tmp.toString(), layers);
		assertEquals(1, orders.size());
		
		assertEquals(1, orders.get(0).getLevel());
		assertEquals("bombe", orders.get(0).toString());
		assertEquals("biscuit, butter, chocolate", orders.get(0).getRecipeDescription());
		assertEquals("", orders.get(0).getGarnishDescription());
	}

	@Test
	public void testReadCustomerFile__OnlyHeader() throws IllegalAccessException, InvocationTargetException, IOException {
		String txt = """
				LEVEL, NAME, RECIPE, GARNISH
				""";

		File tmp = File.createTempFile("bakery_orders_", ".tmp");
		Files.writeString(tmp.toPath(), txt);
		tmp.deleteOnExit();

		List<CustomerOrder> orders = CardUtils.readCustomerFile(tmp.toString(), layers);
		assertEquals(0, orders.size());
	}

	@Test
	public void testReadCustomerFile__Empty() throws IllegalAccessException, InvocationTargetException, IOException {
		String txt = "";

		File tmp = File.createTempFile("bakery_orders_", ".tmp");
		Files.writeString(tmp.toPath(), txt);
		tmp.deleteOnExit();

		List<CustomerOrder> orders = CardUtils.readCustomerFile(tmp.toString(), layers);
		assertEquals(0, orders.size());
	}

	// Layers File
	@Test
	public void testReadLayerFile__ManyLayers() throws IllegalAccessException, InvocationTargetException, IOException {
		String txt = """
			NAME, RECIPE
			biscuit, eggs; flour; sugar
			crème pât, butter; eggs; sugar
			icing, butter; sugar
			""";

		File tmp = File.createTempFile("bakery_layers_", ".tmp");
		Files.writeString(tmp.toPath(), txt);
		tmp.deleteOnExit();

		List<Layer> layers = CardUtils.readLayerFile(tmp.toString());
		assertEquals(12, layers.size());
		
		assertEquals("biscuit", layers.get(0).toString());
		assertEquals("eggs, flour, sugar", layers.get(0).getRecipeDescription());

		assertEquals("crème pât", layers.get(4).toString());
		assertEquals("butter, eggs, sugar", layers.get(4).getRecipeDescription());

		assertEquals("icing", layers.get(8).toString());
		assertEquals("butter, sugar", layers.get(8).getRecipeDescription());
	}

	@Test
	public void testReadLayerFile__OneLayer() throws IllegalAccessException, InvocationTargetException, IOException {
		String txt = """
			NAME, RECIPE
			biscuit, eggs; flour; sugar
			""";

		File tmp = File.createTempFile("bakery_layers_", ".tmp");
		Files.writeString(tmp.toPath(), txt);
		tmp.deleteOnExit();

		List<Layer> layers = CardUtils.readLayerFile(tmp.toString());
		assertEquals(4, layers.size());
		
		assertEquals("biscuit", layers.get(0).toString());
		assertEquals("eggs, flour, sugar", layers.get(0).getRecipeDescription());
	}

	@Test
	public void testReadLayerFile__OnlyHeader() throws IllegalAccessException, InvocationTargetException, IOException {
		String txt = """
			NAME, RECIPE
			""";

		File tmp = File.createTempFile("bakery_layers_", ".tmp");
		Files.writeString(tmp.toPath(), txt);
		tmp.deleteOnExit();

		List<Layer> layers = CardUtils.readLayerFile(tmp.toString());
		assertEquals(0, layers.size());
	}

	@Test
	public void testReadLayerFile__Empty() throws IllegalAccessException, InvocationTargetException, IOException {
		String txt = "";

		File tmp = File.createTempFile("bakery_layers_", ".tmp");
		Files.writeString(tmp.toPath(), txt);
		tmp.deleteOnExit();

		List<Layer> layers = CardUtils.readLayerFile(tmp.toString());
		assertEquals(0, layers.size());
	}

	// Ingredients File
	@Test
	public void testReadIngredientFile__ManyIngredients() throws IllegalAccessException, InvocationTargetException, IOException {
		String txt = """
			NAME, COUNT
			fruit, 1
			chocolate, 2
			flour, 3
			detergent, 0
			""";

		File tmp = File.createTempFile("bakery_ingredients_", ".tmp");
		Files.writeString(tmp.toPath(), txt);
		tmp.deleteOnExit();

		List<Ingredient> ingredients = CardUtils.readIngredientFile(tmp.toString());
		assertEquals(6, ingredients.size());

		int fruit_count = 0;
		int chocolate_count = 0;
		int flour_count = 0;
		int detergent_count = 0;

		for (Ingredient ingredient: ingredients) {
			if (ingredient.toString().equals("fruit")) {
				fruit_count++;
			} else if (ingredient.toString().equals("chocolate")) {
				chocolate_count++;
			} else if (ingredient.toString().equals("flour")) {
				flour_count++;
			} else if (ingredient.toString().equals("detergent")) {
				detergent_count++;
			} else {
				fail();
			}
		}
		assertEquals(1, fruit_count);
		assertEquals(2, chocolate_count);
		assertEquals(3, flour_count);
		assertEquals(0, detergent_count);
	}

	@Test
	public void testReadIngredientFile__OneIngredient() throws IllegalAccessException, InvocationTargetException, IOException {
		String txt = """
			NAME, COUNT
			fruit, 3
			""";

		File tmp = File.createTempFile("bakery_ingredients_", ".tmp");
		Files.writeString(tmp.toPath(), txt);
		tmp.deleteOnExit();

		List<Ingredient> ingredients = CardUtils.readIngredientFile(tmp.toString());
		assertEquals(3, ingredients.size());

		for (Ingredient ingredient: ingredients) {
			assertEquals("fruit", ingredient.toString());
		}
	}

	@Test
	public void testReadIngredientFile__OnlyHeader() throws IllegalAccessException, InvocationTargetException, IOException {
		String txt = """
			NAME, COUNT
			""";

		File tmp = File.createTempFile("bakery_ingredients_", ".tmp");
		Files.writeString(tmp.toPath(), txt);
		tmp.deleteOnExit();

		List<Ingredient> ingredients = CardUtils.readIngredientFile(tmp.toString());
		assertEquals(0, ingredients.size());
	}

	@Test
	public void testReadIngredientFile__Empty() throws IllegalAccessException, InvocationTargetException, IOException {
		String txt = """
			NAME, COUNT
			""";

		File tmp = File.createTempFile("bakery_ingredients_", ".tmp");
		Files.writeString(tmp.toPath(), txt);
		tmp.deleteOnExit();

		List<Ingredient> ingredients = CardUtils.readIngredientFile(tmp.toString());
		assertEquals(0, ingredients.size());
	}

}
