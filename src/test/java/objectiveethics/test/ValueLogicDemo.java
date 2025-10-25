package objectiveethics.test;

import objectiveethics.values.logic.ValueLogic;
import objectiveethics.values.structure.BaseValue;
import java.util.List;

/**
 * Demonstráció a ValueLogic új funkcionalitásához.
 * 
 * @author Volgyerdo Nonprofit Kft.
 */
public class ValueLogicDemo {
    
    public static void main(String[] args) {
        
        System.out.println("=== Összes BaseValue annotáció ===");
        List<BaseValue> allAnnotations = ValueLogic.getBaseValueAnnotations();
        allAnnotations.forEach(annotation -> {
            System.out.printf("Név: %s, Kategória: %s, Mozaikszó: %s%n", 
                annotation.name(), annotation.category(), annotation.acronym());
        });
        
        System.out.println("\n=== Information kategóriájú algoritmusok ===");
        List<BaseValue> infoAnnotations = ValueLogic.getBaseValueAnnotationsByCategory("information");
        infoAnnotations.forEach(annotation -> {
            System.out.printf("Név: %s, Mozaikszó: %s%n", 
                annotation.name(), annotation.acronym());
        });
        
        System.out.println("\n=== Assembly kategóriájú algoritmusok ===");
        List<BaseValue> assemblyAnnotations = ValueLogic.getBaseValueAnnotationsByCategory("assembly");
        assemblyAnnotations.forEach(annotation -> {
            System.out.printf("Név: %s, Mozaikszó: %s%n", 
                annotation.name(), annotation.acronym());
        });
        
        System.out.println("\n=== Entropy kategóriájú algoritmusok ===");
        List<BaseValue> entropyAnnotations = ValueLogic.getBaseValueAnnotationsByCategory("entropy");
        entropyAnnotations.forEach(annotation -> {
            System.out.printf("Név: %s, Mozaikszó: %s%n", 
                annotation.name(), annotation.acronym());
        });
        
        System.out.println("\n=== GZIP algoritmus keresése mozaikszó alapján ===");
        List<BaseValue> gzipAnnotations = ValueLogic.getBaseValueAnnotationsByAcronym("IGZIP");
        gzipAnnotations.forEach(annotation -> {
            System.out.printf("Név: %s, Leírás: %s%n", 
                annotation.name(), annotation.description());
            System.out.printf("Algoritmus: %s%n", annotation.algorithm());
        });
        
        System.out.println("\n=== Value-Annotation párok ===");
        List<ValueLogic.ValueAnnotationPair> pairs = ValueLogic.getValueAnnotationPairs();
        pairs.stream().limit(5).forEach(pair -> {
            System.out.printf("Osztály: %s, Annotáció név: %s%n", 
                pair.getClassName(), 
                pair.getAnnotation() != null ? pair.getAnnotation().name() : "nincs annotáció");
        });
        
        System.out.println("\n=== Kombinált szűrés: information kategória és ISH mozaikszó ===");
        List<BaseValue> filteredAnnotations = ValueLogic.getBaseValueAnnotationsFiltered("information", "ISH", null);
        filteredAnnotations.forEach(annotation -> {
            System.out.printf("Név: %s, Kategória: %s, Mozaikszó: %s%n", 
                annotation.name(), annotation.category(), annotation.acronym());
        });
    }
}
