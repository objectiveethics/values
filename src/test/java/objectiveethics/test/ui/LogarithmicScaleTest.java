/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objectiveethics.test.ui;

import javax.swing.*;

import objectiveethics.commons.diagram.DataSeries;
import objectiveethics.commons.diagram.PlotPanel2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class demonstrating logarithmic scale functionality in PlotPanel2D.
 * Shows a linear series from 1 to 100 on logarithmic scale.
 * 
 * @author zsolt
 */
public class LogarithmicScaleTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Logarithmic Scale Test - Linear Series 1-100");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Create the plot panel
            PlotPanel2D plotPanel = new PlotPanel2D();
            plotPanel.setPreferredSize(new Dimension(1000, 700));
            
            // Create linear data series from 1 to 100
            List<Point2D> linearPoints = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                linearPoints.add(new Point2D.Double(i, i)); // y = x linear function
            }
            
            // Create exponential data series to show logarithmic effect
            List<Point2D> exponentialPoints = new ArrayList<>();
            for (int i = 1; i <= 50; i++) {
                double y = Math.pow(2, i/10.0); // Exponential growth
                if (y <= 1000) { // Limit to reasonable values
                    exponentialPoints.add(new Point2D.Double(i, y));
                }
            }
            
            // Create quadratic data series
            List<Point2D> quadraticPoints = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                double y = i * i / 10.0; // y = x²/10
                if (y <= 1000) {
                    quadraticPoints.add(new Point2D.Double(i, y));
                }
            }
            
            // Create data series
            List<DataSeries> dataSeriesList = List.of(
                new DataSeries("Linear (y=x)", linearPoints, Color.BLUE, true, true, 2, 4),
                new DataSeries("Exponential (y=2^(x/10))", exponentialPoints, Color.RED, true, true, 2, 4),
                new DataSeries("Quadratic (y=x²/10)", quadraticPoints, Color.GREEN, true, true, 2, 4)
            );
            
            plotPanel.setDataSeries(dataSeriesList);
            plotPanel.setAxisLabels("X values", "Y values");
            plotPanel.setPlotTitle("Linear vs Exponential vs Quadratic Functions - Logarithmic Scale Demo");
            
            frame.add(plotPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            // Show instructions
            JOptionPane.showMessageDialog(frame, 
                "Instructions:\n" +
                "1. Use the 'X-Scale' and 'Y-Scale' dropdowns to switch between Linear and Logarithmic scales\n" +
                "2. Try setting Y-Scale to 'Logarithmic' to see how the exponential curve becomes linear\n" +
                "3. Try setting both X-Scale and Y-Scale to 'Logarithmic' for log-log plots\n" +
                "4. Use mouse wheel to zoom, drag to pan, and buttons for reset",
                "Logarithmic Scale Demo", 
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
