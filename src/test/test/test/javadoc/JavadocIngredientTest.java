package test.javadoc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("javadoc")
@Tag("Ingredient")
public class JavadocIngredientTest {

    String FQCN = "bakery.Ingredient";

    @Test
    public void testIngredientIsDocumented() {
        assertTrue(JavadocHelper._testclassIsDocumented("bakery.Ingredient"));
    }

    @Test
    public void testAllMembersDocumented() {
        assertTrue(JavadocHelper.allMembersAreDocumented(FQCN));
    }

    @Test
    public void testConstructorDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Ingredient\\s*\\(.*"));
    }

    @Test
    public void testEqualsDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "boolean\\s*equals\\s*\\(.*"));
    }

    @Test
    public void testToStringDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "String\\s*toString\\s*\\(.*"));
    }

    @Test
    public void testHashCodeDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "int\\s*hashCode\\s*\\(.*"));
    }

    @Test
    public void testCompareToTakesObjectAndReturnsintDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "int\\s*compareTo\\s*\\(.*"));
    }

    @Test
    public void testCompareToTakesIngredientAndReturnsintDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "int\\s*compareTo\\s*\\(.*"));
    }

    @Test
    public void testHelpfulDuckDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Ingredient\\s*HELPFUL_DUCK"));
    }

}
