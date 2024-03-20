package test.javadoc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("javadoc")
@Tag("MagicBakery")
public class JavadocMagicBakeryTest {

    String FQCN = "bakery.MagicBakery";

    @Test
    public void testMagicBakeryIsDocumented() {
        assertTrue(JavadocHelper._testclassIsDocumented("bakery.MagicBakery"));
    }

    @Test
    public void testActionTypeIsDocumented() {
        assertTrue(JavadocHelper._testclassIsDocumented("bakery.MagicBakery$ActionType", "bakery.MagicBakery"));
    }

    @Test
    public void testAllMembersDocumented() {
        assertTrue(JavadocHelper.allMembersAreDocumented(FQCN));
    }

    @Test
    public void testConstructorDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "MagicBakery\\s*\\(.*"));
    }

    @Test
    public void testGetActionsRemainingDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "int\\s*getActionsRemaining\\s*\\(.*"));
    }

    @Test
    public void testGetCurrentPlayerDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Player\\s*getCurrentPlayer\\s*\\(.*"));
    }

    @Test
    public void testDrawFromPantryTakesStringAndReturnsvoidDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*drawFromPantry\\s*\\(.*"));
    }

    @Test
    public void testDrawFromPantryTakesIngredientAndReturnsvoidDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*drawFromPantry\\s*\\(.*"));
    }

    @Test
    public void testGetCustomersDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Customers\\s*getCustomers\\s*\\(.*"));
    }

    @Test
    public void testGetActionsPermittedDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "int\\s*getActionsPermitted\\s*\\(.*"));
    }

    @Test
    public void testGetLayersDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Collection\\s*<\\s*Layer\\s*>\\s*getLayers\\s*\\(.*"));
    }

    @Test
    public void testGetFulfilableCustomersDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Collection\\s*<\\s*CustomerOrder\\s*>\\s*getFulfil(l)?ableCustomers\\s*\\(.*"));
    }

    @Test
    public void testGetPantryDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Collection\\s*<\\s*Ingredient\\s*>\\s*getPantry\\s*\\(.*"));
    }

    @Test
    public void testPrintCustomerServiceRecordDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*printCustomerServiceRecord\\s*\\(.*"));
    }

    @Test
    public void testBakeLayerDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*bakeLayer\\s*\\(.*"));
    }

    @Test
    public void testEndTurnDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "boolean\\s*endTurn\\s*\\(.*"));
    }

    @Test
    public void testFulfillOrderDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "List\\s*<\\s*Ingredient\\s*>\\s*fulfillOrder\\s*\\(.*"));
    }

    @Test
    public void testGetBakeableLayersDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Collection\\s*<\\s*Layer\\s*>\\s*getBakeableLayers\\s*\\(.*"));
    }

    @Test
    public void testGetGarnishableCustomersDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Collection\\s*<\\s*CustomerOrder\\s*>\\s*getGarnishableCustomers\\s*\\(.*"));
    }

    @Test
    public void testGetPlayersDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Collection\\s*<\\s*Player\\s*>\\s*getPlayers\\s*\\(.*"));
    }

    @Test
    public void testLoadStateDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "MagicBakery\\s*loadState\\s*\\(.*"));
    }

    @Test
    public void testPassCardDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*passCard\\s*\\(.*"));
    }

    @Test
    public void testPrintGameStateDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*printGameState\\s*\\(.*"));
    }

    @Test
    public void testRefreshPantryDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*refreshPantry\\s*\\(.*"));
    }

    @Test
    public void testSaveStateDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*saveState\\s*\\(.*"));
    }

    @Test
    public void testStartGameDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*startGame\\s*\\(.*"));
    }

}
