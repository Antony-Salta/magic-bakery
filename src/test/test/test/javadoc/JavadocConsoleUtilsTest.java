package test.javadoc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


@Tag("javadoc")
@Tag("ConsoleUtils")
public class JavadocConsoleUtilsTest {

    String FQCN = "util.ConsoleUtils";

    @Test
    public void testConsoleUtilsIsDocumented() {
        assertTrue(JavadocHelper._testclassIsDocumented("util.ConsoleUtils"));
    }

    @Test
    public void testAllMembersDocumented() {
        assertTrue(JavadocHelper.allMembersAreDocumented(FQCN));
    }

    @Test
    public void testConstructorDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "ConsoleUtils\\s*\\(.*"));
    }

    @Test
    public void testReadLineTakesStringAndObjectArrayAndReturnsStringDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "String\\s*readLine\\s*\\(.*"));
    }

    @Test
    public void testReadLineTakesNothingAndReturnsStringDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "String\\s*readLine\\s*\\(.*"));
    }

    @Test
    public void testPromptForYesNoDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "boolean\\s*promptForYesNo\\s*\\(.*"));
    }

    @Test
    public void testPromptForActionDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "ActionType\\s*promptForAction\\s*\\(.*"));
    }

    @Test
    public void testPromptForCustomerDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "CustomerOrder\\s*promptForCustomer\\s*\\(.*"));
    }

    @Test
    public void testPromptForExistingPlayerDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Player\\s*promptForExistingPlayer\\s*\\(.*"));
    }

    @Test
    public void testPromptForFilePathDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "File\\s*promptForFilePath\\s*\\(.*"));
    }

    @Test
    public void testPromptForIngredientDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Ingredient\\s*promptForIngredient\\s*\\(.*"));
    }

    @Test
    public void testPromptForNewPlayersDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "List\\s*<\\s*String\\s*>\\s*promptForNewPlayers\\s*\\(.*"));
    }

    @Test
    public void testPromptForStartLoadDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "boolean\\s*promptForStartLoad\\s*\\(.*"));
    }

}
