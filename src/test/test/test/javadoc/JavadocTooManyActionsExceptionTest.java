package test.javadoc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("javadoc")
@Tag("TooManyActionsException")
public class JavadocTooManyActionsExceptionTest {

    String FQCN = "bakery.TooManyActionsException";

    @Test
    public void testTooManyActionsExceptionIsDocumented() {
        assertTrue(JavadocHelper._testclassIsDocumented("bakery.TooManyActionsException"));
    }

    @Test
    public void testAllMembersDocumented() {
        assertTrue(JavadocHelper.allMembersAreDocumented(FQCN));
    }

    @Test
    public void testConstructorDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "TooManyActionsException\\s*\\(.*"));
    }

}
