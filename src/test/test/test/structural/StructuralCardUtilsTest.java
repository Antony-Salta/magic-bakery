package test.structural;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Tag("structural")
@Tag("CardUtils")
public class StructuralCardUtilsTest {

    static String FQCN = "util.CardUtils";
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
    public void testEmptyConstructorIsPrivate() throws ClassNotFoundException, NoSuchMethodException {
        assertTrue(StructuralHelper.ctorIsPrivate(FQCN ));
    }

    @Test
    public void testReadIngredientFileExists() {
        assertTrue(StructuralHelper.methodExists("readIngredientFile", methods));
    }

    @Test
    public void testReadCustomerFileExists() {
        assertTrue(StructuralHelper.methodExists("readCustomerFile", methods));
    }

    @Test
    public void testStringToCustomerOrderExists() {
        assertTrue(StructuralHelper.methodExists("stringToCustomerOrder", methods));
    }

    @Test
    public void testStringToLayersExists() {
        assertTrue(StructuralHelper.methodExists("stringToLayers", methods));
    }

    @Test
    public void testStringToIngredientsExists() {
        assertTrue(StructuralHelper.methodExists("stringToIngredients", methods));
    }

    @Test
    public void testReadLayerFileExists() {
        assertTrue(StructuralHelper.methodExists("readLayerFile", methods));
    }

    @Test
    public void testReadIngredientFileIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("readIngredientFile", methods, AccessType.PUBLIC));
    }

    @Test
    public void testReadIngredientFileIsStatic() {
        assertTrue(StructuralHelper.methodIsStatic("readIngredientFile", methods));
    }

    @Test
    public void testReadCustomerFileIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("readCustomerFile", methods, AccessType.PUBLIC));
    }

    @Test
    public void testReadCustomerFileIsStatic() {
        assertTrue(StructuralHelper.methodIsStatic("readCustomerFile", methods));
    }

    @Test
    public void testStringToCustomerOrderIsPrivate() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("stringToCustomerOrder", methods, AccessType.PRIVATE));
    }

    @Test
    public void testStringToCustomerOrderIsStatic() {
        assertTrue(StructuralHelper.methodIsStatic("stringToCustomerOrder", methods));
    }

    @Test
    public void testStringToLayersIsPrivate() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("stringToLayers", methods, AccessType.PRIVATE));
    }

    @Test
    public void testStringToLayersIsStatic() {
        assertTrue(StructuralHelper.methodIsStatic("stringToLayers", methods));
    }

    @Test
    public void testStringToIngredientsIsPrivate() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("stringToIngredients", methods, AccessType.PRIVATE));
    }

    @Test
    public void testStringToIngredientsIsStatic() {
        assertTrue(StructuralHelper.methodIsStatic("stringToIngredients", methods));
    }

    @Test
    public void testReadLayerFileIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("readLayerFile", methods, AccessType.PUBLIC));
    }

    @Test
    public void testReadLayerFileIsStatic() {
        assertTrue(StructuralHelper.methodIsStatic("readLayerFile", methods));
    }

    @Test
    public void testReadIngredientFileTakesStringReturnsListOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("readIngredientFile", methods, Class.forName("java.util.List"), Class.forName("bakery.Ingredient") , Class.forName("java.lang.String")));
    }

    @Test
    public void testReadCustomerFileTakesStringAndCollectionReturnsListOfCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("readCustomerFile", methods, Class.forName("java.util.List"), Class.forName("bakery.CustomerOrder") , Class.forName("java.lang.String"), Class.forName("java.util.Collection")));
    }

    @Test
    public void testStringToCustomerOrderTakesStringAndCollectionReturnsCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("stringToCustomerOrder", methods, Class.forName("bakery.CustomerOrder") , Class.forName("java.lang.String"), Class.forName("java.util.Collection")));
    }

    @Test
    public void testStringToLayersTakesStringReturnsListOfLayer() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("stringToLayers", methods, Class.forName("java.util.List"), Class.forName("bakery.Layer") , Class.forName("java.lang.String")));
    }

    @Test
    public void testStringToIngredientsTakesStringReturnsListOfIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("stringToIngredients", methods, Class.forName("java.util.List"), Class.forName("bakery.Ingredient") , Class.forName("java.lang.String")));
    }

    @Test
    public void testReadLayerFileTakesStringReturnsListOfLayer() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("readLayerFile", methods, Class.forName("java.util.List"), Class.forName("bakery.Layer") , Class.forName("java.lang.String")));
    }

}
