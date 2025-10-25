package objectiveethics.test.ui;

import javax.swing.*;

import objectiveethics.commons.diagram.DataSeries;
import objectiveethics.commons.diagram.PlotPanel2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;

/**
 * Test class specifically for demonstrating axis labels functionality
 *
 * @author zsolt
 */
public class AxisLabelsTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("PlotPanel2D - Axis Labels Test");
            PlotPanel2D plotPanel = new PlotPanel2D();
            plotPanel.setPreferredSize(new Dimension(900, 600));
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(plotPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Create sample data representing temperature over time
            List<Point2D> temperatureData = new ArrayList<>();
            for (double hour = 0; hour <= 24; hour += 0.5) {
                // Simulate temperature variation throughout the day
                double temp = 15 + 10 * Math.sin((hour - 6) * Math.PI / 12) + 2 * Math.random();
                temperatureData.add(new Point2D.Double(hour, temp));
            }
            
            // Create humidity data
            List<Point2D> humidityData = new ArrayList<>();
            for (double hour = 0; hour <= 24; hour += 1.0) {
                // Simulate humidity variation
                double humidity = 60 + 20 * Math.cos(hour * Math.PI / 12) + 5 * Math.random();
                humidityData.add(new Point2D.Double(hour, humidity));
            }
            
            List<DataSeries> dataSeriesList = List.of(
                new DataSeries("Temperature (°C)", temperatureData, Color.RED, true, true, 2, 4),
                new DataSeries("Humidity (%)", humidityData, Color.BLUE, true, true, 3, 6)
            );
            
            plotPanel.setDataSeries(dataSeriesList);
            
            // Demonstrate meaningful axis labels
            plotPanel.setAxisLabels("Time (hours)", "Value");
        });
    }
}
