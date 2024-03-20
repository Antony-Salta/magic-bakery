package test.javadoc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


@Tag("javadoc")
@Tag("CustomerOrder")
public class JavadocCustomerOrderTest {

    String FQCN = "bakery.CustomerOrder";

    @Test
    public void testCustomerOrderIsDocumented() {
        assertTrue(JavadocHelper._testclassIsDocumented("bakery.CustomerOrder"));
    }

    @Test
    public void testCustomerOrderStatusIsDocumented() {
        assertTrue(JavadocHelper._testclassIsDocumented("bakery.CustomerOrder$CustomerOrderStatus", "bakery.CustomerOrder"));
    }

    @Test
    public void testAllMembersDocumented() {
        assertTrue(JavadocHelper.allMembersAreDocumented(FQCN));
    }

    @Test
    public void testConstructorDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "CustomerOrder\\s*\\(.*"));
    }

    @Test
    public void testToStringDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "String\\s*toString\\s*\\(.*"));
    }

    @Test
    public void testGetLevelDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "int\\s*getLevel\\s*\\(.*"));
    }

    @Test
    public void testSetStatusDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*setStatus\\s*\\(.*"));
    }

    @Test
    public void testFulfillDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "List\\s*<\\s*Ingredient\\s*>\\s*fulfill\\s*\\(.*"));
    }

    @Test
    public void testGetGarnishDescriptionDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "String\\s*getGarnishDescription\\s*\\(.*"));
    }

    @Test
    public void testGetRecipeDescriptionDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "String\\s*getRecipeDescription\\s*\\(.*"));
    }

    @Test
    public void testGetRecipeDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "List\\s*<\\s*Ingredient\\s*>\\s*getRecipe\\s*\\(.*"));
    }

    @Test
    public void testGetGarnishDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "List\\s*<\\s*Ingredient\\s*>\\s*getGarnish\\s*\\(.*"));
    }

    @Test
    public void testCanGarnishDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "boolean\\s*canGarnish\\s*\\(.*"));
    }

    @Test
    public void testCanFulfillDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "boolean\\s*canFulfill\\s*\\(.*"));
    }

    @Test
    public void testAbandonDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*abandon\\s*\\(.*"));
    }

    @Test
    public void testGetStatusDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "CustomerOrderStatus\\s*getStatus\\s*\\(.*"));
    }

}
