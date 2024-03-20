package test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import bakery.Ingredient;

@Tag("functional")
@Tag("Ingredient")
public class IngredientTest {

	static String name_first;
	static String name_middle;
	static String name_last;

	static Ingredient first;
	static Ingredient middle;
	static Ingredient middle_too;
	static Ingredient last;

	@BeforeAll
	public static void setUp() {
		name_first = "First";
		first = new Ingredient(name_first);

		name_middle = "Middle";
		middle = new Ingredient(name_middle);
		middle_too = new Ingredient(name_middle);

		name_last = "X";
		last = new Ingredient(name_last);
	}

	@Test
	public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
		assertEquals(name_first, FunctionalHelper.getFieldValue(first, "name"));
		assertEquals(name_middle, FunctionalHelper.getFieldValue(middle, "name"));
		assertEquals(name_middle, FunctionalHelper.getFieldValue(middle_too, "name"));
		assertEquals(name_last, FunctionalHelper.getFieldValue(last, "name"));
	}

	@Test
	public void testComparable() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
		ingredients.add(last);
		ingredients.add(middle);
		ingredients.add(first);
		ingredients.add(middle_too);

		Collections.sort(ingredients);

		assertEquals(first, ingredients.get(0));
		assertEquals(name_middle, FunctionalHelper.getFieldValue(ingredients.get(1), "name"));
		assertEquals(name_middle, FunctionalHelper.getFieldValue(ingredients.get(2), "name"));
		assertEquals(last, ingredients.get(3));
	}

	@Test
	public void testCompareTo__Equal() throws NoSuchFieldException, IllegalAccessException {
		assertEquals(0, middle.compareTo(middle_too));
	}

	@Test
	public void testCompareTo__Greater() throws NoSuchFieldException, IllegalAccessException {
		assertTrue(middle.compareTo(last) < 0);
		assertTrue(first.compareTo(middle) < 0);
		assertTrue(first.compareTo(last) < 0);
	}

	@Test
	public void testCompareTo__Less() throws NoSuchFieldException, IllegalAccessException {
		assertTrue(last.compareTo(middle) > 0);
		assertTrue(last.compareTo(first) > 0);
		assertTrue(middle.compareTo(first) > 0);
	}

	@Test
	public void testEquals__Null() throws NoSuchFieldException, IllegalAccessException {
		assertFalse(first.equals(null));
		assertFalse(middle.equals(null));
		assertFalse(last.equals(null));
	}

	@Test
	public void testEquals__Same() throws NoSuchFieldException, IllegalAccessException {
		assertTrue(first.equals(first));
		assertTrue(middle.equals(middle));
		assertTrue(last.equals(last));
	}

	@Test
	public void testEquals__DifferentNames() throws NoSuchFieldException, IllegalAccessException {
		assertFalse(first.equals(middle));
		assertFalse(middle.equals(last));
		assertFalse(last.equals(first));
	}

	@Test
	public void testEquals__SameNames() throws NoSuchFieldException, IllegalAccessException {
		assertTrue(first.equals(new Ingredient(name_first)));
		assertTrue(middle.equals(middle_too));
		assertTrue(last.equals(new Ingredient(name_last)));
	}

	@Test
	public void testHashCode_Same() throws NoSuchFieldException, IllegalAccessException {
		assertEquals(first.hashCode(), first.hashCode());
		assertEquals(middle.hashCode(), middle_too.hashCode());
		assertEquals(last.hashCode(), last.hashCode());
	}

	@Test
	public void testToString() throws NoSuchFieldException, IllegalAccessException {
		assertEquals(name_first, first.toString());
		assertEquals(name_middle, middle.toString());
		assertEquals(name_last, last.toString());
	}
}
