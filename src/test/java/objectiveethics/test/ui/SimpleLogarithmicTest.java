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
 * Simple test demonstrating logarithmic scale with a linear series from 1 to 100.
 * 
 * @author zsolt
 */
public class SimpleLogarithmicTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Simple Logarithmic Test - 1 to 100 Linear Series");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Create the plot panel
            PlotPanel2D plotPanel = new PlotPanel2D();
            plotPanel.setPreferredSize(new Dimension(800, 600));
            
            // Create linear data series from 1 to 100
            List<Point2D> points = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                points.add(new Point2D.Double(i, i)); // Simple linear function y = x
            }
            
            // Create data series
            DataSeries linearSeries = new DataSeries("Linear Series (1-100)", points, Color.BLUE, true, true, 3, 5);
            plotPanel.addDataSeries(linearSeries);
            
            plotPanel.setAxisLabels("X Values (1-100)", "Y Values (1-100)");
            plotPanel.setPlotTitle("Linear Series from 1 to 100 - Switch to Logarithmic Scale");
            
            frame.add(plotPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            // Automatically set to logarithmic scale for demonstration
            Timer timer = new Timer(2000, e -> {
                JOptionPane.showMessageDialog(frame, 
                    "Now switch the Y-Scale to 'Logarithmic' to see the linear data on a log scale!\n\n" +
                    "Notice how the linear function appears as a curve on the logarithmic scale,\n" +
                    "and how the Y-axis labels change to show the original values (1, 10, 100, etc.)",
                    "Try Logarithmic Scale", 
                    JOptionPane.INFORMATION_MESSAGE);
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
}
