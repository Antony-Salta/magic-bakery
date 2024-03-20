package test.structural;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("structural")
@Tag("EmptyPantryException")
public class StructuralEmptyPantryExceptionTest {

    static String FQCN = "bakery.EmptyPantryException";

    @BeforeAll
    public static void setUp() {
        // Fail early and cleanly if the class is effectively empty
        assertNotNull(StructuralHelper.getFields(FQCN), "The class is empty or it has not compiled successfully, I will not run structural tests on it");
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
    public void testClassIsException() {
        assertTrue(StructuralHelper.classIsException(FQCN));
    }

    @Test
    public void testStringAndThrowableConstructorIsPublic() throws ClassNotFoundException, NoSuchMethodException {
        assertTrue(StructuralHelper.ctorIsPublic(FQCN , Class.forName("java.lang.String"), Class.forName("java.lang.Throwable")));
    }

}
