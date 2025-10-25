package objectiveethics.test.ui;

import javax.swing.*;

import objectiveethics.commons.diagram.DataSeries;
import objectiveethics.commons.diagram.PlotPanel2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Test class for PlotPanel2D to verify line thickness and node size functionality
 *
 * @author zsolt
 */
public class PlotPanel2DTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("PlotPanel2D - Line Thickness & Node Size Test");
            PlotPanel2D plotPanel = new PlotPanel2D();
            plotPanel.setPreferredSize(new Dimension(800, 600));
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(plotPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Test constant Y values (horizontal line) with thick line
            List<DataSeries> dataSeriesList = List.of(
                new DataSeries("Horizontal Line (thick)", List.of(
                    new Point2D.Double(0, 1), 
                    new Point2D.Double(1, 1), 
                    new Point2D.Double(2, 1), 
                    new Point2D.Double(3, 1)
                ), Color.RED, true, true, 3, 8),
                
                // Test constant X values (vertical line) with large nodes
                new DataSeries("Vertical Line (large nodes)", List.of(
                    new Point2D.Double(2, 0), 
                    new Point2D.Double(2, 1), 
                    new Point2D.Double(2, 2), 
                    new Point2D.Double(2, 3)
                ), Color.BLUE, true, true, 1, 10),
                
                // Test normal varying data with default settings
                new DataSeries("Normal Data (default)", List.of(
                    new Point2D.Double(0, 0), 
                    new Point2D.Double(1, 2), 
                    new Point2D.Double(2, 1.5), 
                    new Point2D.Double(3, 3)
                ), Color.GREEN, true, true),
                
                // Test with very thin line and small nodes
                new DataSeries("Thin Line (small nodes)", List.of(
                    new Point2D.Double(0.5, 0.5), 
                    new Point2D.Double(1.5, 2.5), 
                    new Point2D.Double(2.5, 1.8), 
                    new Point2D.Double(3.5, 2.8)
                ), Color.ORANGE, true, true, 1, 3)
            );
            
            plotPanel.setDataSeries(dataSeriesList);
            
            // Demonstrate axis labels
            plotPanel.setAxisLabels("X Values", "Y Values");
        });
    }
}
