/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objectiveethics.values.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.lang.reflect.Modifier;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.net.JarURLConnection;

import objectiveethics.values.structure.Value;
import objectiveethics.values.structure.BaseValue;

/**
 *
 * @author zsolt
 */
public class ValueLogic {
    
    /**
     * Static cache that maps BaseValue IDs to Value classes.
     * Initialized once at class loading time for optimal performance.
     * Thread-safe because we create new instances on each call.
     */
    private static final java.util.Map<Long, Class<?>> VALUE_CLASS_CACHE = initializeValueClassCache();
    
    /**
     * Initializes the static value class cache by loading all Value classes
     * and mapping their BaseValue.id() to the corresponding Value class objects.
     */
    private static java.util.Map<Long, Class<?>> initializeValueClassCache() {
        java.util.Map<Long, Class<?>> cache = new java.util.HashMap<>();
        List<Class<?>> annotatedClasses = findAnnotatedClasses();
        
        for (Class<?> clazz : annotatedClasses) {
            BaseValue annotation = clazz.getAnnotation(BaseValue.class);
            if (annotation != null) {
                cache.put(annotation.id(), clazz);
            }
        }
        
        return cache;
    }


    /**
     * Finds and returns all classes annotated with BaseValue 
     * in the objectiveethics.values.logic.method package and its subdirectories.
     */
    private static List<Class<?>> findAnnotatedClasses() {
        List<Class<?>> annotatedClasses = new ArrayList<>();
        String packageName = "objectiveethics.values.logic.method";
        String path = packageName.replace('.', '/');
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();
                if ("file".equals(protocol)) {
                    File directory = new File(resource.toURI());
                    if (directory.exists()) {
                        findClassesInDirectory(directory, packageName, annotatedClasses);
                    }
                } else if ("jar".equals(protocol)) {
                    try {
                        JarURLConnection jarConn = (JarURLConnection) resource.openConnection();
                        try (JarFile jarFile = jarConn.getJarFile()) {
                            scanJar(jarFile, path, packageName, annotatedClasses);
                        }
                    } catch (ClassCastException e) {
                        // Fallback: parse jar path manually
                        String file = resource.getFile(); // e.g. file:/path/values-1.0-SNAPSHOT.jar!/volgyerdo/value/logic/method
                        int sep = file.indexOf(".jar!");
                        if (sep != -1) {
                            String jarPath = file.substring(0, sep + 4);
                            if (jarPath.startsWith("file:")) {
                                jarPath = jarPath.substring(5);
                            }
                            jarPath = jarPath.replace("%20", " ");
                            try (JarFile jar = new JarFile(jarPath)) {
                                scanJar(jar, path, packageName, annotatedClasses);
                            } catch (Exception ignore) { }
                        }
                    }
                } else {
                    // Unknown protocol, attempt manual jar parsing if contains .jar!
                    String file = resource.toString();
                    int sep = file.indexOf(".jar!");
                    if (sep != -1) {
                        int start = file.indexOf("file:");
                        if (start != -1) {
                            String jarPath = file.substring(start + 5, sep + 4).replace("%20", " ");
                            try (JarFile jar = new JarFile(jarPath)) {
                                scanJar(jar, path, packageName, annotatedClasses);
                            } catch (Exception ignore) { }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return annotatedClasses;
    }

    private static void scanJar(JarFile jarFile, String path, String packageName, List<Class<?>> annotatedClasses) {
        jarFile.stream()
            .filter(e -> !e.isDirectory())
            .map(JarEntry::getName)
            .filter(name -> name.startsWith(path) && name.endsWith(".class"))
            .forEach(name -> {
                String className = name.replace('/', '.').substring(0, name.length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(BaseValue.class) &&
                            Value.class.isAssignableFrom(clazz) &&
                            !Modifier.isAbstract(clazz.getModifiers()) &&
                            !clazz.isInterface()) {
                        annotatedClasses.add(clazz);
                    }
                } catch (Throwable ex) {
                    // ignore
                }
            });
    }
    
    /**
     * Recursively searches for annotated classes in a directory.
     */
    private static void findClassesInDirectory(File directory, String packageName, List<Class<?>> annotatedClasses) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findClassesInDirectory(file, packageName + "." + file.getName(), annotatedClasses);
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(BaseValue.class) && 
                            Value.class.isAssignableFrom(clazz) &&
                            !Modifier.isAbstract(clazz.getModifiers()) &&
                            !clazz.isInterface()) {
                            annotatedClasses.add(clazz);
                        }
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        // Ignore classes that can't be loaded
                    }
                }
            }
        }
    }
    
    /**
     * Returns all BaseValue annotation objects from annotated classes.
     * 
     * @return list of BaseValue annotations
     */
    public static List<BaseValue> getBaseValueAnnotations() {
        return VALUE_CLASS_CACHE.values().stream()
                .map(clazz -> clazz.getAnnotation(BaseValue.class))
                .filter(annotation -> annotation != null)
                .collect(Collectors.toList());
    }
    
    /**
     * Returns instances of all Value classes from the cache.
     * Creates new instances for each call to ensure thread safety.
     * 
     * @return list of Value instances
     */
    public static List<Value> values() {
        return VALUE_CLASS_CACHE.values().stream()
                .map(clazz -> {
                    try {
                        return (Value) clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to instantiate Value class: " + clazz.getName(), e);
                    }
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Filtered list of BaseValue annotations by category.
     * 
     * @param category the searched category (null means no filter)
     * @return list of filtered BaseValue annotations
     */
    public static List<BaseValue> getBaseValueAnnotationsByCategory(String category) {
        return VALUE_CLASS_CACHE.values().stream()
                .map(clazz -> clazz.getAnnotation(BaseValue.class))
                .filter(annotation -> annotation != null)
                .filter(annotation -> category == null || category.equals(annotation.category()))
                .collect(Collectors.toList());
    }
    
    /**
     * Filtered list of BaseValue annotations by acronym.
     * 
     * @param acronym the searched acronym (null means no filter)
     * @return list of filtered BaseValue annotations
     */
    public static List<BaseValue> getBaseValueAnnotationsByAcronym(String acronym) {
        return VALUE_CLASS_CACHE.values().stream()
                .map(clazz -> clazz.getAnnotation(BaseValue.class))
                .filter(annotation -> annotation != null)
                .filter(annotation -> acronym == null || acronym.equals(annotation.acronym()))
                .collect(Collectors.toList());
    }
    
    /**
     * Filtered list of BaseValue annotations by class name.
     * 
     * @param className the searched class name (null means no filter)
     * @return list of filtered BaseValue annotations
     */
    public static List<BaseValue> getBaseValueAnnotationsByClassName(String className) {
        return VALUE_CLASS_CACHE.values().stream()
                .filter(clazz -> className == null || clazz.getSimpleName().equals(className))
                .map(clazz -> clazz.getAnnotation(BaseValue.class))
                .filter(annotation -> annotation != null)
                .collect(Collectors.toList());
    }
    
    /**
     * Combined filter method by category, acronym and class name.
     * 
     * @param category the searched category (null means no filter)
     * @param acronym the searched acronym (null means no filter)
     * @param className the searched class name (null means no filter)
     * @return list of filtered BaseValue annotations
     */
    public static List<BaseValue> getBaseValueAnnotationsFiltered(String category, String acronym, String className) {
        return VALUE_CLASS_CACHE.values().stream()
                .filter(clazz -> className == null || clazz.getSimpleName().equals(className))
                .map(clazz -> clazz.getAnnotation(BaseValue.class))
                .filter(annotation -> annotation != null)
                .filter(annotation -> category == null || category.equals(annotation.category()))
                .filter(annotation -> acronym == null || acronym.equals(annotation.acronym()))
                .collect(Collectors.toList());
    }
    
    /**
     * Returns all Value objects with their corresponding BaseValue annotations.
     * 
     * @return list of ValueAnnotationPair objects
     */
    public static List<ValueAnnotationPair> getValueAnnotationPairs() {
        return VALUE_CLASS_CACHE.values().stream()
                .map(clazz -> {
                    try {
                        Value value = (Value) clazz.getDeclaredConstructor().newInstance();
                        BaseValue annotation = clazz.getAnnotation(BaseValue.class);
                        return new ValueAnnotationPair(value, annotation);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to instantiate Value class: " + clazz.getName(), e);
                    }
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Calculates a value using the specified BaseValue ID and input object.
     * Uses a static cache for optimal performance - O(1) lookup time.
     * 
     * @param valueId the BaseValue.id() to find the corresponding Value implementation
     * @param object the input object to calculate the value for
     * @return the calculated double value
     * @throws IllegalArgumentException if no Value implementation found for the given ID
     * @throws RuntimeException if Value instantiation or calculation fails
     */
    public static double calculateValueById(long valueId, Object object) {
        if (object == null) {
            return 0.0;
        }
        
        // Get the class from cache and create a new instance for thread safety
        Class<?> valueClass = VALUE_CLASS_CACHE.get(valueId);
        
        if (valueClass == null) {
            throw new IllegalArgumentException("No Value implementation found for BaseValue.id() = " + valueId);
        }
        
        Value valueInstance;
        try {
            valueInstance = (Value) valueClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate Value class for BaseValue.id() = " + valueId, e);
        }
        
        try {
            // Call the appropriate value() method based on object type
            if (object instanceof byte[]) {
                return valueInstance.value((byte[]) object);
            } else if (object instanceof Collection<?>) {
                return valueInstance.value((Collection<?>) object);
            } else if (object instanceof String) {
                return valueInstance.value((String) object);
            } else if (object instanceof boolean[]) {
                return valueInstance.value((boolean[]) object);
            } else if (object instanceof short[]) {
                return valueInstance.value((short[]) object);
            } else if (object instanceof int[]) {
                return valueInstance.value((int[]) object);
            } else if (object instanceof float[]) {
                return valueInstance.value((float[]) object);
            } else if (object instanceof double[]) {
                return valueInstance.value((double[]) object);
            } else if (object instanceof char[]) {
                return valueInstance.value((char[]) object);
            } else if (object instanceof Character[]) {
                return valueInstance.value((Character[]) object);
            } else if (object instanceof Byte[]) {
                return valueInstance.value((Byte[]) object);
            } else if (object instanceof Short[]) {
                return valueInstance.value((Short[]) object);
            } else if (object instanceof Integer[]) {
                return valueInstance.value((Integer[]) object);
            } else if (object instanceof Long[]) {
                return valueInstance.value((Long[]) object);
            } else if (object instanceof String[]) {
                return valueInstance.value((String[]) object);
            } else {
                // Default: use the generic Object method which converts to string
                return valueInstance.value(object);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate value for BaseValue.id() = " + valueId + 
                                     " with object type " + object.getClass().getSimpleName(), e);
        }
    }

    /**
     * Gets a new Value instance by its BaseValue ID.
     * Uses the static class cache for O(1) lookup time and creates a new instance for thread safety.
     * 
     * @param valueId the BaseValue.id() to find the corresponding Value implementation
     * @return a new Value instance, or null if not found
     */
    public static Value getValueById(long valueId) {
        Class<?> valueClass = VALUE_CLASS_CACHE.get(valueId);
        if (valueClass == null) {
            return null;
        }
        
        try {
            return (Value) valueClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate Value class for BaseValue.id() = " + valueId, e);
        }
    }

    /**
     * Helper class for pairing Value objects with BaseValue annotations.
     */
    public static class ValueAnnotationPair {
        private final Value value;
        private final BaseValue annotation;
        
        public ValueAnnotationPair(Value value, BaseValue annotation) {
            this.value = value;
            this.annotation = annotation;
        }
        
        public Value getValue() {
            return value;
        }
        
        public BaseValue getAnnotation() {
            return annotation;
        }
        
        public String getClassName() {
            return value.getClass().getSimpleName();
        }
    }
}
