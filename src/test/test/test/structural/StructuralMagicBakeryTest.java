package test.structural;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Tag("structural")
@Tag("MagicBakery")
public class StructuralMagicBakeryTest {

    static String FQCN = "bakery.MagicBakery";
    static Field[] fields;
    static Method[] methods;

    public String getMethodGetFulfilableCustomers() {
        if (StructuralHelper.methodExists("getFulfilableCustomers", methods)) {
            return "getFulfilableCustomers";
        }
        return "getFulfillableCustomers";
    }

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
    public void testClassImplementsSerializable() {
        assertTrue(StructuralHelper.classIsSerializable(FQCN));
    }

    @Test
    public void testParam3ConstructorIsPublic() throws ClassNotFoundException, NoSuchMethodException {
        assertTrue(StructuralHelper.ctorIsPublic(FQCN , long.class, Class.forName("java.lang.String"), Class.forName("java.lang.String")));
    }

    @Test
    public void testSerialVersionUIDExists() {
        assertTrue(StructuralHelper.classHasPublicStaticFinalSerialVersionUID(FQCN));
    }

    @Test
    public void testRandomExists() {
        assertTrue(StructuralHelper.fieldExists("random", fields));
    }

    @Test
    public void testPlayersExists() {
        assertTrue(StructuralHelper.fieldExists("players", fields));
    }

    @Test
    public void testPantryDeckExists() {
        assertTrue(StructuralHelper.fieldExists("pantryDeck", fields));
    }

    @Test
    public void testPantryDiscardExists() {
        assertTrue(StructuralHelper.fieldExists("pantryDiscard", fields));
    }

    @Test
    public void testPantryExists() {
        assertTrue(StructuralHelper.fieldExists("pantry", fields));
    }

    @Test
    public void testLayersExists() {
        assertTrue(StructuralHelper.fieldExists("layers", fields));
    }

    @Test
    public void testCustomersExists() {
        assertTrue(StructuralHelper.fieldExists("customers", fields));
    }

    @Test
    public void testRandomIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("random", fields, AccessType.PRIVATE));
    }

    @Test
    public void testPlayersIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("players", fields, AccessType.PRIVATE));
    }

    @Test
    public void testPantryDeckIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("pantryDeck", fields, AccessType.PRIVATE));
    }

    @Test
    public void testPantryDiscardIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("pantryDiscard", fields, AccessType.PRIVATE));
    }

    @Test
    public void testPantryIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("pantry", fields, AccessType.PRIVATE));
    }

    @Test
    public void testLayersIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("layers", fields, AccessType.PRIVATE));
    }

    @Test
    public void testCustomersIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("customers", fields, AccessType.PRIVATE));
    }

    @Test
    public void testRandomIsRandom() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("random", fields, Class.forName("java.util.Random")));
    }


    @Test
    public void testPlayersIsCollectionOfPlayer() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasGenericType("players", fields, Class.forName("java.util.Collection"), Class.forName("bakery.Player")));
    }

    @Test
    public void testPantryDeckIsCollectionOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasGenericType("pantryDeck", fields, Class.forName("java.util.Collection"), Class.forName("bakery.Ingredient")));
    }

    @Test
    public void testPantryDiscardIsCollectionOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasGenericType("pantryDiscard", fields, Class.forName("java.util.Collection"), Class.forName("bakery.Ingredient")));
    }

    @Test
    public void testPantryIsCollectionOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasGenericType("pantry", fields, Class.forName("java.util.Collection"), Class.forName("bakery.Ingredient")));
    }

    @Test
    public void testLayersIsCollectionOfLayer() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasGenericType("layers", fields, Class.forName("java.util.Collection"), Class.forName("bakery.Layer")));
    }

    @Test
    public void testCustomersIsCustomers() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("customers", fields, Class.forName("bakery.Customers")));
    }

    @Test
    public void testGetActionsRemainingExists() {
        assertTrue(StructuralHelper.methodExists("getActionsRemaining", methods));
    }

    @Test
    public void testGetCurrentPlayerExists() {
        assertTrue(StructuralHelper.methodExists("getCurrentPlayer", methods));
    }

    @Test
    public void testDrawFromPantryExists() {
        assertTrue(StructuralHelper.methodExists("drawFromPantry", methods));
    }

    @Test
    public void testDrawFromPantryDeckExists() {
        assertTrue(StructuralHelper.methodExists("drawFromPantryDeck", methods));
    }

    @Test
    public void testGetCustomersExists() {
        assertTrue(StructuralHelper.methodExists("getCustomers", methods));
    }

    @Test
    public void testGetActionsPermittedExists() {
        assertTrue(StructuralHelper.methodExists("getActionsPermitted", methods));
    }

    @Test
    public void testGetLayersExists() {
        assertTrue(StructuralHelper.methodExists("getLayers", methods));
    }

    @Test
    public void testGetFulfilableCustomersExists() {
        String getFulfillable = getMethodGetFulfilableCustomers();
        assertTrue(StructuralHelper.methodExists(getFulfillable, methods));
    }

    @Test
    public void testGetPantryExists() {
        assertTrue(StructuralHelper.methodExists("getPantry", methods));
    }

    @Test
    public void testPrintCustomerServiceRecordExists() {
        assertTrue(StructuralHelper.methodExists("printCustomerServiceRecord", methods));
    }

    @Test
    public void testBakeLayerExists() {
        assertTrue(StructuralHelper.methodExists("bakeLayer", methods));
    }

    @Test
    public void testEndTurnExists() {
        assertTrue(StructuralHelper.methodExists("endTurn", methods));
    }

    @Test
    public void testFulfillOrderExists() {
        assertTrue(StructuralHelper.methodExists("fulfillOrder", methods));
    }

    @Test
    public void testGetBakeableLayersExists() {
        assertTrue(StructuralHelper.methodExists("getBakeableLayers", methods));
    }

    @Test
    public void testGetGarnishableCustomersExists() {
        assertTrue(StructuralHelper.methodExists("getGarnishableCustomers", methods));
    }

    @Test
    public void testGetPlayersExists() {
        assertTrue(StructuralHelper.methodExists("getPlayers", methods));
    }

    @Test
    public void testLoadStateExists() {
        assertTrue(StructuralHelper.methodExists("loadState", methods));
    }

    @Test
    public void testPassCardExists() {
        assertTrue(StructuralHelper.methodExists("passCard", methods));
    }

    @Test
    public void testPrintGameStateExists() {
        assertTrue(StructuralHelper.methodExists("printGameState", methods));
    }

    @Test
    public void testRefreshPantryExists() {
        assertTrue(StructuralHelper.methodExists("refreshPantry", methods));
    }

    @Test
    public void testSaveStateExists() {
        assertTrue(StructuralHelper.methodExists("saveState", methods));
    }

    @Test
    public void testStartGameExists() {
        assertTrue(StructuralHelper.methodExists("startGame", methods));
    }

    @Test
    public void testGetActionsRemainingIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getActionsRemaining", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetCurrentPlayerIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getCurrentPlayer", methods, AccessType.PUBLIC));
    }

    @Test
    public void testDrawFromPantryIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("drawFromPantry", methods, AccessType.PUBLIC));
    }

    @Test
    public void testDrawFromPantryDeckIsPrivate() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("drawFromPantryDeck", methods, AccessType.PRIVATE));
    }

    @Test
    public void testGetCustomersIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getCustomers", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetActionsPermittedIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getActionsPermitted", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetLayersIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getLayers", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetFulfilableCustomersIsPublic() {
        String getFulfillable = getMethodGetFulfilableCustomers();
        assertTrue(StructuralHelper.methodAccessIsAccessType(getFulfillable, methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetPantryIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getPantry", methods, AccessType.PUBLIC));
    }

    @Test
    public void testPrintCustomerServiceRecordIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("printCustomerServiceRecord", methods, AccessType.PUBLIC));
    }

    @Test
    public void testBakeLayerIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("bakeLayer", methods, AccessType.PUBLIC));
    }

    @Test
    public void testEndTurnIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("endTurn", methods, AccessType.PUBLIC));
    }

    @Test
    public void testFulfillOrderIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("fulfillOrder", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetBakeableLayersIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getBakeableLayers", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetGarnishableCustomersIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getGarnishableCustomers", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetPlayersIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getPlayers", methods, AccessType.PUBLIC));
    }

    @Test
    public void testLoadStateIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("loadState", methods, AccessType.PUBLIC));
    }

    @Test
    public void testLoadStateIsStatic() {
        assertTrue(StructuralHelper.methodIsStatic("loadState", methods));
    }

    @Test
    public void testPassCardIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("passCard", methods, AccessType.PUBLIC));
    }

    @Test
    public void testPrintGameStateIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("printGameState", methods, AccessType.PUBLIC));
    }

    @Test
    public void testRefreshPantryIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("refreshPantry", methods, AccessType.PUBLIC));
    }

    @Test
    public void testSaveStateIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("saveState", methods, AccessType.PUBLIC));
    }

    @Test
    public void testStartGameIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("startGame", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetActionsRemainingTakesNothingReturnsint() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("getActionsRemaining", methods, int.class ));
    }

    @Test
    public void testGetCurrentPlayerTakesNothingReturnsPlayer() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("getCurrentPlayer", methods, Class.forName("bakery.Player") ));
    }

    @Test
    public void testDrawFromPantryTakesStringReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("drawFromPantry", methods, void.class , Class.forName("java.lang.String")));
    }

    @Test
    public void testDrawFromPantryTakesIngredientReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("drawFromPantry", methods, void.class , Class.forName("bakery.Ingredient")));
    }

    @Test
    public void testDrawFromPantryDeckTakesNothingReturnsIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("drawFromPantryDeck", methods, Class.forName("bakery.Ingredient") ));
    }

    @Test
    public void testGetCustomersTakesNothingReturnsCustomers() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("getCustomers", methods, Class.forName("bakery.Customers") ));
    }

    @Test
    public void testGetActionsPermittedTakesNothingReturnsint() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("getActionsPermitted", methods, int.class ));
    }

    @Test
    public void testGetLayersTakesNothingReturnsCollectionOfLayer() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("getLayers", methods, Class.forName("java.util.Collection"), Class.forName("bakery.Layer") ));
    }

    @Test
    public void testGetFulfilableCustomersTakesNothingReturnsCollectionOfCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        String getFulfillable = getMethodGetFulfilableCustomers();
        assertTrue(StructuralHelper.methodHasGenericReturnType(getFulfillable, methods, Class.forName("java.util.Collection"), Class.forName("bakery.CustomerOrder") ));
    }

    @Test
    public void testGetPantryTakesNothingReturnsCollectionOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("getPantry", methods, Class.forName("java.util.Collection"), Class.forName("bakery.Ingredient") ));
    }

    @Test
    public void testPrintCustomerServiceRecordTakesNothingReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("printCustomerServiceRecord", methods, void.class ));
    }

    @Test
    public void testBakeLayerTakesLayerReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("bakeLayer", methods, void.class , Class.forName("bakery.Layer")));
    }

    @Test
    public void testEndTurnTakesNothingReturnsboolean() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("endTurn", methods, boolean.class ));
    }

    @Test
    public void testFulfillOrderTakesCustomerOrderAndbooleanReturnsListOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("fulfillOrder", methods, Class.forName("java.util.List"), Class.forName("bakery.Ingredient") , Class.forName("bakery.CustomerOrder"), boolean.class));
    }

    @Test
    public void testGetBakeableLayersTakesNothingReturnsCollectionOfLayer() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("getBakeableLayers", methods, Class.forName("java.util.Collection"), Class.forName("bakery.Layer") ));
    }

    @Test
    public void testGetGarnishableCustomersTakesNothingReturnsCollectionOfCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("getGarnishableCustomers", methods, Class.forName("java.util.Collection"), Class.forName("bakery.CustomerOrder") ));
    }

    @Test
    public void testGetPlayersTakesNothingReturnsCollectionOfPlayer() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("getPlayers", methods, Class.forName("java.util.Collection"), Class.forName("bakery.Player") ));
    }

    @Test
    public void testLoadStateTakesFileReturnsMagicBakery() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("loadState", methods, Class.forName("bakery.MagicBakery") , Class.forName("java.io.File")));
    }

    @Test
    public void testPassCardTakesIngredientAndPlayerReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("passCard", methods, void.class , Class.forName("bakery.Ingredient"), Class.forName("bakery.Player")));
    }

    @Test
    public void testPrintGameStateTakesNothingReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("printGameState", methods, void.class ));
    }

    @Test
    public void testRefreshPantryTakesNothingReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("refreshPantry", methods, void.class ));
    }

    @Test
    public void testSaveStateTakesFileReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("saveState", methods, void.class , Class.forName("java.io.File")));
    }

    @Test
    public void testStartGameTakesListAndStringReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("startGame", methods, void.class , Class.forName("java.util.List"), Class.forName("java.lang.String")));
    }

}
