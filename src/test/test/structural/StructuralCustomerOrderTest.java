package test.structural;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Tag("structural")
@Tag("CustomerOrder")
public class StructuralCustomerOrderTest {

    static String FQCN = "bakery.CustomerOrder";
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
    public void testParam4ConstructorIsPublic() throws ClassNotFoundException, NoSuchMethodException {
        assertTrue(StructuralHelper.ctorIsPublic(FQCN , Class.forName("java.lang.String"), Class.forName("java.util.List"), Class.forName("java.util.List"), int.class));
    }

    @Test
    public void testSerialVersionUIDExists() {
        assertTrue(StructuralHelper.classHasPublicStaticFinalSerialVersionUID(FQCN));
    }

    @Test
    public void testNameExists() {
        assertTrue(StructuralHelper.fieldExists("name", fields));
    }

    @Test
    public void testLevelExists() {
        assertTrue(StructuralHelper.fieldExists("level", fields));
    }

    @Test
    public void testRecipeExists() {
        assertTrue(StructuralHelper.fieldExists("recipe", fields));
    }

    @Test
    public void testGarnishExists() {
        assertTrue(StructuralHelper.fieldExists("garnish", fields));
    }

    @Test
    public void testStatusExists() {
        assertTrue(StructuralHelper.fieldExists("status", fields));
    }

    @Test
    public void testNameIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("name", fields, AccessType.PRIVATE));
    }

    @Test
    public void testLevelIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("level", fields, AccessType.PRIVATE));
    }

    @Test
    public void testRecipeIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("recipe", fields, AccessType.PRIVATE));
    }

    @Test
    public void testGarnishIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("garnish", fields, AccessType.PRIVATE));
    }

    @Test
    public void testStatusIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("status", fields, AccessType.PRIVATE));
    }

    @Test
    public void testNameIsString() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("name", fields, Class.forName("java.lang.String")));
    }

    @Test
    public void testLevelIsint() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("level", fields, int.class));
    }

    @Test
    public void testRecipeIsListOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasGenericType("recipe", fields, Class.forName("java.util.List"), Class.forName("bakery.Ingredient")));
    }

    @Test
    public void testGarnishIsListOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasGenericType("garnish", fields, Class.forName("java.util.List"), Class.forName("bakery.Ingredient")));
    }

    @Test
    public void testStatusIsCustomerOrderStatus() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("status", fields, Class.forName("bakery.CustomerOrder$CustomerOrderStatus")));
    }

    @Test
    public void testToStringExists() {
        assertTrue(StructuralHelper.methodExists("toString", methods));
    }

    @Test
    public void testGetLevelExists() {
        assertTrue(StructuralHelper.methodExists("getLevel", methods));
    }

    @Test
    public void testSetStatusExists() {
        assertTrue(StructuralHelper.methodExists("setStatus", methods));
    }

    @Test
    public void testGetGarnishExists() {
        assertTrue(StructuralHelper.methodExists("getGarnish", methods));
    }

    @Test
    public void testCanGarnishExists() {
        assertTrue(StructuralHelper.methodExists("canGarnish", methods));
    }

    @Test
    public void testFulfillExists() {
        assertTrue(StructuralHelper.methodExists("fulfill", methods));
    }

    @Test
    public void testGetGarnishDescriptionExists() {
        assertTrue(StructuralHelper.methodExists("getGarnishDescription", methods));
    }

    @Test
    public void testGetRecipeDescriptionExists() {
        assertTrue(StructuralHelper.methodExists("getRecipeDescription", methods));
    }

    @Test
    public void testGetRecipeExists() {
        assertTrue(StructuralHelper.methodExists("getRecipe", methods));
    }

    @Test
    public void testCanFulfillExists() {
        assertTrue(StructuralHelper.methodExists("canFulfill", methods));
    }

    @Test
    public void testAbandonExists() {
        assertTrue(StructuralHelper.methodExists("abandon", methods));
    }

    @Test
    public void testGetStatusExists() {
        assertTrue(StructuralHelper.methodExists("getStatus", methods));
    }

    @Test
    public void testToStringIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("toString", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetLevelIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getLevel", methods, AccessType.PUBLIC));
    }

    @Test
    public void testSetStatusIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("setStatus", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetGarnishIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getGarnish", methods, AccessType.PUBLIC));
    }

    @Test
    public void testCanGarnishIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("canGarnish", methods, AccessType.PUBLIC));
    }

    @Test
    public void testFulfillIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("fulfill", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetGarnishDescriptionIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getGarnishDescription", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetRecipeDescriptionIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getRecipeDescription", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetRecipeIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getRecipe", methods, AccessType.PUBLIC));
    }

    @Test
    public void testCanFulfillIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("canFulfill", methods, AccessType.PUBLIC));
    }

    @Test
    public void testAbandonIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("abandon", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetStatusIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getStatus", methods, AccessType.PUBLIC));
    }

    @Test
    public void testToStringTakesNothingReturnsString() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("toString", methods, Class.forName("java.lang.String") ));
    }

    @Test
    public void testGetLevelTakesNothingReturnsint() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("getLevel", methods, int.class ));
    }

    @Test
    public void testSetStatusTakesCustomerOrderStatusReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("setStatus", methods, void.class , Class.forName("bakery.CustomerOrder$CustomerOrderStatus")));
    }

    @Test
    public void testGetGarnishTakesNothingReturnsListOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("getGarnish", methods, Class.forName("java.util.List"), Class.forName("bakery.Ingredient") ));
    }

    @Test
    public void testCanGarnishTakesListReturnsboolean() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("canGarnish", methods, boolean.class , Class.forName("java.util.List")));
    }

    @Test
    public void testFulfillTakesListAndbooleanReturnsListOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("fulfill", methods, Class.forName("java.util.List"), Class.forName("bakery.Ingredient") , Class.forName("java.util.List"), boolean.class));
    }

    @Test
    public void testGetGarnishDescriptionTakesNothingReturnsString() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("getGarnishDescription", methods, Class.forName("java.lang.String") ));
    }

    @Test
    public void testGetRecipeDescriptionTakesNothingReturnsString() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("getRecipeDescription", methods, Class.forName("java.lang.String") ));
    }

    @Test
    public void testGetRecipeTakesNothingReturnsListOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("getRecipe", methods, Class.forName("java.util.List"), Class.forName("bakery.Ingredient") ));
    }

    @Test
    public void testCanFulfillTakesListReturnsboolean() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("canFulfill", methods, boolean.class , Class.forName("java.util.List")));
    }

    @Test
    public void testAbandonTakesNothingReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("abandon", methods, void.class ));
    }

    @Test
    public void testGetStatusTakesNothingReturnsCustomerOrderStatus() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("getStatus", methods, Class.forName("bakery.CustomerOrder$CustomerOrderStatus") ));
    }

}
