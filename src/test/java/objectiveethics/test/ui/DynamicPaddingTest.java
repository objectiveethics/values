package objectiveethics.test.ui;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import javax.swing.*;

import objectiveethics.commons.diagram.DataSeries;
import objectiveethics.commons.diagram.PlotPanel2D;

public class DynamicPaddingTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dynamic Padding Test");
            
            PlotPanel2D plotPanel = new PlotPanel2D();
            plotPanel.setPreferredSize(new Dimension(800, 600));
            
            // Create test data with various ranges
            List<DataSeries> dataSeriesList = List.of(
                new DataSeries("Test Data", List.of(
                    new Point2D.Double(-123.456, -987.654),
                    new Point2D.Double(0, 0),
                    new Point2D.Double(456.789, 1234.567),
                    new Point2D.Double(999.999, 2000.123)
                ), Color.RED, true, true)
            );
            
            // NO axis labels set - this should test dynamic padding
            plotPanel.setDataSeries(dataSeriesList);
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(plotPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
