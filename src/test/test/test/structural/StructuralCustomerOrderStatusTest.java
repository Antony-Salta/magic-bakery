package test.structural;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Tag("structural")
@Tag("CustomerOrderStatus")
public class StructuralCustomerOrderStatusTest {

    static String FQCN = "bakery.CustomerOrder$CustomerOrderStatus";
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
    public void testWAITINGExists() {
        assertTrue(StructuralHelper.fieldExists("WAITING", fields));
    }

    @Test
    public void testFULFILLEDExists() {
        assertTrue(StructuralHelper.fieldExists("FULFILLED", fields));
    }

    @Test
    public void testGARNISHEDExists() {
        assertTrue(StructuralHelper.fieldExists("GARNISHED", fields));
    }

    @Test
    public void testIMPATIENTExists() {
        assertTrue(StructuralHelper.fieldExists("IMPATIENT", fields));
    }

    @Test
    public void testGIVEN_UPExists() {
        assertTrue(StructuralHelper.fieldExists("GIVEN_UP", fields));
    }

    @Test
    public void testWAITINGIsPublic() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("WAITING", fields, AccessType.PUBLIC));
    }

    @Test
    public void testWAITINGIsStatic() {
        assertTrue(StructuralHelper.fieldIsStatic("WAITING", fields));
    }

    @Test
    public void testWAITINGIsFinal() {
        assertTrue(StructuralHelper.fieldIsFinal("WAITING", fields));
    }

    @Test
    public void testFULFILLEDIsPublic() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("FULFILLED", fields, AccessType.PUBLIC));
    }

    @Test
    public void testFULFILLEDIsStatic() {
        assertTrue(StructuralHelper.fieldIsStatic("FULFILLED", fields));
    }

    @Test
    public void testFULFILLEDIsFinal() {
        assertTrue(StructuralHelper.fieldIsFinal("FULFILLED", fields));
    }

    @Test
    public void testGARNISHEDIsPublic() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("GARNISHED", fields, AccessType.PUBLIC));
    }

    @Test
    public void testGARNISHEDIsStatic() {
        assertTrue(StructuralHelper.fieldIsStatic("GARNISHED", fields));
    }

    @Test
    public void testGARNISHEDIsFinal() {
        assertTrue(StructuralHelper.fieldIsFinal("GARNISHED", fields));
    }

    @Test
    public void testIMPATIENTIsPublic() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("IMPATIENT", fields, AccessType.PUBLIC));
    }

    @Test
    public void testIMPATIENTIsStatic() {
        assertTrue(StructuralHelper.fieldIsStatic("IMPATIENT", fields));
    }

    @Test
    public void testIMPATIENTIsFinal() {
        assertTrue(StructuralHelper.fieldIsFinal("IMPATIENT", fields));
    }

    @Test
    public void testGIVEN_UPIsPublic() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("GIVEN_UP", fields, AccessType.PUBLIC));
    }

    @Test
    public void testGIVEN_UPIsStatic() {
        assertTrue(StructuralHelper.fieldIsStatic("GIVEN_UP", fields));
    }

    @Test
    public void testGIVEN_UPIsFinal() {
        assertTrue(StructuralHelper.fieldIsFinal("GIVEN_UP", fields));
    }

    @Test
    public void testWAITINGIsCustomerOrderStatus() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("WAITING", fields, Class.forName("bakery.CustomerOrder$CustomerOrderStatus")));
    }

    @Test
    public void testFULFILLEDIsCustomerOrderStatus() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("FULFILLED", fields, Class.forName("bakery.CustomerOrder$CustomerOrderStatus")));
    }

    @Test
    public void testGARNISHEDIsCustomerOrderStatus() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("GARNISHED", fields, Class.forName("bakery.CustomerOrder$CustomerOrderStatus")));
    }

    @Test
    public void testIMPATIENTIsCustomerOrderStatus() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("IMPATIENT", fields, Class.forName("bakery.CustomerOrder$CustomerOrderStatus")));
    }

    @Test
    public void testGIVEN_UPIsCustomerOrderStatus() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("GIVEN_UP", fields, Class.forName("bakery.CustomerOrder$CustomerOrderStatus")));
    }
}
