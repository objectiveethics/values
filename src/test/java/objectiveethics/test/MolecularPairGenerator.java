package objectiveethics.test;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Program that selects 1000 artificial molecules and finds 1000 matching natural molecules
 * of similar size, then outputs the pairs to a text file.
 */
public class MolecularPairGenerator {
    
    private static final Logger LOGGER = Logger.getLogger(MolecularPairGenerator.class.getName());
    private static final String ARTIFICIAL_DIR = "/media/PROJECTS/Github/molecules/artificial";
    private static final String NATURAL_FILE = "/media/PROJECTS/Github/molecules/natural/natural.smi";
    private static final String OUTPUT_FILE = "/media/PROJECTS/Github/molecular_pairs.txt";
    private static final int TARGET_PAIRS = 1000;
    private static final double SIZE_TOLERANCE = 0.15; // 15% size tolerance
    
    // Data structure for molecules
    private static class Molecule {
        String structure;
        String name;
        int size;
        
        Molecule(String structure, String name) {
            this.structure = structure;
            this.name = name;
            this.size = structure.length();
        }
        
        @Override
        public String toString() {
            return structure + " | " + name + " (size: " + size + ")";
        }
    }
    
    // Natural molecules grouped by size ranges for efficient lookup
    private Map<Integer, List<Molecule>> naturalMoleculesBySize = new HashMap<>();
    
    public static void main(String[] args) {
        MolecularPairGenerator generator = new MolecularPairGenerator();
        generator.generateMolecularPairs();
    }
    
    public void generateMolecularPairs() {
        LOGGER.info("Starting Molecular Pair Generation");
        
        try {
            // Step 1: Load all natural molecules into memory
            LOGGER.info("Loading natural molecules from: " + NATURAL_FILE);
            loadNaturalMolecules();
            
            // Step 2: Sample artificial molecules
            LOGGER.info("Sampling artificial molecules from: " + ARTIFICIAL_DIR);
            List<Molecule> artificialMolecules = sampleArtificialMolecules();
            
            if (artificialMolecules.size() < TARGET_PAIRS) {
                LOGGER.warning("Only found " + artificialMolecules.size() + " artificial molecules, less than target " + TARGET_PAIRS);
            }
            
            // Step 3: Find matching natural molecules for each artificial molecule
            LOGGER.info("Finding matching natural molecules...");
            List<MolecularPair> pairs = findMatchingPairs(artificialMolecules);
            
            // Step 4: Write pairs to output file
            LOGGER.info("Writing " + pairs.size() + " molecular pairs to: " + OUTPUT_FILE);
            writePairsToFile(pairs);
            
            // Step 5: Log statistics
            logStatistics(pairs);
            
            LOGGER.info("Molecular pair generation completed successfully!");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during molecular pair generation", e);
        }
    }
    
    /**
     * Load all natural molecules into memory, grouped by size for efficient lookup
     */
    private void loadNaturalMolecules() throws IOException {
        Path naturalFile = Paths.get(NATURAL_FILE);
        if (!Files.exists(naturalFile)) {
            throw new FileNotFoundException("Natural molecules file not found: " + NATURAL_FILE);
        }
        
        long totalMolecules = 0;
        
        try (BufferedReader reader = Files.newBufferedReader(naturalFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    String[] parts = line.split("\\s+", 2);
                    if (parts.length >= 1) {
                        String structure = parts[0];
                        String name = parts.length > 1 ? parts[1] : "natural_" + totalMolecules;
                        
                        if (!structure.isEmpty()) {
                            Molecule molecule = new Molecule(structure, name);
                            naturalMoleculesBySize.computeIfAbsent(molecule.size, k -> new ArrayList<>()).add(molecule);
                            totalMolecules++;
                        }
                    }
                }
            }
        }
        
        LOGGER.info("Loaded " + totalMolecules + " natural molecules");
        LOGGER.info("Size distribution: " + naturalMoleculesBySize.size() + " different sizes");
        
        // Log size distribution statistics
        IntSummaryStatistics sizeStats = naturalMoleculesBySize.keySet().stream()
            .mapToInt(Integer::intValue)
            .summaryStatistics();
        LOGGER.info("Natural molecule sizes - Min: " + sizeStats.getMin() + 
                   ", Max: " + sizeStats.getMax() + 
                   ", Average: " + String.format("%.1f", sizeStats.getAverage()));
    }
    
    /**
     * Sample artificial molecules from all SMI files in the artificial directory
     */
    private List<Molecule> sampleArtificialMolecules() throws IOException {
        Path artificialDir = Paths.get(ARTIFICIAL_DIR);
        if (!Files.exists(artificialDir)) {
            throw new FileNotFoundException("Artificial molecules directory not found: " + ARTIFICIAL_DIR);
        }
        
        List<Path> smiFiles = Files.walk(artificialDir)
            .filter(path -> path.toString().toLowerCase().endsWith(".smi"))
            .collect(Collectors.toList());
            
        if (smiFiles.isEmpty()) {
            throw new FileNotFoundException("No SMI files found in: " + ARTIFICIAL_DIR);
        }
        
        LOGGER.info("Found " + smiFiles.size() + " SMI files in artificial directory");
        
        List<Molecule> artificialMolecules = new ArrayList<>();
        int moleculesPerFile = Math.max(1, TARGET_PAIRS / smiFiles.size());
        
        for (Path smiFile : smiFiles) {
            sampleMoleculesFromFile(smiFile, artificialMolecules, moleculesPerFile);
            if (artificialMolecules.size() >= TARGET_PAIRS) {
                break;
            }
        }
        
        // If we still need more molecules, do additional sampling
        while (artificialMolecules.size() < TARGET_PAIRS && !smiFiles.isEmpty()) {
            Path randomFile = smiFiles.get(ThreadLocalRandom.current().nextInt(smiFiles.size()));
            sampleMoleculesFromFile(randomFile, artificialMolecules, 10);
        }
        
        // Limit to target size and shuffle
        if (artificialMolecules.size() > TARGET_PAIRS) {
            Collections.shuffle(artificialMolecules, ThreadLocalRandom.current());
            artificialMolecules = artificialMolecules.subList(0, TARGET_PAIRS);
        }
        
        LOGGER.info("Sampled " + artificialMolecules.size() + " artificial molecules");
        return artificialMolecules;
    }
    
    /**
     * Sample molecules from a single SMI file
     */
    private void sampleMoleculesFromFile(Path smiFile, List<Molecule> molecules, int targetSamples) {
        try {
            long totalLines = countLines(smiFile);
            if (totalLines == 0) return;
            
            Set<Long> randomLineNumbers = generateRandomLineNumbers(totalLines, targetSamples);
            
            try (BufferedReader reader = Files.newBufferedReader(smiFile)) {
                String line;
                long currentLine = 0;
                
                while ((line = reader.readLine()) != null && !randomLineNumbers.isEmpty()) {
                    currentLine++;
                    
                    if (randomLineNumbers.contains(currentLine)) {
                        line = line.trim();
                        if (!line.isEmpty() && !line.startsWith("#")) {
                            String[] parts = line.split("\\s+", 2);
                            if (parts.length >= 1) {
                                String structure = parts[0];
                                String name = parts.length > 1 ? parts[1] : 
                                             "artificial_" + smiFile.getFileName() + "_" + currentLine;
                                
                                if (!structure.isEmpty()) {
                                    molecules.add(new Molecule(structure, name));
                                    randomLineNumbers.remove(currentLine);
                                }
                            }
                        }
                    }
                }
            }
            
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error sampling from file: " + smiFile, e);
        }
    }
    
    /**
     * Count lines in a file efficiently
     */
    private long countLines(Path filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            long lines = 0;
            while (reader.readLine() != null) {
                lines++;
            }
            return lines;
        }
    }
    
    /**
     * Generate random line numbers for sampling
     */
    private Set<Long> generateRandomLineNumbers(long totalLines, int targetSamples) {
        Set<Long> lineNumbers = new HashSet<>();
        Random random = ThreadLocalRandom.current();
        
        int actualSamples = Math.min(targetSamples, (int) totalLines);
        
        while (lineNumbers.size() < actualSamples) {
            long randomLine = random.nextLong(totalLines) + 1;
            lineNumbers.add(randomLine);
        }
        
        return lineNumbers;
    }
    
    /**
     * Find matching natural molecules for each artificial molecule
     */
    private List<MolecularPair> findMatchingPairs(List<Molecule> artificialMolecules) {
        List<MolecularPair> pairs = new ArrayList<>();
        int foundPairs = 0;
        int notFoundCount = 0;
        
        for (Molecule artificial : artificialMolecules) {
            Molecule natural = findMatchingNaturalMolecule(artificial);
            if (natural != null) {
                pairs.add(new MolecularPair(artificial, natural));
                foundPairs++;
            } else {
                notFoundCount++;
            }
            
            // Log progress every 100 molecules
            if ((foundPairs + notFoundCount) % 100 == 0) {
                LOGGER.info("Progress: " + (foundPairs + notFoundCount) + "/" + artificialMolecules.size() + 
                           " processed, " + foundPairs + " pairs found");
            }
        }
        
        LOGGER.info("Pair matching completed: " + foundPairs + " pairs found, " + 
                   notFoundCount + " artificial molecules without matches");
        
        return pairs;
    }
    
    /**
     * Find a matching natural molecule for the given artificial molecule
     */
    private Molecule findMatchingNaturalMolecule(Molecule artificial) {
        int targetSize = artificial.size;
        int minSize = (int) Math.round(targetSize * (1.0 - SIZE_TOLERANCE));
        int maxSize = (int) Math.round(targetSize * (1.0 + SIZE_TOLERANCE));
        
        // Try exact size first
        List<Molecule> candidates = naturalMoleculesBySize.get(targetSize);
        if (candidates != null && !candidates.isEmpty()) {
            return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        }
        
        // Try nearby sizes
        for (int size = targetSize - 1; size >= minSize; size--) {
            candidates = naturalMoleculesBySize.get(size);
            if (candidates != null && !candidates.isEmpty()) {
                return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
            }
        }
        
        for (int size = targetSize + 1; size <= maxSize; size++) {
            candidates = naturalMoleculesBySize.get(size);
            if (candidates != null && !candidates.isEmpty()) {
                return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
            }
        }
        
        return null; // No matching natural molecule found
    }
    
    /**
     * Write molecular pairs to output file
     */
    private void writePairsToFile(List<MolecularPair> pairs) throws IOException {
        Path outputPath = Paths.get(OUTPUT_FILE);
        
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            writer.write("# Molecular Pairs: [artificial] [natural]\n");
            writer.write("# Generated: " + new Date() + "\n");
            writer.write("# Total pairs: " + pairs.size() + "\n");
            writer.write("#\n");
            
            for (MolecularPair pair : pairs) {
                writer.write(pair.artificial.structure + " " + pair.natural.structure + "\n");
            }
        }
        
        LOGGER.info("Successfully wrote " + pairs.size() + " molecular pairs to: " + OUTPUT_FILE);
    }
    
    /**
     * Log statistics about the generated pairs
     */
    private void logStatistics(List<MolecularPair> pairs) {
        if (pairs.isEmpty()) {
            LOGGER.warning("No pairs generated!");
            return;
        }
        
        // Size difference statistics
        List<Integer> sizeDifferences = pairs.stream()
            .mapToInt(pair -> Math.abs(pair.artificial.size - pair.natural.size))
            .boxed()
            .collect(Collectors.toList());
            
        IntSummaryStatistics diffStats = sizeDifferences.stream().mapToInt(Integer::intValue).summaryStatistics();
        
        // Artificial molecule size statistics
        IntSummaryStatistics artificialSizeStats = pairs.stream()
            .mapToInt(pair -> pair.artificial.size)
            .summaryStatistics();
            
        // Natural molecule size statistics
        IntSummaryStatistics naturalSizeStats = pairs.stream()
            .mapToInt(pair -> pair.natural.size)
            .summaryStatistics();
        
        LOGGER.info("=== PAIR GENERATION STATISTICS ===");
        LOGGER.info("Total pairs generated: " + pairs.size());
        LOGGER.info("Artificial molecule sizes - Min: " + artificialSizeStats.getMin() + 
                   ", Max: " + artificialSizeStats.getMax() + 
                   ", Average: " + String.format("%.1f", artificialSizeStats.getAverage()));
        LOGGER.info("Natural molecule sizes - Min: " + naturalSizeStats.getMin() + 
                   ", Max: " + naturalSizeStats.getMax() + 
                   ", Average: " + String.format("%.1f", naturalSizeStats.getAverage()));
        LOGGER.info("Size differences - Min: " + diffStats.getMin() + 
                   ", Max: " + diffStats.getMax() + 
                   ", Average: " + String.format("%.1f", diffStats.getAverage()));
        
        // Count perfect matches (same size)
        long perfectMatches = sizeDifferences.stream().mapToInt(Integer::intValue).filter(diff -> diff == 0).count();
        LOGGER.info("Perfect size matches: " + perfectMatches + " (" + 
                   String.format("%.1f", 100.0 * perfectMatches / pairs.size()) + "%)");
    }
    
    /**
     * Data structure to hold a pair of molecules
     */
    private static class MolecularPair {
        final Molecule artificial;
        final Molecule natural;
        
        MolecularPair(Molecule artificial, Molecule natural) {
            this.artificial = artificial;
            this.natural = natural;
        }
    }
}
