package objectiveethics.test;

import java.util.ArrayList;
import java.util.List;
import objectiveethics.values.logic.ValueLogic;
import objectiveethics.values.structure.Information;
import objectiveethics.values.structure.Value;

/**
 * Test class that checks what value each Information interface implementation
 * returns for a single character input.
 * 
 * @author Volgyerdo Nonprofit Kft.
 */
public class InformationSingleCharacterTest {
    
    public static void main(String[] args) {
        System.out.println("=== Information Test for '1', '11', '111' ===");
        System.out.println("Testing all Information implementations with strings '1', '11', '111'");
        System.out.println();
        
        // Get all Value implementations
        List<Value> allValues = ValueLogic.values();
        
        // Filter only Information implementations
        List<Information> informationValues = new ArrayList<>();
        for (Value value : allValues) {
            if (value instanceof Information) {
                informationValues.add((Information) value);
            }
        }
        
        System.out.println("Found " + informationValues.size() + " Information implementations:");
        System.out.println();
        
        // Test data
        String[] testStrings = {"1", "11", "111"};
        
        System.out.printf("%-50s | %12s | %12s | %12s%n", 
                         "Class Name", "'1'", "'11'", "'111'");
        System.out.println("-".repeat(95));
        
        // Test each Information implementation
        for (Information info : informationValues) {
            try {
                System.out.printf("%-50s", info.getClass().getSimpleName());
                
                for (String testString : testStrings) {
                    byte[] testBytes = testString.getBytes("UTF-8");
                    double value = info.value(testBytes);
                    System.out.printf(" | %12.6f", value);
                }
                System.out.println();
                
            } catch (Exception e) {
                System.out.printf("%-50s | ERROR: %s%n", 
                                 info.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
