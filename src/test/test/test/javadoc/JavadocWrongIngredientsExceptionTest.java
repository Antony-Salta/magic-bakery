package test.javadoc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("javadoc")
@Tag("WrongIngredientsException")
public class JavadocWrongIngredientsExceptionTest {

    String FQCN = "bakery.WrongIngredientsException";

    @Test
    public void testWrongIngredientsExceptionIsDocumented() {
        assertTrue(JavadocHelper._testclassIsDocumented("bakery.WrongIngredientsException"));
    }

    @Test
    public void testAllMembersDocumented() {
        assertTrue(JavadocHelper.allMembersAreDocumented(FQCN));
    }

    @Test
    public void testConstructorDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "WrongIngredientsException\\s*\\(.*"));
    }

}
