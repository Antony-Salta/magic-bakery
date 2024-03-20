package test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import bakery.Layer;
import bakery.Ingredient;

@Tag("functional")
@Tag("Layer")
public class LayerTest {

	static ArrayList<Ingredient> recipe_multi;
	static ArrayList<Ingredient> recipe_single;

	static Layer layer_multi;
	static Layer layer_single;

	static ArrayList<Ingredient> pantry;
	static ArrayList<Ingredient> pantryWithOneDuck;
	static ArrayList<Ingredient> pantryWithTwoDucks;

	@BeforeAll
	public static void setUp() {
		recipe_multi = new ArrayList<Ingredient>();
		recipe_multi.add(new Ingredient("butter"));
		recipe_multi.add(new Ingredient("flour"));
		recipe_multi.add(new Ingredient("sugar"));
		

		recipe_single = new ArrayList<Ingredient>();
		recipe_single.add(new Ingredient("ready-made sponge"));

		layer_multi = new Layer("sponge", recipe_multi);
		layer_single = new Layer("sponge2", recipe_single);

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

	@Test
	public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
		@SuppressWarnings("unchecked")
		List<Ingredient> recipe = (List<Ingredient>)FunctionalHelper.getFieldValue(layer_multi, "recipe");

		ArrayList<String> ingredients = new ArrayList<String>();
		for (Ingredient ing: recipe) {
			ingredients.add(ing.toString());
		}
		assertTrue(ingredients.contains("butter"));
		assertTrue(ingredients.contains("flour"));
		assertTrue(ingredients.contains("sugar"));
	}

	@Test
	public void testGetRecipe() throws NoSuchFieldException, IllegalAccessException {
		@SuppressWarnings("unchecked")
		List<Ingredient> recipe = (List<Ingredient>)FunctionalHelper.getFieldValue(layer_multi, "recipe");
		List<Ingredient> recipe_too = layer_multi.getRecipe();
		assertTrue(recipe.containsAll(recipe_too));
		assertTrue(recipe_too.containsAll(recipe));
	}

	@Test
	public void testGetRecipeDescription__SingleIngredient() throws NoSuchFieldException, IllegalAccessException {
		assertEquals("ready-made sponge", layer_single.getRecipeDescription());
	}

	@Test
	public void testGetRecipeDescription__MultipleIngredients() throws NoSuchFieldException, IllegalAccessException {
		assertEquals("butter, flour, sugar", layer_multi.getRecipeDescription());
	}

	@Test
	public void testHashCode__Same() throws NoSuchFieldException, IllegalAccessException {
		Layer layer2 = new Layer("sponge", recipe_multi);
		assertEquals(layer_multi.hashCode(), layer2.hashCode());
	}

	@Test
	public void testHashCode__SameRecipeDifferentIngredientOrder() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> ingredients2 = new ArrayList<Ingredient>();
		ingredients2.add(recipe_multi.get(1));
		ingredients2.add(recipe_multi.get(2));
		ingredients2.add(recipe_multi.get(0));

		Layer layer2 = new Layer("sponge", ingredients2);
		assertEquals(layer_multi.hashCode(), layer2.hashCode());
	}

	@Test
	public void testCanBake() throws NoSuchFieldException, IllegalAccessException {

		assertTrue(layer_multi.canBake(pantry));
		assertFalse(layer_single.canBake(pantry));
	}

	@Test
	public void testCanBake__DoubleIngredient() throws NoSuchFieldException, IllegalAccessException {
		// Does order_multi still can be baked if it needs two butter cards?
		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>(recipe_multi);
		recipe.add(new Ingredient("butter"));
		Layer layer = new Layer("some recipe", recipe);
		assertFalse(layer.canBake(pantry));
	}

	// Let's check what happens if we have a Helpful Duck
	@Test
	public void testCanBake_WithOneDuck() throws NoSuchFieldException, IllegalAccessException {
		assertTrue(layer_multi.canBake(pantryWithOneDuck));
		assertTrue(layer_single.canBake(pantryWithOneDuck));
	}

	@Test
	public void testCanBake__DoubleIngredient_WithOneDuck() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>(recipe_multi);
		recipe.add(new Ingredient("butter"));
		Layer layer = new Layer("some recipe", recipe);
		assertTrue(layer.canBake(pantryWithOneDuck));
	}

	@Test
	public void testCanBake__ExtraIngredients_WithOneDuck() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>(recipe_multi);
		recipe.add(new Ingredient("butter"));
		recipe.add(new Ingredient("sour cream"));
		Layer layer = new Layer("some recipe", recipe);
		assertFalse(layer.canBake(pantryWithOneDuck));
	}

	// What if we have two Helpful Ducks
	@Test
	public void testCanBake_WithTwoDucks() throws NoSuchFieldException, IllegalAccessException {
		assertTrue(layer_multi.canBake(pantryWithTwoDucks));
		assertTrue(layer_single.canBake(pantryWithTwoDucks));
	}

	@Test
	public void testCanBake__DoubleIngredient_WithTwoDucks() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>(recipe_multi);
		recipe.add(new Ingredient("butter"));
		Layer layer = new Layer("some recipe", recipe);
		assertTrue(layer.canBake(pantryWithTwoDucks));
	}

	@Test
	public void testCanBake__ExtraIngredients_WithTwoDucks() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>(recipe_multi);
		recipe.add(new Ingredient("butter"));
		recipe.add(new Ingredient("sour cream"));
		Layer layer = new Layer("some recipe", recipe);
		assertTrue(layer.canBake(pantryWithTwoDucks));
	}

	@Test
	public void testCanBake__InfiniteIngredients_WithTwoDucks() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> recipe = new ArrayList<Ingredient>(recipe_multi);
		recipe.add(new Ingredient("butter"));
		recipe.add(new Ingredient("sour cream"));
		recipe.add(new Ingredient("tortillas"));
		recipe.add(new Ingredient("yeast"));
		recipe.add(new Ingredient("maple syrup"));
		recipe.add(new Ingredient("tahini"));
		recipe.add(new Ingredient("glucose"));
		Layer layer = new Layer("some recipe", recipe);
		assertFalse(layer.canBake(pantryWithTwoDucks));
	}
}
