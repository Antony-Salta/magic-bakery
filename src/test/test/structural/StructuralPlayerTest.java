package test.structural;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


@Tag("structural")
@Tag("Player")
public class StructuralPlayerTest {

    static String FQCN = "bakery.Player";
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
    public void testClassImplementsSerializable() {
        assertTrue(StructuralHelper.classIsSerializable(FQCN));
    }

    @Test
    public void testStringConstructorIsPublic() throws ClassNotFoundException, NoSuchMethodException {
        assertTrue(StructuralHelper.ctorIsPublic(FQCN , Class.forName("java.lang.String")));
    }

    @Test
    public void testSerialVersionUIDExists() {
        assertTrue(StructuralHelper.classHasPublicStaticFinalSerialVersionUID(FQCN));
    }

    @Test
    public void testHandExists() {
        assertTrue(StructuralHelper.fieldExists("hand", fields));
    }

    @Test
    public void testNameExists() {
        assertTrue(StructuralHelper.fieldExists("name", fields));
    }

    @Test
    public void testHandIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("hand", fields, AccessType.PRIVATE));
    }

    @Test
    public void testNameIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("name", fields, AccessType.PRIVATE));
    }

    @Test
    public void testHandIsListOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasGenericType("hand", fields, Class.forName("java.util.List"), Class.forName("bakery.Ingredient")));
    }

    @Test
    public void testNameIsString() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("name", fields, Class.forName("java.lang.String")));
    }

    @Test
    public void testToStringExists() {
        assertTrue(StructuralHelper.methodExists("toString", methods));
    }

    @Test
    public void testRemoveFromHandExists() {
        assertTrue(StructuralHelper.methodExists("removeFromHand", methods));
    }

    @Test
    public void testGetHandStrExists() {
        assertTrue(StructuralHelper.methodExists("getHandStr", methods));
    }

    @Test
    public void testAddToHandExists() {
        assertTrue(StructuralHelper.methodExists("addToHand", methods));
    }

    @Test
    public void testHasIngredientExists() {
        assertTrue(StructuralHelper.methodExists("hasIngredient", methods));
    }

    @Test
    public void testGetHandExists() {
        assertTrue(StructuralHelper.methodExists("getHand", methods));
    }

    @Test
    public void testToStringIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("toString", methods, AccessType.PUBLIC));
    }

    @Test
    public void testRemoveFromHandIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("removeFromHand", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetHandStrIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getHandStr", methods, AccessType.PUBLIC));
    }

    @Test
    public void testAddToHandIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("addToHand", methods, AccessType.PUBLIC));
    }

    @Test
    public void testHasIngredientIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("hasIngredient", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetHandIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getHand", methods, AccessType.PUBLIC));
    }

    @Test
    public void testToStringTakesNothingReturnsString() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("toString", methods, Class.forName("java.lang.String") ));
    }

    @Test
    public void testRemoveFromHandTakesIngredientReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("removeFromHand", methods, void.class , Class.forName("bakery.Ingredient")));
    }

    @Test
    public void testGetHandStrTakesNothingReturnsString() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("getHandStr", methods, Class.forName("java.lang.String") ));
    }

    @Test
    public void testAddToHandTakesIngredientReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("addToHand", methods, void.class , Class.forName("bakery.Ingredient")));
    }

    @Test
    public void testAddToHandTakesListReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("addToHand", methods, void.class , Class.forName("java.util.List")));
    }

    @Test
    public void testHasIngredientTakesIngredientReturnsboolean() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("hasIngredient", methods, boolean.class , Class.forName("bakery.Ingredient")));
    }

    @Test
    public void testGetHandTakesNothingReturnsListOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("getHand", methods, Class.forName("java.util.List"), Class.forName("bakery.Ingredient") ));
    }

}
