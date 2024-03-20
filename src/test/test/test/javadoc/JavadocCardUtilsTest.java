package test.javadoc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("javadoc")
@Tag("CardUtils")
public class JavadocCardUtilsTest {

    String FQCN = "util.CardUtils";

    @Test
    public void testCardUtilsIsDocumented() {
        assertTrue(JavadocHelper._testclassIsDocumented("util.CardUtils"));
    }

    @Test
    public void testAllMembersDocumented() {
        assertTrue(JavadocHelper.allMembersAreDocumented(FQCN));
    }

    // @Test
    // public void testConstructorDocumented() {
    //     assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "CardUtils\\s*\\(.*"));
    // }

    @Test
    public void testReadIngredientFileDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "List\\s*<\\s*Ingredient\\s*>\\s*readIngredientFile\\s*\\(.*"));
    }

    @Test
    public void testReadLayerFileDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "List\\s*<\\s*Layer\\s*>\\s*readLayerFile\\s*\\(.*"));
    }

    @Test
    public void testReadCustomerFileDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "List\\s*<\\s*CustomerOrder\\s*>\\s*readCustomerFile\\s*\\(.*"));
    }

}
