package objectiveethics.values.logic;

import java.util.Arrays;
import java.util.List;

/**
 * Test class for ValueLogic.calculateValueById() method.
 * 
 * @author Volgyerdo Nonprofit Kft.
 */
public class ValueLogicCalculateTest {

    public static void main(String[] args) {
        System.out.println("=== Testing ValueLogic.calculateValueById() ===");
        
        try {
            // Test 1: Valid ID with byte array
            System.out.println("\n1. Testing with byte array:");
            byte[] testBytes = {1, 2, 3, 4, 5};
            long valueId = 2; // AssemblyIndexApprox
            double result1 = ValueLogic.calculateValueById(valueId, testBytes);
            System.out.println("Result for byte array [1,2,3,4,5]: " + result1);
            
            // Test 2: Valid ID with string
            System.out.println("\n2. Testing with string:");
            String testString = "Hello World";
            double result2 = ValueLogic.calculateValueById(valueId, testString);
            System.out.println("Result for string 'Hello World': " + result2);
            
            // Test 3: Valid ID with collection
            System.out.println("\n3. Testing with collection:");
            List<String> testList = Arrays.asList("a", "b", "c", "d", "e");
            double result3 = ValueLogic.calculateValueById(valueId, testList);
            System.out.println("Result for collection [a,b,c,d,e]: " + result3);
            
            // Test 4: Null object
            System.out.println("\n4. Testing with null object:");
            double result4 = ValueLogic.calculateValueById(valueId, null);
            System.out.println("Result for null object: " + result4);
            
            // Test 5: Different data types
            System.out.println("\n5. Testing with different data types:");
            System.out.println("int array [1,2,3]: " + ValueLogic.calculateValueById(valueId, new int[]{1, 2, 3}));
            System.out.println("char array ['a','b','c']: " + ValueLogic.calculateValueById(valueId, new char[]{'a', 'b', 'c'}));
            System.out.println("boolean array [true,false,true]: " + ValueLogic.calculateValueById(valueId, new boolean[]{true, false, true}));
            System.out.println("Integer object 42: " + ValueLogic.calculateValueById(valueId, 42));
            
            // Test 6: Empty arrays
            System.out.println("\n6. Testing with empty arrays:");
            System.out.println("Empty byte array: " + ValueLogic.calculateValueById(valueId, new byte[0]));
            System.out.println("Empty string: " + ValueLogic.calculateValueById(valueId, ""));
            System.out.println("Empty collection: " + ValueLogic.calculateValueById(valueId, Arrays.asList()));
            
            // Test 7: Invalid ID (should throw exception)
            System.out.println("\n7. Testing with invalid ID:");
            try {
                ValueLogic.calculateValueById(99999, "test");
                System.out.println("ERROR: Should have thrown exception for invalid ID!");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Correctly threw exception: " + e.getMessage());
            }
            
            System.out.println("\n=== All tests completed successfully! ===");
            
        } catch (Exception e) {
            System.err.println("ERROR during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
