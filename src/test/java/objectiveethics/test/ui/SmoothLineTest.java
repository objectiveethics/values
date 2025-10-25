package objectiveethics.test.ui;

import javax.swing.*;

import objectiveethics.commons.diagram.DataSeries;
import objectiveethics.commons.diagram.PlotPanel2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;

/**
 * Test class specifically for demonstrating smooth line rendering
 * This test creates curved and angled lines to showcase antialiasing improvements
 *
 * @author zsolt
 */
public class SmoothLineTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("PlotPanel2D - Smooth Line Test");
            PlotPanel2D plotPanel = new PlotPanel2D();
            plotPanel.setPreferredSize(new Dimension(1000, 700));
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(plotPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Create test data with curves and angles to test smoothness
            List<DataSeries> dataSeriesList = new ArrayList<>();
            
            // Sine wave - should show smooth curves
            List<Point2D> sineWave = new ArrayList<>();
            for (double x = 0; x <= 4 * Math.PI; x += 0.1) {
                sineWave.add(new Point2D.Double(x, Math.sin(x)));
            }
            dataSeriesList.add(new DataSeries("Sine Wave (thick)", sineWave, Color.RED, true, false, 3, 6));
            
            // Cosine wave with bullets
            List<Point2D> cosineWave = new ArrayList<>();
            for (double x = 0; x <= 4 * Math.PI; x += 0.2) {
                cosineWave.add(new Point2D.Double(x, Math.cos(x) + 2));
            }
            dataSeriesList.add(new DataSeries("Cosine Wave (with bullets)", cosineWave, Color.BLUE, true, true, 2, 8));
            
            // Zigzag pattern - should show smooth angles
            List<Point2D> zigzag = new ArrayList<>();
            for (double x = 0; x <= 4 * Math.PI; x += 0.3) {
                double y = (x % 1.0 < 0.5) ? -2 : -1;
                zigzag.add(new Point2D.Double(x, y));
            }
            dataSeriesList.add(new DataSeries("Zigzag Pattern (very thick)", zigzag, Color.GREEN, true, true, 5, 4));
            
            // Spiral-like curve
            List<Point2D> spiral = new ArrayList<>();
            for (double t = 0; t <= 3 * Math.PI; t += 0.05) {
                double x = t;
                double y = 0.5 * t * Math.sin(2 * t) - 3;
                spiral.add(new Point2D.Double(x, y));
            }
            dataSeriesList.add(new DataSeries("Spiral Curve (medium)", spiral, Color.ORANGE, true, false, 2, 6));
            
            // Sharp angles test
            List<Point2D> sharpAngles = new ArrayList<>();
            sharpAngles.add(new Point2D.Double(1, 1.5));
            sharpAngles.add(new Point2D.Double(3, 1.5));
            sharpAngles.add(new Point2D.Double(3, 2.5));
            sharpAngles.add(new Point2D.Double(5, 2.5));
            sharpAngles.add(new Point2D.Double(5, 1.5));
            sharpAngles.add(new Point2D.Double(7, 1.5));
            dataSeriesList.add(new DataSeries("Sharp Angles (ultra thick)", sharpAngles, Color.MAGENTA, true, true, 6, 10));
            
            plotPanel.setDataSeries(dataSeriesList);
            
            // Set axis labels to demonstrate the new feature
            plotPanel.setAxisLabels("Time (radians)", "Amplitude");
        });
    }
}
