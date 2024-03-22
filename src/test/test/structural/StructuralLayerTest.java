package test.structural;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Tag("structural")
@Tag("Layer")
public class StructuralLayerTest {

    static String FQCN = "bakery.Layer";
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
    public void testClassImplementsComparable() {
        assertTrue(StructuralHelper.classIsComparable(FQCN));
    }

    @Test
    public void testClassImplementsSerializable() {
        assertTrue(StructuralHelper.classIsSerializable(FQCN));
    }

    @Test
    public void testClassIsIngredientSubclass() {
        assertTrue(StructuralHelper.classIsSubclassOf(FQCN, "bakery.Ingredient"));
    }

    @Test
    public void testStringAndListConstructorIsPublic() throws ClassNotFoundException, NoSuchMethodException {
        assertTrue(StructuralHelper.ctorIsPublic(FQCN , Class.forName("java.lang.String"), Class.forName("java.util.List")));
    }

    @Test
    public void testSerialVersionUIDExists() {
        assertTrue(StructuralHelper.classHasPublicStaticFinalSerialVersionUID(FQCN));
    }

    @Test
    public void testRecipeExists() {
        assertTrue(StructuralHelper.fieldExists("recipe", fields));
    }

    @Test
    public void testRecipeIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("recipe", fields, AccessType.PRIVATE));
    }

    @Test
    public void testRecipeIsListOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasGenericType("recipe", fields, Class.forName("java.util.List"), Class.forName("bakery.Ingredient")));
    }

    @Test
    public void testHashCodeExists() {
        assertTrue(StructuralHelper.methodExists("hashCode", methods));
    }

    @Test
    public void testGetRecipeDescriptionExists() {
        assertTrue(StructuralHelper.methodExists("getRecipeDescription", methods));
    }

    @Test
    public void testCanBakeExists() {
        assertTrue(StructuralHelper.methodExists("canBake", methods));
    }

    @Test
    public void testGetRecipeExists() {
        assertTrue(StructuralHelper.methodExists("getRecipe", methods));
    }

    @Test
    public void testHashCodeIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("hashCode", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetRecipeDescriptionIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getRecipeDescription", methods, AccessType.PUBLIC));
    }

    @Test
    public void testCanBakeIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("canBake", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetRecipeIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getRecipe", methods, AccessType.PUBLIC));
    }

    @Test
    public void testHashCodeTakesNothingReturnsint() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("hashCode", methods, int.class ));
    }

    @Test
    public void testGetRecipeDescriptionTakesNothingReturnsString() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("getRecipeDescription", methods, Class.forName("java.lang.String") ));
    }

    @Test
    public void testCanBakeTakesListReturnsboolean() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("canBake", methods, boolean.class , Class.forName("java.util.List")));
    }

    @Test
    public void testGetRecipeTakesNothingReturnsListOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("getRecipe", methods, Class.forName("java.util.List"), Class.forName("bakery.Ingredient") ));
    }

}
