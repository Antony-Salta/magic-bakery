package test.structural;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
@Tag("structural")
@Tag("ActionType")
public class StructuralActionTypeTest {

    static String FQCN = "bakery.MagicBakery$ActionType";
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
    public void testDRAW_INGREDIENTExists() {
        assertTrue(StructuralHelper.fieldExists("DRAW_INGREDIENT", fields));
    }

    @Test
    public void testPASS_INGREDIENTExists() {
        assertTrue(StructuralHelper.fieldExists("PASS_INGREDIENT", fields));
    }

    @Test
    public void testBAKE_LAYERExists() {
        assertTrue(StructuralHelper.fieldExists("BAKE_LAYER", fields));
    }

    @Test
    public void testFULFIL_ORDERExists() {
        assertTrue(StructuralHelper.fieldExists("FULFIL_ORDER", fields));
    }

    @Test
    public void testREFRESH_PANTRYExists() {
        assertTrue(StructuralHelper.fieldExists("REFRESH_PANTRY", fields));
    }

    @Test
    public void testDRAW_INGREDIENTIsPublic() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("DRAW_INGREDIENT", fields, AccessType.PUBLIC));
    }

    @Test
    public void testDRAW_INGREDIENTIsStatic() {
        assertTrue(StructuralHelper.fieldIsStatic("DRAW_INGREDIENT", fields));
    }

    @Test
    public void testDRAW_INGREDIENTIsFinal() {
        assertTrue(StructuralHelper.fieldIsFinal("DRAW_INGREDIENT", fields));
    }

    @Test
    public void testPASS_INGREDIENTIsPublic() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("PASS_INGREDIENT", fields, AccessType.PUBLIC));
    }

    @Test
    public void testPASS_INGREDIENTIsStatic() {
        assertTrue(StructuralHelper.fieldIsStatic("PASS_INGREDIENT", fields));
    }

    @Test
    public void testPASS_INGREDIENTIsFinal() {
        assertTrue(StructuralHelper.fieldIsFinal("PASS_INGREDIENT", fields));
    }

    @Test
    public void testBAKE_LAYERIsPublic() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("BAKE_LAYER", fields, AccessType.PUBLIC));
    }

    @Test
    public void testBAKE_LAYERIsStatic() {
        assertTrue(StructuralHelper.fieldIsStatic("BAKE_LAYER", fields));
    }

    @Test
    public void testBAKE_LAYERIsFinal() {
        assertTrue(StructuralHelper.fieldIsFinal("BAKE_LAYER", fields));
    }

    @Test
    public void testFULFIL_ORDERIsPublic() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("FULFIL_ORDER", fields, AccessType.PUBLIC));
    }

    @Test
    public void testFULFIL_ORDERIsStatic() {
        assertTrue(StructuralHelper.fieldIsStatic("FULFIL_ORDER", fields));
    }

    @Test
    public void testFULFIL_ORDERIsFinal() {
        assertTrue(StructuralHelper.fieldIsFinal("FULFIL_ORDER", fields));
    }

    @Test
    public void testREFRESH_PANTRYIsPublic() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("REFRESH_PANTRY", fields, AccessType.PUBLIC));
    }

    @Test
    public void testREFRESH_PANTRYIsStatic() {
        assertTrue(StructuralHelper.fieldIsStatic("REFRESH_PANTRY", fields));
    }

    @Test
    public void testREFRESH_PANTRYIsFinal() {
        assertTrue(StructuralHelper.fieldIsFinal("REFRESH_PANTRY", fields));
    }

    @Test
    public void testDRAW_INGREDIENTIsActionType() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("DRAW_INGREDIENT", fields, Class.forName("bakery.MagicBakery$ActionType")));
    }

    @Test
    public void testPASS_INGREDIENTIsActionType() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("PASS_INGREDIENT", fields, Class.forName("bakery.MagicBakery$ActionType")));
    }

    @Test
    public void testBAKE_LAYERIsActionType() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("BAKE_LAYER", fields, Class.forName("bakery.MagicBakery$ActionType")));
    }

    @Test
    public void testFULFIL_ORDERIsActionType() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("FULFIL_ORDER", fields, Class.forName("bakery.MagicBakery$ActionType")));
    }

    @Test
    public void testREFRESH_PANTRYIsActionType() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("REFRESH_PANTRY", fields, Class.forName("bakery.MagicBakery$ActionType")));
    }

}
