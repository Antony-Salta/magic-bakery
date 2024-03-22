package test.structural;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Tag("structural")
@Tag("Ingredient")
public class StructuralIngredientTest {

    static String FQCN = "bakery.Ingredient";
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
    public void testStringConstructorIsPublic() throws ClassNotFoundException, NoSuchMethodException {
        assertTrue(StructuralHelper.ctorIsPublic(FQCN , Class.forName("java.lang.String")));
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
    public void testHELPFUL_DUCKExists() {
        assertTrue(StructuralHelper.fieldExists("HELPFUL_DUCK", fields));
    }

    @Test
    public void testNameIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("name", fields, AccessType.PRIVATE));
    }

    @Test
    public void testHELPFUL_DUCKIsPublic() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("HELPFUL_DUCK", fields, AccessType.PUBLIC));
    }

    @Test
    public void testHELPFUL_DUCKIsStatic() {
        assertTrue(StructuralHelper.fieldIsStatic("HELPFUL_DUCK", fields));
    }

    @Test
    public void testHELPFUL_DUCKIsFinal() {
        assertTrue(StructuralHelper.fieldIsFinal("HELPFUL_DUCK", fields));
    }

    @Test
    public void testNameIsString() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("name", fields, Class.forName("java.lang.String")));
    }

    @Test
    public void testHELPFUL_DUCKIsIngredient() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("HELPFUL_DUCK", fields, Class.forName("bakery.Ingredient")));
    }

    @Test
    public void testEqualsExists() {
        assertTrue(StructuralHelper.methodExists("equals", methods));
    }

    @Test
    public void testToStringExists() {
        assertTrue(StructuralHelper.methodExists("toString", methods));
    }

    @Test
    public void testHashCodeExists() {
        assertTrue(StructuralHelper.methodExists("hashCode", methods));
    }

    @Test
    public void testCompareToExists() {
        assertTrue(StructuralHelper.methodExists("compareTo", methods));
    }

    @Test
    public void testEqualsIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("equals", methods, AccessType.PUBLIC));
    }

    @Test
    public void testToStringIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("toString", methods, AccessType.PUBLIC));
    }

    @Test
    public void testHashCodeIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("hashCode", methods, AccessType.PUBLIC));
    }

    @Test
    public void testCompareToIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("compareTo", methods, AccessType.PUBLIC));
    }

    @Test
    public void testEqualsTakesObjectReturnsboolean() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("equals", methods, boolean.class , Class.forName("java.lang.Object")));
    }

    @Test
    public void testToStringTakesNothingReturnsString() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("toString", methods, Class.forName("java.lang.String") ));
    }

    @Test
    public void testHashCodeTakesNothingReturnsint() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("hashCode", methods, int.class ));
    }

    @Test
    public void testCompareToTakesObjectReturnsint() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("compareTo", methods, int.class , Class.forName("java.lang.Object")));
    }

    @Test
    public void testCompareToTakesIngredientReturnsint() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("compareTo", methods, int.class , Class.forName("bakery.Ingredient")));
    }

}
