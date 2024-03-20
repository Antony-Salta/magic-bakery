package test.structural;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Tag("structural")
@Tag("Customers")
public class StructuralCustomersTest {

    static String FQCN = "bakery.Customers";
    static Field[] fields;
    static Method[] methods;

    public String getMethodGetFulfilable() {
        if (StructuralHelper.methodExists("getFulfilable", methods)) {
            return "getFulfilable";
        }
        return "getFulfillable";
    }

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
    public void testClassImplementsSerializable() {
        assertTrue(StructuralHelper.classIsSerializable(FQCN));
    }

    @Test
    public void testParam4ConstructorIsPublic() throws ClassNotFoundException, NoSuchMethodException {
        assertTrue(StructuralHelper.ctorIsPublic(FQCN , Class.forName("java.lang.String"), Class.forName("java.util.Random"), Class.forName("java.util.Collection"), int.class));
    }

    @Test
    public void testSerialVersionUIDExists() {
        assertTrue(StructuralHelper.classHasPublicStaticFinalSerialVersionUID(FQCN));
    }

    @Test
    public void testActiveCustomersExists() {
        assertTrue(StructuralHelper.fieldExists("activeCustomers", fields));
    }

    @Test
    public void testCustomerDeckExists() {
        assertTrue(StructuralHelper.fieldExists("customerDeck", fields));
    }

    @Test
    public void testInactiveCustomersExists() {
        assertTrue(StructuralHelper.fieldExists("inactiveCustomers", fields));
    }

    @Test
    public void testRandomExists() {
        assertTrue(StructuralHelper.fieldExists("random", fields));
    }

    

    @Test
    public void testActiveCustomersIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("activeCustomers", fields, AccessType.PRIVATE));
    }

    @Test
    public void testCustomerDeckIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("customerDeck", fields, AccessType.PRIVATE));
    }

    @Test
    public void testInactiveCustomersIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("inactiveCustomers", fields, AccessType.PRIVATE));
    }

    @Test
    public void testRandomIsPrivate() {
        assertTrue(StructuralHelper.fieldAccessIsAccessType("random", fields, AccessType.PRIVATE));
    }


    @Test
    public void testActiveCustomersIsCollectionOfCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasGenericType("activeCustomers", fields, Class.forName("java.util.Collection"), Class.forName("bakery.CustomerOrder")));
    }

    @Test
    public void testCustomerDeckIsCollectionOfCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasGenericType("customerDeck", fields, Class.forName("java.util.Collection"), Class.forName("bakery.CustomerOrder")));
    }

    @Test
    public void testInactiveCustomersIsListOfCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasGenericType("inactiveCustomers", fields, Class.forName("java.util.List"), Class.forName("bakery.CustomerOrder")));
    }

    @Test
    public void testRandomIsRandom() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.fieldHasType("random", fields, Class.forName("java.util.Random")));
    }

    @Test
    public void testRemoveExists() {
        assertTrue(StructuralHelper.methodExists("remove", methods));
    }

    @Test
    public void testIsEmptyExists() {
        assertTrue(StructuralHelper.methodExists("isEmpty", methods));
    }

    @Test
    public void testSizeExists() {
        assertTrue(StructuralHelper.methodExists("size", methods));
    }

    @Test
    public void testPeekExists() {
        assertTrue(StructuralHelper.methodExists("peek", methods));
    }

    @Test
    public void testInitialiseCustomerDeckExists() {
        assertTrue(StructuralHelper.methodExists("initialiseCustomerDeck", methods));
    }

    @Test
    public void testTimePassesExists() {
        assertTrue(StructuralHelper.methodExists("timePasses", methods));
    }

    @Test
    public void testGetActiveCustomersExists() {
        assertTrue(StructuralHelper.methodExists("getActiveCustomers", methods));
    }

    @Test
    public void testDrawCustomerExists() {
        assertTrue(StructuralHelper.methodExists("drawCustomer", methods));
    }

    @Test
    public void testCustomerWillLeaveSoonExists() {
        assertTrue(StructuralHelper.methodExists("customerWillLeaveSoon", methods));
    }

    @Test
    public void testGetCustomerDeckExists() {
        assertTrue(StructuralHelper.methodExists("getCustomerDeck", methods));
    }

    @Test
    public void testAddCustomerOrderExists() {
        assertTrue(StructuralHelper.methodExists("addCustomerOrder", methods));
    }

    @Test
    public void testGetFulfilableExists() {
        String getFulfillable = getMethodGetFulfilable();
        assertTrue(StructuralHelper.methodExists(getFulfillable, methods));
    }

    @Test
    public void testGetInactiveCustomersWithStatusExists() {
        assertTrue(StructuralHelper.methodExists("getInactiveCustomersWithStatus", methods));
    }

    @Test
    public void testRemoveIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("remove", methods, AccessType.PUBLIC));
    }

    @Test
    public void testIsEmptyIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("isEmpty", methods, AccessType.PUBLIC));
    }

    @Test
    public void testSizeIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("size", methods, AccessType.PUBLIC));
    }

    @Test
    public void testPeekIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("peek", methods, AccessType.PUBLIC));
    }

    @Test
    public void testInitialiseCustomerDeckIsPrivate() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("initialiseCustomerDeck", methods, AccessType.PRIVATE));
    }

    @Test
    public void testTimePassesIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("timePasses", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetActiveCustomersIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getActiveCustomers", methods, AccessType.PUBLIC));
    }

    @Test
    public void testDrawCustomerIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("drawCustomer", methods, AccessType.PUBLIC));
    }

    @Test
    public void testCustomerWillLeaveSoonIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("customerWillLeaveSoon", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetCustomerDeckIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getCustomerDeck", methods, AccessType.PUBLIC));
    }

    @Test
    public void testAddCustomerOrderIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("addCustomerOrder", methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetFulfilableIsPublic() {
        String getFulfillable = getMethodGetFulfilable();
        assertTrue(StructuralHelper.methodAccessIsAccessType(getFulfillable, methods, AccessType.PUBLIC));
    }

    @Test
    public void testGetInactiveCustomersWithStatusIsPublic() {
        assertTrue(StructuralHelper.methodAccessIsAccessType("getInactiveCustomersWithStatus", methods, AccessType.PUBLIC));
    }
   
    @Test
    public void testRemoveTakesCustomerOrderReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("remove", methods, void.class , Class.forName("bakery.CustomerOrder")));
    }

    @Test
    public void testIsEmptyTakesNothingReturnsboolean() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("isEmpty", methods, boolean.class ));
    }

    @Test
    public void testSizeTakesNothingReturnsint() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("size", methods, int.class ));
    }

    @Test
    public void testPeekTakesNothingReturnsCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("peek", methods, Class.forName("bakery.CustomerOrder") ));
    }

    @Test
    public void testInitialiseCustomerDeckTakesStringAndCollectionAndintReturnsvoid() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("initialiseCustomerDeck", methods, void.class , Class.forName("java.lang.String"), Class.forName("java.util.Collection"), int.class));
    }

    @Test
    public void testTimePassesTakesNothingReturnsCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("timePasses", methods, Class.forName("bakery.CustomerOrder") ));
    }

    @Test
    public void testGetActiveCustomersTakesNothingReturnsCollectionOfCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("getActiveCustomers", methods, Class.forName("java.util.Collection"), Class.forName("bakery.CustomerOrder") ));
    }

    @Test
    public void testDrawCustomerTakesNothingReturnsCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("drawCustomer", methods, Class.forName("bakery.CustomerOrder") ));
    }

    @Test
    public void testCustomerWillLeaveSoonTakesNothingReturnsboolean() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("customerWillLeaveSoon", methods, boolean.class ));
    }

    @Test
    public void testGetCustomerDeckTakesNothingReturnsCollectionOfCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("getCustomerDeck", methods, Class.forName("java.util.Collection"), Class.forName("bakery.CustomerOrder") ));
    }

    @Test
    public void testAddCustomerOrderTakesNothingReturnsCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasReturnType("addCustomerOrder", methods, Class.forName("bakery.CustomerOrder") ));
    }

    @Test
    public void testGetFulfilableTakesListReturnsCollectionOfCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        String getFulfillable = getMethodGetFulfilable(); 
        assertTrue(StructuralHelper.methodHasGenericReturnType(getFulfillable, methods, Class.forName("java.util.Collection"), Class.forName("bakery.CustomerOrder") , Class.forName("java.util.List")));
    }

    @Test
    public void testGetInactiveCustomersWithStatusTakesCustomerOrderStatusReturnsCollectionOfCustomerOrder() throws ClassNotFoundException, NoSuchFieldException {
        assertTrue(StructuralHelper.methodHasGenericReturnType("getInactiveCustomersWithStatus", methods, Class.forName("java.util.Collection"), Class.forName("bakery.CustomerOrder") , Class.forName("bakery.CustomerOrder$CustomerOrderStatus")));
    }

}
