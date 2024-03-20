package test.javadoc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("javadoc")
@Tag("Customers")
public class JavadocCustomersTest {

    String FQCN = "bakery.Customers";

    @Test
    public void testCustomersIsDocumented() {
        assertTrue(JavadocHelper._testclassIsDocumented("bakery.Customers"));
    }

    @Test
    public void testAllMembersDocumented() {
        assertTrue(JavadocHelper.allMembersAreDocumented(FQCN));
    }

    @Test
    public void testConstructorDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Customers\\s*\\(.*"));
    }

    @Test
    public void testRemoveDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "void\\s*remove\\s*\\(.*"));
    }

    @Test
    public void testIsEmptyDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "boolean\\s*isEmpty\\s*\\(.*"));
    }

    @Test
    public void testSizeDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "int\\s*size\\s*\\(.*"));
    }

    @Test
    public void testPeekDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "CustomerOrder\\s*peek\\s*\\(.*"));
    }

    @Test
    public void testTimePassesDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "CustomerOrder\\s*timePasses\\s*\\(.*"));
    }

    @Test
    public void testGetActiveCustomersDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Collection\\s*<\\s*CustomerOrder\\s*>\\s*getActiveCustomers\\s*\\(.*"));
    }

    @Test
    public void testDrawCustomerDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "CustomerOrder\\s*drawCustomer\\s*\\(.*"));
    }

    @Test
    public void testCustomerWillLeaveSoonDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "boolean\\s*customerWillLeaveSoon\\s*\\(.*"));
    }

    @Test
    public void testGetCustomerDeckDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Collection\\s*<\\s*CustomerOrder\\s*>\\s*getCustomerDeck\\s*\\(.*"));
    }

    @Test
    public void testAddCustomerOrderDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "CustomerOrder\\s*addCustomerOrder\\s*\\(.*"));
    }

    @Test
    public void testGetFulfilableDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Collection\\s*<\\s*CustomerOrder\\s*>\\s*getFulfil(l)?able\\s*\\(.*"));
    }

    @Test
    public void testGetInactiveCustomersWithStatusDocumented() {
        assertTrue(JavadocHelper._testmemberIsDocumented(FQCN, "Collection\\s*<\\s*CustomerOrder\\s*>\\s*getInactiveCustomersWithStatus\\s*\\(.*"));
    }

}
