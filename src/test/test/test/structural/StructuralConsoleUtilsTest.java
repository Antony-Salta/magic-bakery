package test.structural;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


@Tag("structural")
@Tag("ConsoleUtils")
public class StructuralConsoleUtilsTest {

    static String FQCN = "util.ConsoleUtils";
    static Field[] fields;
    static Method[] methods;

    @BeforeAll
    public static void setUp() {
        fields = StructuralHelper.getFields(FQCN);
        methods = StructuralHelper.getMethods(FQCN);
        // Fail early and cleanly if the class is effectively empty
        assertNotNull(fields, "The class is empty or it has not compiled successfully, I will not run structural tests on it");
        assertNotNull(methods, "The class is empty or it has not compiled successfully, I will not run structural tests on it");
    }

    @Test
    public void testClassExists() {
        assertTrue(StructuralHelper.classExists(FQCN));
    }

    @Test
    public void testClassIsPublic() {
        assertTrue(StructuralHelper.classIsPublic(FQCN));
    }

    @Test
    public void testEmptyConstructorIsPublic() throws ClassNotFoundException, NoSuchMethodException {
        assertTrue(StructuralHelper.ctorIsPublic(FQCN ));
    }

    @Test
    public void testConsoleExists() {
        assertTrue(StructuralHelper.fieldExists("console", fields));
    }

    @Test
    public void testConsoleIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("console", fields, AccessType.PRIVATE));
    }

    @Test
    public void testConsoleIsConsole() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("console", fields, Class.forName("java.io.Console")));
    }

    @Test
    public void testReadLineExists() {
        assertTrue(StructuralHelper.methodExists("readLine", methods));
    }

    @Test
    public void testPromptEnumerateCollectionExists() {
        assertTrue(StructuralHelper.methodExists("promptEnumerateCollection", methods));
    }

    @Test
    public void testPromptForYesNoExists() {
        assertTrue(StructuralHelper.methodExists("promptForYesNo", methods));
    }

    @Test
    public void testPromptForActionExists() {
        assertTrue(StructuralHelper.methodExists("promptForAction", methods));
    }

    @Test
    public void testPromptForCustomerExists() {
        assertTrue(StructuralHelper.methodExists("promptForCustomer", methods));
    }

    @Test
    public void testPromptForExistingPlayerExists() {
        assertTrue(StructuralHelper.methodExists("promptForExistingPlayer", methods));
    }

    @Test
    public void testPromptForFilePathExists() {
        assertTrue(StructuralHelper.methodExists("promptForFilePath", methods));
    }

    @Test
    public void testPromptForIngredientExists() {
        assertTrue(StructuralHelper.methodExists("promptForIngredient", methods));
    }

    @Test
    public void testPromptForNewPlayersExists() {
        assertTrue(StructuralHelper.methodExists("promptForNewPlayers", methods));
    }

    @Test
    public void testPromptForStartLoadExists() {
        assertTrue(StructuralHelper.methodExists("promptForStartLoad", methods));
    }

    @Test
    public void testReadLineIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("readLine", methods, AccessType.PUBLIC));
    }

    @Test
    public void testPromptEnumerateCollectionIsPrivate() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("promptEnumerateCollection", methods, AccessType.PRIVATE));
    }

    @Test
    public void testPromptForYesNoIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("promptForYesNo", methods, AccessType.PUBLIC));
    }

    @Test
    public void testPromptForActionIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("promptForAction", methods, AccessType.PUBLIC));
    }

    @Test
    public void testPromptForCustomerIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("promptForCustomer", methods, AccessType.PUBLIC));
    }

    @Test
    public void testPromptForExistingPlayerIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("promptForExistingPlayer", methods, AccessType.PUBLIC));
    }

    @Test
    public void testPromptForFilePathIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("promptForFilePath", methods, AccessType.PUBLIC));
    }

    @Test
    public void testPromptForIngredientIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("promptForIngredient", methods, AccessType.PUBLIC));
    }

    @Test
    public void testPromptForNewPlayersIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("promptForNewPlayers", methods, AccessType.PUBLIC));
    }

    @Test
    public void testPromptForStartLoadIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("promptForStartLoad", methods, AccessType.PUBLIC));
    }

    @Test
    public void testReadLineTakesStringAndObjectArrayReturnsString() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("readLine", methods, Class.forName("java.lang.String") , Class.forName("java.lang.String"), Class.forName("[Ljava.lang.Object;")));
    }

    @Test
    public void testReadLineTakesNothingReturnsString() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("readLine", methods, Class.forName("java.lang.String") ));
    }

    @Test
    public void testPromptEnumerateCollectionTakesStringAndCollectionReturnsObject() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("promptEnumerateCollection", methods, Class.forName("java.lang.Object") , Class.forName("java.lang.String"), Class.forName("java.util.Collection")));
    }

    @Test
    public void testPromptForYesNoTakesStringReturnsboolean() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("promptForYesNo", methods, boolean.class , Class.forName("java.lang.String")));
    }

    @Test
    public void testPromptForActionTakesStringAndMagicBakeryReturnsActionType() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("promptForAction", methods, Class.forName("bakery.MagicBakery$ActionType") , Class.forName("java.lang.String"), Class.forName("bakery.MagicBakery")));
    }

    @Test
    public void testPromptForCustomerTakesStringAndCollectionReturnsCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("promptForCustomer", methods, Class.forName("bakery.CustomerOrder") , Class.forName("java.lang.String"), Class.forName("java.util.Collection")));
    }

    @Test
    public void testPromptForExistingPlayerTakesStringAndMagicBakeryReturnsPlayer() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("promptForExistingPlayer", methods, Class.forName("bakery.Player") , Class.forName("java.lang.String"), Class.forName("bakery.MagicBakery")));
    }

    @Test
    public void testPromptForFilePathTakesStringReturnsFile() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("promptForFilePath", methods, Class.forName("java.io.File") , Class.forName("java.lang.String")));
    }

    @Test
    public void testPromptForIngredientTakesStringAndCollectionReturnsIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("promptForIngredient", methods, Class.forName("bakery.Ingredient") , Class.forName("java.lang.String"), Class.forName("java.util.Collection")));
    }

    @Test
    public void testPromptForNewPlayersTakesStringReturnsListOfString() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("promptForNewPlayers", methods, Class.forName("java.util.List"), Class.forName("java.lang.String") , Class.forName("java.lang.String")));
    }

    @Test
    public void testPromptForStartLoadTakesStringReturnsboolean() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("promptForStartLoad", methods, boolean.class , Class.forName("java.lang.String")));
    }

}
