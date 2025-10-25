package objectiveethics.test.theoretic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import objectiveethics.commons.diagram.DataSeries;
import objectiveethics.commons.diagram.PlotPanel2D;
import objectiveethics.commons.diagram.PlotPanel2D.ScaleType;
import objectiveethics.values.logic.method.information.AssemblyIndexHeuristic;
import objectiveethics.values.logic.method.information.GZIPInfo;
import objectiveethics.values.structure.Information;

public class AssemblyMonomersComparisonTest {

    // Konstansok
    private static final int MAX_MONOMER_LENGTH = 16;
    private static final int NUM_BINS = 14; // ]0,1], ]1,2], ..., ]13,14]
    
    // Eredmények tárolása
    private final Map<Integer, int[]> assemblyIndexData = new ConcurrentHashMap<>();
    private final Map<Integer, int[]> infoData = new ConcurrentHashMap<>();
    
    // Mérők
    private final AssemblyIndexHeuristic assemblyIndex = new AssemblyIndexHeuristic();
    private final Information info = new GZIPInfo();

    public static void main(String[] args) {
        System.out.println("Starting Assembly Monomers Comparison Test...");
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            AssemblyMonomersComparisonTest test = new AssemblyMonomersComparisonTest();
            test.createAndShowGUI();
        });
    }
    
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Assembly Monomers Comparison Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Generating monomers and calculating assembly indices...");
        progressBar.setIndeterminate(true);
        progressBar.setBackground(Color.WHITE);
        frame.add(progressBar, BorderLayout.NORTH);
        
        // Create plot panels for side-by-side comparison
        PlotPanel2D assemblyIndexPlot = new PlotPanel2D();
        assemblyIndexPlot.setBackground(Color.WHITE);
        assemblyIndexPlot.setLegendVisible(true);
        assemblyIndexPlot.setYScaleType(ScaleType.LOGARITHMIC);
        
        PlotPanel2D infoPlot = new PlotPanel2D();
        infoPlot.setBackground(Color.WHITE);
        infoPlot.setLegendVisible(true);
        infoPlot.setYScaleType(ScaleType.LOGARITHMIC);
        
        
        // Create panel for side-by-side display
        JPanel plotPanel = new JPanel(new GridLayout(1, 2));
        plotPanel.setBackground(Color.WHITE);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(assemblyIndexPlot, BorderLayout.CENTER);
        leftPanel.setBorder(BorderFactory.createTitledBorder("AssemblyMeasure"));
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(infoPlot, BorderLayout.CENTER);
        rightPanel.setBorder(BorderFactory.createTitledBorder("InfoBasedAssembly"));
        
        plotPanel.add(leftPanel);
        plotPanel.add(rightPanel);
        
        frame.add(plotPanel, BorderLayout.CENTER);
        
        frame.setSize(1000, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Run tests in background thread
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                runTests();
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                if (!chunks.isEmpty()) {
                    progressBar.setString(chunks.get(chunks.size() - 1));
                }
            }
            
            @Override
            protected void done() {
                try {
                    // Create data series for plots
                    List<DataSeries> assemblyIndexSeries = new ArrayList<>();
                    List<DataSeries> infoSeries = new ArrayList<>();
                    
                    Color[] colors = {
                        new Color(160, 160, 160), // 1 monomer - light gray
                        new Color(80, 80, 80),    // 2 monomers - dark gray
                        Color.BLACK               // 3 monomers - black
                    };
                    String[] labels = {"1 monomer", "2 monomers", "3 monomers"};
                    
                    // Create data series for each alphabet size
                    for (int abcSize = 1; abcSize <= 3; abcSize++) {
                        int[] binCountsAi = assemblyIndexData.get(abcSize);
                        int[] binCountsI = infoData.get(abcSize);
                        
                        if (binCountsAi != null && binCountsI != null) {
                            List<Point2D> pointsA = new ArrayList<>();
                            List<Point2D> pointsIBA = new ArrayList<>();
                            
                            for (int bin = 0; bin < NUM_BINS; bin++) {
                                double binCenter = bin + 1; // Center of bin (bin + 1)
                                pointsA.add(new Point2D.Double(binCenter, binCountsAi[bin]));
                                pointsIBA.add(new Point2D.Double(binCenter, binCountsI[bin]));
                            }
                            
                            assemblyIndexSeries.add(new DataSeries(labels[abcSize - 1], pointsA, 
                                colors[abcSize - 1], false, true, 2, 7));
                            infoSeries.add(new DataSeries(labels[abcSize - 1], pointsIBA, 
                                colors[abcSize - 1], false, true, 2, 7));
                        }
                    }
                    
                    // Set data to plots
                    assemblyIndexPlot.setDataSeries(assemblyIndexSeries);
                    assemblyIndexPlot.setAxisLabels("Assembly Index", "Number of Monomers");
                    assemblyIndexPlot.setPlotTitle("Assembly index based");
                    
                    infoPlot.setDataSeries(infoSeries);
                    infoPlot.setAxisLabels("I_GZIP_", "Number of Monomers");
                    infoPlot.setPlotTitle("GZIP compression based");
                    
                    // Hide progress bar
                    progressBar.setVisible(false);
                    
                    // Print summary statistics
                    printSummaryStatistics();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error displaying results: " + e.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }

    private void runTests() {
        System.out.println("Starting monomer generation and assembly index calculation...");
        
        // Initialize bins for each alphabet size
        for (int abcSize = 1; abcSize <= 3; abcSize++) {
            assemblyIndexData.put(abcSize, new int[NUM_BINS]);
            infoData.put(abcSize, new int[NUM_BINS]);
        }
        
        // Generate all possible monomers for each alphabet size
        for (int abcSize = 1; abcSize <= 3; abcSize++) {
            System.out.println("Processing alphabet size: " + abcSize);
            char[] alphabet = createAlphabet(abcSize);
            
            AtomicInteger processedCount = new AtomicInteger(0);
            int totalMonomers = calculateTotalMonomers(abcSize);
            
            // Generate all monomers up to MAX_MONOMER_LENGTH
            generateAllMonomers(alphabet, "", abcSize, processedCount, totalMonomers);
        }
        
        System.out.println("All tests completed.");
    }
    
    /**
     * Létrehoz egy ábécét a megadott mérettel
     */
    private char[] createAlphabet(int size) {
        char[] alphabet = new char[size];
        for (int i = 0; i < size; i++) {
            alphabet[i] = (char)('a' + i);
        }
        return alphabet;
    }
    
    /**
     * Kiszámolja az összes lehetséges monimer számát egy adott ábécé mérethez
     */
    private int calculateTotalMonomers(int alphabetSize) {
        int total = 0;
        for (int length = 1; length <= MAX_MONOMER_LENGTH; length++) {
            total += Math.pow(alphabetSize, length);
        }
        return total;
    }
    
    /**
     * Generálja az összes lehetséges monimert rekurzívan
     */
    private void generateAllMonomers(char[] alphabet, String current, int abcSize, 
                                   AtomicInteger processedCount, int totalMonomers) {
        if (current.length() > 0) {
            // Process current monomer
            processMonomers(current, abcSize);
            
            int count = processedCount.incrementAndGet();
            if (count % 1000000 == 0) {
                System.out.println("Processed " + count + "/" + totalMonomers + 
                                 " monomers for alphabet size " + abcSize);
            }
        }
        
        // Generate longer monomers (if within limit)
        if (current.length() < MAX_MONOMER_LENGTH) {
            for (char c : alphabet) {
                generateAllMonomers(alphabet, current + c, abcSize, processedCount, totalMonomers);
            }
        }
    }
    
    /**
     * Feldolgoz egy monimert és besorolja a megfelelő bin-be
     */
    private void processMonomers(String monomer, int abcSize) {
        try {

            // Calculate assembly indices directly from string
            double assemblyA = assemblyIndex.value(monomer);
            double assemblyIBA = info.value(monomer);

            // Classify into bins
            int binA = getBinIndex(assemblyA);
            int binIBA = getBinIndex(assemblyIBA);
            
            // Update bin counts
            if (binA >= 0 && binA < NUM_BINS) {
                assemblyIndexData.get(abcSize)[binA]++;
            }
            if (binIBA >= 0 && binIBA < NUM_BINS) {
                infoData.get(abcSize)[binIBA]++;
            }
            
        } catch (Exception e) {
            System.err.println("Error processing monomer '" + monomer + "': " + e.getMessage());
        }
    }
    
    /**
     * Meghatározza, hogy egy assembly index melyik bin-be tartozik
     * Bins: ]0,1], ]1,2], ]2,3], ..., ]13,14]
     */
    private int getBinIndex(double assemblyIndex) {
        if (assemblyIndex <= 0) {
            return -1; // Invalid
        }
        
        int bin = (int) Math.floor(assemblyIndex);
        if (bin >= NUM_BINS) {
            return NUM_BINS - 1; // Cap at last bin
        }
        return bin;
    }
    
    /**
     * Kiírja az összefoglaló statisztikákat
     */
    private void printSummaryStatistics() {
        System.out.println("\n=== SUMMARY STATISTICS ===");
        
        for (int abcSize = 1; abcSize <= 3; abcSize++) {
            System.out.println("\nAlphabet size " + abcSize + ":");
            
            int[] binCountsA = assemblyIndexData.get(abcSize);
            int[] binCountsIBA = infoData.get(abcSize);
            
            System.out.println("AssemblyMeasure distribution:");
            for (int bin = 0; bin < NUM_BINS; bin++) {
                System.out.printf("  ]%d,%d]: %d monomers\n", bin, bin + 1, binCountsA[bin]);
            }
            
            System.out.println("InfoBasedAssembly distribution:");
            for (int bin = 0; bin < NUM_BINS; bin++) {
                System.out.printf("  ]%d,%d]: %d monomers\n", bin, bin + 1, binCountsIBA[bin]);
            }
            
            int totalA = 0, totalIBA = 0;
            for (int bin = 0; bin < NUM_BINS; bin++) {
                totalA += binCountsA[bin];
                totalIBA += binCountsIBA[bin];
            }
            System.out.println("Total monomers processed - AssemblyMeasure: " + totalA + 
                             ", InfoBasedAssembly: " + totalIBA);
        }
    }
}
