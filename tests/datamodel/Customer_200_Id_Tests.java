package datamodel;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link Customer} class: [200..299] Id-tests with tested
 * methods:
 * <pre>
 * - getId()
 * - setId(long id)
 * </pre>
 * @author sgra64
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Customer_200_Id_Tests {
    /*
     * Test case 200: test the value returned for getId() after object construction: should be null.
     */
    @Test @Order(200)
    void test200_ID_AfterConstructionWithNull(){
        Customer c = new Customer("Julius Dittrich");
        assertEquals(null, c.getId());
    }
    /*
     *  Test case 201: test value returned for getId() after first setId(x) is the set value: x.
     */
    @Test @Order(201)
    void test201_ID_AfterFirstAssignment(){
        Customer c = new Customer("Julius Dittrich");
        assertEquals(3460L, c.setId(3460L).getId());
    }
    /*
     * Test case 202: 202: test value returned for getId() after second invocation of setId(y) is still the first value: x.
     */
    @Test @Order(202)
    void test202_ID_AfterSecondAssignment(){
        Customer c = new Customer("Julius Dittrich");
        assertEquals(3422L, c.setId(3422L).setId(32352L).getId());
    }
    /*
     * Test cases 210: test setId(x) with minimum allowed value x and value x+1.
     */
    @Test @Order(210)
    void test210_ID_MininumValue(){
        Customer c1 = new Customer("Julius Dittrich");
        Customer c2 = new Customer("Marlow Mix");
        assertEquals(1L, c1.setId(1L).getId());
        assertEquals(2L, c2.setId(2L).getId());
    }
    /*
     * Test cases 211: test setId(x) with maximum allowed value x and value x-1.
     */
    @Test @Order(211)
    void test211_ID_MaximumValue(){
        Customer c1 = new Customer("Julius Dittrich");
        Customer c2 = new Customer("Marlow Mix");
        assertEquals(Long.MAX_VALUE, c1.setId(Long.MAX_VALUE).getId());
        assertEquals(Long.MAX_VALUE-1, c2.setId(Long.MAX_VALUE-1).getId());
    }
    /*
     * 220: Test case 220 setId(-1) illegal (exception) case that expects the method to throw an IllegalArgumentException with message: "invalid id (negative)"
     */
    @Test @Order(220)
    void test220_ID_NegativeValue(){
        Customer c = new Customer("Julius Dittrich");
        IllegalArgumentException thrown = 
            assertThrows(
                IllegalArgumentException.class, () -> {
                    c.setId(-1L);
            }
        );
        assertEquals(thrown.getMessage(), "invalid id (negative)");
    }
    /*
     * 220: Test case: setId(Long.MIN_VALUE) illegal (exception) case that expects the method to throw an IllegalArgumentException with message: "invalid id (negative)"
     */
    @Test @Order(221)
    void test221_ID_NegativeMinValue(){
        Customer c = new Customer("Julius Dittrich");
        IllegalArgumentException thrown = 
            assertThrows(
                IllegalArgumentException.class, () -> {
                    c.setId(Long.MIN_VALUE);
            }
        );
        assertEquals(thrown.getMessage(), "invalid id (negative)");
    }

}
