package test.javadoc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


@Tag("javadoc")
@Tag("Layer")
public class JavadocLayerTest {

    String FQCN = "bakery.Layer";

    @Test
    public void testLayerIsDocumented() {
        assertTrue(JavadocHelper._testclassIsDocumented("bakery.Layer"));
    }

    @Test
    public void testAllMembersDocumented() {
        assertTrue(JavadocHelper.allMembersAreDocumented(FQCN));
    }

    @Test
    public void testConstructorDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Layer\\s*\\(.*"));
    }

    @Test
    public void testHashCodeDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "int\\s*hashCode\\s*\\(.*"));
    }

    @Test
    public void testGetRecipeDescriptionDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "String\\s*getRecipeDescription\\s*\\(.*"));
    }

    @Test
    public void testCanBakeDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "boolean\\s*canBake\\s*\\(.*"));
    }

    @Test
    public void testGetRecipeDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "List\\s*<\\s*Ingredient\\s*>\\s*getRecipe\\s*\\(.*"));
    }

}
