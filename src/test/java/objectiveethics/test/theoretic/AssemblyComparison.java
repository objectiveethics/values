/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package objectiveethics.test.theoretic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import objectiveethics.commons.diagram.DataSeries;
import objectiveethics.commons.diagram.PlotPanel2D;
import objectiveethics.commons.math.fast.FastLog;

/**
 *
 * @author zsolt
 */
public class AssemblyComparison extends javax.swing.JFrame {

    private static DecimalFormat fiFormat = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.ENGLISH));
    private static DecimalFormat resultFormat = new DecimalFormat("0.####");

    /**
     * Creates new form AssemblyComparison
     */
    public AssemblyComparison() {
        initComponents();
        
        // Set logarithmic scale for Y-axis on both plots
        plotFixPhi.setYScaleType(PlotPanel2D.ScaleType.LOGARITHMIC);
        PlotChangingPhi.setYScaleType(PlotPanel2D.ScaleType.LOGARITHMIC);
        
        progress.setVisible(true);
        progress.setStringPainted(false);
        progress.setIndeterminate(true);
        calcWithFixPhi();
        calcWithChangingPhi();
        calcWithFixPhi2();
        calcWithChangingPhi2();
        progress.setVisible(false);
    }

    private void calcWithFixPhi() {
        try {
            List<DataSeries> dataSeriesList = new ArrayList<>();
            for (double phi = 0.0; phi <= 1.0; phi += 0.2) {
                float grayLevel = (float) (0.9 - phi * 0.7); // Inverted gray scale from 0.9 to 0.2
                dataSeriesList.add(createSeriesFixPhi(phi, new Color(grayLevel, grayLevel, grayLevel)));
            }
            printData(dataSeriesList, "Fix phi:");
            SwingUtilities.invokeLater(() -> {
                plotFixPhi.setDataSeries(dataSeriesList);
                plotFixPhi.setAxisLabels("Iteration", "Assembly");
                plotFixPhi.setPlotTitle("Assembly with Fixed Phi");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calcWithChangingPhi() {
        try {
            List<DataSeries> dataSeriesList = new ArrayList<>();
            for (double phi0 = 0.0; phi0 <= 1.0; phi0 += 0.2) {
                float grayLevel = (float) (0.9 - phi0 * 0.7); // Inverted gray scale from 0.9 to 0.2
                dataSeriesList.add(createSeriesChangingPhi(phi0, new Color(grayLevel, grayLevel, grayLevel)));
            }
            printData(dataSeriesList, "Changing phi:");
            SwingUtilities.invokeLater(() -> {
                PlotChangingPhi.setDataSeries(dataSeriesList);
                PlotChangingPhi.setAxisLabels("Iteration", "Assembly");
                PlotChangingPhi.setPlotTitle("Assembly with Changing Phi");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calcWithFixPhi2() {
        try {
            List<DataSeries> dataSeriesList = new ArrayList<>();
            for (double phi = 0.0; phi <= 1.0; phi += 0.2) {
                float grayLevel = (float) (0.9 - phi * 0.7); // Inverted gray scale from 0.9 to 0.2
                dataSeriesList.add(createSeriesFixPhi2(phi, new Color(grayLevel, grayLevel, grayLevel)));
            }
            printData(dataSeriesList, "Fix phi:");
            SwingUtilities.invokeLater(() -> {
                plotFixPhi2.setDataSeries(dataSeriesList);
                plotFixPhi2.setAxisLabels("Iteration", "A[a]");
                plotFixPhi2.setPlotTitle("A[a] with Fixed Phi");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calcWithChangingPhi2() {
        try {
            List<DataSeries> dataSeriesList = new ArrayList<>();
            for (double phi0 = 0.0; phi0 <= 1.0; phi0 += 0.2) {
                float grayLevel = (float) (0.9 - phi0 * 0.7); // Inverted gray scale from 0.9 to 0.2
                dataSeriesList.add(createSeriesChangingPhi2(phi0, new Color(grayLevel, grayLevel, grayLevel)));
            }
            printData(dataSeriesList, "Changing phi:");
            SwingUtilities.invokeLater(() -> {
                PlotChangingPhi2.setDataSeries(dataSeriesList);
                PlotChangingPhi2.setAxisLabels("Iteration", "A[a]");
                PlotChangingPhi2.setPlotTitle("A[a] with Changing Phi");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void printData(List<DataSeries> series, String title){
        System.out.println(title);
            for (int i = 0; i < 50; i++) {
                for (int j = 0; j < 6; j++) {
                    System.out.print(resultFormat.format(series.get(j).getPoints().get(i).getY()) + ";");
                }
                System.out.println();
            }
    }

    private DataSeries createSeriesFixPhi(double phi, Color color) {
        double[] a = new double[102];
        a[1] = 10e12;
        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            double[] a1 = new double[102];
            points.add(new Point2D.Double(i, assembly(a)));
            for (int j = 1; j < 102; j++) {
                a1[j] = (1 - phi) * a[j] + phi * a[j - 1];
            }
            a = a1;
        }
        return new DataSeries("Φ=" + fiFormat.format(phi),
                points, color, true, false, 3, 6);
    }

    private DataSeries createSeriesChangingPhi(double phi0, Color color) {
        double[] a = new double[102];
        a[1] = 10e12;
        List<Point2D> points = new ArrayList<>();
        double phi = phi0;
        for (int i = 0; i < 50; i++) {
            double[] a1 = new double[102];
            points.add(new Point2D.Double(i, assembly(a)));
            for (int j = 1; j < 102; j++) {
                phi = phi0 * Math.pow(0.33, j - 1);
                a1[j] = (1 - phi) * a[j] + phi * a[j - 1];
            }
            a = a1;

        }
        return new DataSeries("Φ₀=" + fiFormat.format(phi0),
                points, color, true, false, 3, 6);
    }

    private DataSeries createSeriesFixPhi2(double phi, Color color) {
        double[] a = new double[102];
        a[1] = 10e12;
        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            double[] a1 = new double[102];
            points.add(new Point2D.Double(i, iba(a)));
            for (int j = 1; j < 102; j++) {
                a1[j] = (1 - phi) * a[j] + phi * a[j - 1];
            }
            a = a1;
        }
        return new DataSeries("Φ=" + fiFormat.format(phi),
                points, color, true, false, 3, 6);
    }

    private DataSeries createSeriesChangingPhi2(double phi0, Color color) {
        double[] a = new double[102];
        a[1] = 10e12;
        List<Point2D> points = new ArrayList<>();
        double phi = phi0;
        for (int i = 0; i < 50; i++) {
            double[] a1 = new double[102];
            points.add(new Point2D.Double(i, iba(a)));
            for (int j = 1; j < 102; j++) {
                phi = phi0 * Math.pow(0.33, j - 1);
                a1[j] = (1 - phi) * a[j] + phi * a[j - 1];
            }
            a = a1;

        }
        return new DataSeries("Φ₀=" + fiFormat.format(phi0),
                points, color, true, false, 3, 6);
    }

    private double assembly(double[] a) {
        double assembly = 0;
        double total = 0;
        for (int j = 1; j < 102; j++) {
            if (a[j] > 1) {
                total += a[j];
            }
        }
        for (int j = 1; j < 102; j++) {
            if (a[j] > 1) {
                assembly += Math.exp(j) * (a[j] - 1);
            }
        }
        return assembly / total;
    }

    private double iba(double[] a) {
        /*double assembly = 0;
        double total = 0;
        for (int j = 1; j < 102; j++) {
            if (a[j] > 1) {
                total += 1;
            }
        }
        for (int j = 1; j < 102; j++) {
            if (a[j] > 1) {
                assembly += j + Math.log(a[j]);
            }
        }
        return assembly / total;*/

        double assembly = 0;
        for (int j = 1; j < 102; j++) {
            if (a[j] > 1) {
                assembly = Math.max(assembly, j * FastLog.log2(a[j]));
            }
        }
        return assembly;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progress = new javax.swing.JProgressBar();
        tabs = new javax.swing.JTabbedPane();
        plotFixPhi = new objectiveethics.commons.diagram.PlotPanel2D();
        plotFixPhi2 = new objectiveethics.commons.diagram.PlotPanel2D();
        PlotChangingPhi = new objectiveethics.commons.diagram.PlotPanel2D();
        PlotChangingPhi2 = new objectiveethics.commons.diagram.PlotPanel2D();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        progress.setStringPainted(true);
        getContentPane().add(progress, java.awt.BorderLayout.NORTH);

        // Create panels for each tab to hold two plots
        javax.swing.JPanel fixPhiPanel = new javax.swing.JPanel(new java.awt.GridLayout(1, 2));
        fixPhiPanel.add(plotFixPhi);
        fixPhiPanel.add(plotFixPhi2);
        
        javax.swing.JPanel changingPhiPanel = new javax.swing.JPanel(new java.awt.GridLayout(1, 2));
        changingPhiPanel.add(PlotChangingPhi);
        changingPhiPanel.add(PlotChangingPhi2);

        tabs.addTab("Fix Phi", fixPhiPanel);
        tabs.addTab("Changing Phi", changingPhiPanel);

        getContentPane().add(tabs, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AssemblyComparison.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AssemblyComparison.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AssemblyComparison.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AssemblyComparison.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new AssemblyComparison();
                frame.setTitle("Assembly Plot");
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setPreferredSize(new Dimension(1000, 500));
                frame.setMinimumSize(new Dimension(1000, 500));
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private objectiveethics.commons.diagram.PlotPanel2D PlotChangingPhi;
    private objectiveethics.commons.diagram.PlotPanel2D PlotChangingPhi2;
    private objectiveethics.commons.diagram.PlotPanel2D plotFixPhi;
    private objectiveethics.commons.diagram.PlotPanel2D plotFixPhi2;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables
}
