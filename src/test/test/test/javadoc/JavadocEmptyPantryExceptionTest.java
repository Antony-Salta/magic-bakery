package test.javadoc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("javadoc")
@Tag("EmptyPantryException")
public class JavadocEmptyPantryExceptionTest {

    String FQCN = "bakery.EmptyPantryException";

    @Test
    public void testEmptyPantryExceptionIsDocumented() {
        assertTrue(JavadocHelper._testclassIsDocumented("bakery.EmptyPantryException"));
    }

    @Test
    public void testAllMembersDocumented() {
        assertTrue(JavadocHelper.allMembersAreDocumented(FQCN));
    }

    @Test
    public void testConstructorDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "EmptyPantryException\\s*\\(.*"));
    }

}
