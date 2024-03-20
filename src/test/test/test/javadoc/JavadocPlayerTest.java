package test.javadoc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("javadoc")
@Tag("Player")
public class JavadocPlayerTest {

    String FQCN = "bakery.Player";

    @Test
    public void testPlayerIsDocumented() {
        assertTrue(JavadocHelper._testclassIsDocumented("bakery.Player"));
    }

    @Test
    public void testAllMembersDocumented() {
        assertTrue(JavadocHelper.allMembersAreDocumented(FQCN));
    }

    @Test
    public void testConstructorDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Player\\s*\\(.*"));
    }

    @Test
    public void testToStringDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "String\\s*toString\\s*\\(.*"));
    }

    @Test
    public void testHasIngredientDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "boolean\\s*hasIngredient\\s*\\(.*"));
    }

    @Test
    public void testGetHandDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "List\\s*<\\s*Ingredient\\s*>\\s*getHand\\s*\\(.*"));
    }

    @Test
    public void testRemoveFromHandDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*removeFromHand\\s*\\(.*"));
    }

    @Test
    public void testGetHandStrDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "String\\s*getHandStr\\s*\\(.*"));
    }

    @Test
    public void testAddToHandTakesListAndReturnsvoidDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*addToHand\\s*\\(.*"));
    }

    @Test
    public void testAddToHandTakesIngredientAndReturnsvoidDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*addToHand\\s*\\(.*"));
    }

}
