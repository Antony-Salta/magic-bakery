package test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import bakery.Player;
import bakery.Ingredient;

@Tag("functional")
@Tag("Player")
public class PlayerTest {
	static Ingredient flour, butter, sugar, chocolate;

	@BeforeAll
	public static void setUp() {
		flour = new Ingredient("flour");
		butter = new Ingredient("butter");
		sugar = new Ingredient("sugar");
		chocolate = new Ingredient("chocolate");
	}

	@Test
	public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");
		assertEquals("A", FunctionalHelper.getFieldValue(player, "name"));

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");
		assertTrue(hand.isEmpty());
	}

	@Test
	public void testAddToHandMultiple() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> toAdd = new ArrayList<Ingredient>();
		toAdd.add(flour);
		toAdd.add(butter);
		toAdd.add(sugar);
		toAdd.add(chocolate);

		Player player = new Player("A");
		player.addToHand(toAdd);

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");
		assertEquals(4, hand.size());

		assertTrue(hand.containsAll(toAdd));
		assertTrue(toAdd.containsAll(hand));
	}

	@Test
	public void testAddToHandMultiple__Empty() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");
		player.addToHand(new ArrayList<Ingredient>());

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");
		assertTrue(hand.isEmpty());
	}

	@Test
	public void testAddToHandMultiple__SingleIngredient() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> toAdd = new ArrayList<Ingredient>();
		toAdd.add(flour);

		Player player = new Player("A");
		player.addToHand(toAdd);

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");
		assertEquals(1, hand.size());
		assertEquals(toAdd.get(0), hand.get(0));
	}

	@Test
	public void testAddToHandMultiple__DuplicateIngredient() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> toAdd = new ArrayList<Ingredient>();
		toAdd.add(flour);
		toAdd.add(butter);
		toAdd.add(chocolate);
		toAdd.add(new Ingredient("flour"));
		toAdd.add(new Ingredient("butter"));

		Player player = new Player("A");
		player.addToHand(toAdd);

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");
		assertEquals(5, hand.size());

		assertTrue(hand.containsAll(toAdd));
		assertTrue(toAdd.containsAll(hand));

		int count_flour = 0;
		int count_butter = 0;
		int count_chocolate = 0;
		for (Ingredient ing: hand) {
			String name = (String)FunctionalHelper.getFieldValue(ing, "name");
			if (name == "flour") {
				count_flour++;
			}
			if (name == "butter") {
				count_butter++;
			}
			if (name == "chocolate") {
				count_chocolate++;
			}
		}
		assertEquals(2, count_flour);
		assertEquals(2, count_butter);
		assertEquals(1, count_chocolate);
	}

	@Test
	public void testAddToHandSingle() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");
		player.addToHand(flour);

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");
		assertEquals(1, hand.size());
		assertEquals(flour, hand.get(0));
	}

	@Test
	public void testAddToHandSingle__ToExistingHand() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");
		hand.add(butter);
		hand.add(sugar);

		player.addToHand(flour);
		assertEquals(3, hand.size());
		assertTrue(hand.contains(flour));
		assertTrue(hand.contains(butter));
		assertTrue(hand.contains(sugar));
	}

	@Test
	public void testAddToHandSingle__DuplicateCard() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");
		hand.add(butter);
		hand.add(sugar);
		hand.add(flour);

		player.addToHand(new Ingredient("sugar"));

		assertEquals(4, hand.size());
		assertTrue(hand.contains(flour));
		assertTrue(hand.contains(butter));
		assertTrue(hand.contains(sugar));
		
		int count_sugar = 0;
		for (Ingredient ing: hand) {
			String name = (String)FunctionalHelper.getFieldValue(ing, "name");
			if (name == "sugar") {
				count_sugar++;
			}
		}
		assertEquals(2, count_sugar);

	}

	@Test
	public void testHasIngredient() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");
		hand.add(butter);
		hand.add(sugar);
		hand.add(flour);

		assertTrue(player.hasIngredient(butter));
		assertTrue(player.hasIngredient(sugar));
		assertTrue(player.hasIngredient(flour));
		assertFalse(player.hasIngredient(chocolate));
	}

	@Test
	public void testHasIngredient__Empty() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");
		assertFalse(player.hasIngredient(butter));
		assertFalse(player.hasIngredient(sugar));
		assertFalse(player.hasIngredient(flour));
		assertFalse(player.hasIngredient(chocolate));
	}

	@Test
	public void testHasIngredient__DuplicateIngredients() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");
		hand.add(butter);
		hand.add(sugar);
		hand.add(flour);
		hand.add(new Ingredient("flour"));

		assertTrue(player.hasIngredient(butter));
		assertTrue(player.hasIngredient(sugar));
		assertTrue(player.hasIngredient(flour));
		assertFalse(player.hasIngredient(chocolate));
	}

	@Test
	public void testRemoveFromHand() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");

		hand.add(butter);
		hand.add(sugar);
		hand.add(flour);
		
		player.removeFromHand(butter);
		assertEquals(2, hand.size());
		assertFalse(hand.contains(butter));
		assertTrue(hand.contains(sugar));
		assertTrue(hand.contains(flour));
	}

	@Test
	public void testRemoveFromHand__WhenHandContainsIdenticalCards() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");

		hand.add(butter);
		hand.add(sugar);
		hand.add(flour);
		hand.add(flour);
		
		player.removeFromHand(flour);
		assertEquals(3, hand.size());
		assertTrue(hand.contains(butter));
		assertTrue(hand.contains(sugar));
		assertTrue(hand.contains(flour));
	}

	@Test
	public void testGetHand() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");
		assertEquals(hand, player.getHand());
		hand.add(butter);
		hand.add(sugar);
		hand.add(flour);
		hand.add(flour);
		assertEquals(hand, player.getHand());
	}

	@Test
	public void testGetHandStr() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");

		hand.add(butter);
		hand.add(chocolate);
		hand.add(sugar);
		hand.add(flour);
		hand.add(flour);

		assertEquals("Butter, Chocolate, Flour (x2), Sugar", player.getHandStr());
	}

	@Test
	public void testGetHandStr__Empty() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");
		assertEquals("", player.getHandStr());
	}

	@Test
	public void testGetHandStr__SingleCard() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");
		hand.add(sugar);

		assertEquals("Sugar", player.getHandStr());
	}

	@Test
	public void testGetHandStr__ThreeDuplicateCards() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");
		hand.add(sugar);
		hand.add(sugar);
		hand.add(sugar);

		assertEquals("Sugar (x3)", player.getHandStr());
	}

	@Test
	public void testGetHandStr__OnlyThreeDucks() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");
		hand.add(Ingredient.HELPFUL_DUCK);
		hand.add(Ingredient.HELPFUL_DUCK);
		hand.add(Ingredient.HELPFUL_DUCK);

		assertEquals("Helpful duck 𓅭 (x3)", player.getHandStr());
	}

	@Test
	public void testGetHandStr__ContainsOneDuck() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");

		hand.add(butter);
		hand.add(chocolate);
		hand.add(sugar);
		hand.add(flour);
		hand.add(flour);
		hand.add(Ingredient.HELPFUL_DUCK);

		assertEquals("Butter, Chocolate, Flour (x2), Helpful duck 𓅭, Sugar", player.getHandStr());
	}

	@Test
	public void testGetHandStr__ContainsTwoDucks() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");

		@SuppressWarnings("unchecked")
		List<Ingredient> hand = (List<Ingredient>)FunctionalHelper.getFieldValue(player, "hand");

		hand.add(butter);
		hand.add(chocolate);
		hand.add(sugar);
		hand.add(flour);
		hand.add(flour);
		hand.add(Ingredient.HELPFUL_DUCK);
		hand.add(Ingredient.HELPFUL_DUCK);

		assertEquals("Butter, Chocolate, Flour (x2), Helpful duck 𓅭 (x2), Sugar", player.getHandStr());
	}

	@Test
	public void testToString() throws NoSuchFieldException, IllegalAccessException {
		Player player = new Player("A");
		assertEquals("A", player.toString());
		
		player = new Player("");
		assertEquals("", player.toString());

		assertEquals(player.toString(), FunctionalHelper.getFieldValue(player, "name"));
	}

}
