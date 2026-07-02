package objectiveethics.test.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import objectiveethics.values.logic.method.information.GZIPInfo;

/**
 * Graphical test program to analyze average information at different scales.
 */
public class MultiscaleAverageInfoAnalyzer extends JFrame {

    private BufferedImage originalImage;
    private BufferedImage grayscaleImage;
    private ImagePanel imagePanel;
    private ChartPanel chartPanel;
    private JLabel statusLabel;
    private JButton processButton;
    private JCheckBox logXCheckBox;
    private JCheckBox logYCheckBox;
    private GZIPInfo gzipInfo;

    public MultiscaleAverageInfoAnalyzer() {
        setTitle("Multiscale Average Information Analyzer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        gzipInfo = new GZIPInfo();

        initComponents();
    }

    private void initComponents() {
        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton loadButton = new JButton("Load Image");
        loadButton.addActionListener(e -> loadImage());
        toolBar.add(loadButton);

        toolBar.addSeparator();

        processButton = new JButton("Analyze");
        processButton.setEnabled(false);
        processButton.addActionListener(e -> analyzeImage());
        toolBar.add(processButton);
        
        toolBar.addSeparator();
        
        logXCheckBox = new JCheckBox("Log X Axis");
        logXCheckBox.setSelected(true); // Default to Log X for multiscale
        logXCheckBox.addActionListener(e -> {
            chartPanel.setLogX(logXCheckBox.isSelected());
        });
        toolBar.add(logXCheckBox);
        
        logYCheckBox = new JCheckBox("Log Y Axis");
        logYCheckBox.addActionListener(e -> {
            chartPanel.setLogY(logYCheckBox.isSelected());
        });
        toolBar.add(logYCheckBox);

        add(toolBar, BorderLayout.NORTH);

        // Main Content
        imagePanel = new ImagePanel();
        chartPanel = new ChartPanel();
        chartPanel.setLogX(true); // Sync with checkbox

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                new JScrollPane(imagePanel), chartPanel);
        splitPane.setResizeWeight(0.4);
        add(splitPane, BorderLayout.CENTER);

        // Status Bar
        statusLabel = new JLabel("Ready");
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void loadImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                originalImage = ImageIO.read(file);
                if (originalImage == null) {
                    JOptionPane.showMessageDialog(this, "Could not load image.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Convert to grayscale
                grayscaleImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                Graphics g = grayscaleImage.getGraphics();
                g.drawImage(originalImage, 0, 0, null);
                g.dispose();

                imagePanel.setImage(grayscaleImage);
                chartPanel.clearData();
                
                processButton.setEnabled(true);
                statusLabel.setText("Image loaded. Size: " + originalImage.getWidth() + "x" + originalImage.getHeight());

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void analyzeImage() {
        if (grayscaleImage == null) return;

        processButton.setEnabled(false);
        chartPanel.clearData();
        statusLabel.setText("Analyzing...");

        int width = grayscaleImage.getWidth();
        int height = grayscaleImage.getHeight();
        int maxScale = Math.min(width, height);

        SwingWorker<Void, DataPoint> worker = new SwingWorker<Void, DataPoint>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Get pixel data
                int[] grayPixels = new int[width * height];
                grayscaleImage.getRaster().getSamples(0, 0, width, height, 0, grayPixels);

                for (int scale = 1; scale <= maxScale; scale++) {
                    if (isCancelled()) break;

                    int newWidth = width / scale;
                    int newHeight = height / scale;
                    
                    if (newWidth == 0 || newHeight == 0) continue;

                    byte[] downsampledPixels = new byte[newWidth * newHeight];
                    int idx = 0;

                    for (int y = 0; y < newHeight; y++) {
                        for (int x = 0; x < newWidth; x++) {
                            // Calculate average of the block
                            long sum = 0;
                            int count = 0;
                            
                            int startX = x * scale;
                            int startY = y * scale;
                            
                            for (int by = 0; by < scale; by++) {
                                for (int bx = 0; bx < scale; bx++) {
                                    int px = startX + bx;
                                    int py = startY + by;
                                    // Boundary check not strictly needed if we iterate up to newWidth/Height which are floor divisions
                                    if (px < width && py < height) {
                                        sum += grayPixels[py * width + px];
                                        count++;
                                    }
                                }
                            }
                            
                            int avg = (count > 0) ? (int)(sum / count) : 0;
                            downsampledPixels[idx++] = (byte) avg;
                        }
                    }

                    double infoValue = gzipInfo.value(downsampledPixels);
                    double maxEntropy = downsampledPixels.length * 8.0;
                    double normalizedValue = (maxEntropy > 0) ? infoValue / maxEntropy : 0;
                    publish(new DataPoint(scale, normalizedValue));
                    
                    if (scale % 10 == 0 || scale == maxScale) {
                        setProgress((int)((double)scale / maxScale * 100));
                    }
                }
                return null;
            }

            @Override
            protected void process(List<DataPoint> chunks) {
                for (DataPoint p : chunks) {
                    chartPanel.addDataPoint(p);
                }
                statusLabel.setText("Analyzing... Scale: " + chunks.get(chunks.size()-1).scale + "/" + maxScale);
            }

            @Override
            protected void done() {
                try {
                    get();
                    statusLabel.setText("Analysis complete.");
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    statusLabel.setText("Error: " + e.getMessage());
                } finally {
                    processButton.setEnabled(true);
                }
            }
        };
        
        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                // statusLabel.setText("Analyzing... " + evt.getNewValue() + "%");
            }
        });

        worker.execute();
    }

    private static class DataPoint {
        int scale;
        double value;

        public DataPoint(int scale, double value) {
            this.scale = scale;
            this.value = value;
        }
    }

    private class ImagePanel extends JPanel {
        private BufferedImage image;

        public void setImage(BufferedImage image) {
            this.image = image;
            if (image != null) {
                setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
            }
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, this);
            } else {
                g.drawString("No image", 20, 20);
            }
        }
    }

    private class ChartPanel extends JPanel {
        private List<DataPoint> dataPoints = Collections.synchronizedList(new ArrayList<>());
        private int padding = 50;
        private int labelPadding = 25;
        private boolean logX = false;
        private boolean logY = false;

        public ChartPanel() {
            setBackground(Color.WHITE);
        }
        
        public void setLogX(boolean logX) {
            this.logX = logX;
            repaint();
        }
        
        public void setLogY(boolean logY) {
            this.logY = logY;
            repaint();
        }

        public void addDataPoint(DataPoint p) {
            dataPoints.add(p);
            repaint();
        }

        public void clearData() {
            dataPoints.clear();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (dataPoints.isEmpty()) {
                g2.drawString("No data to display", getWidth() / 2 - 50, getHeight() / 2);
                return;
            }

            double minScore = 0;
            double maxScore = 1.0;
            
            for (DataPoint p : dataPoints) {
                maxScore = Math.max(maxScore, p.value);
            }
            maxScore = Math.ceil(maxScore * 1.1);
            if (maxScore == 0) maxScore = 1;
            
            // For Log Y, we need a non-zero min
            double minLogY = Double.MAX_VALUE;
            if (logY) {
                for (DataPoint p : dataPoints) {
                    if (p.value > 0) minLogY = Math.min(minLogY, p.value);
                }
                if (minLogY == Double.MAX_VALUE) minLogY = 0.001; // Default if all 0
                // Adjust minScore for log plot
                minScore = minLogY;
            }

            int maxScale = 0;
            if (!dataPoints.isEmpty()) {
                for(DataPoint p : dataPoints) maxScale = Math.max(maxScale, p.scale);
            }
            if (maxScale == 0) maxScale = 100;

            // Calculate scales
            double xScale;
            if (logX) {
                // Log scale: x = log(scale) / log(maxScale) * width
                // We assume min scale is 1, log(1) = 0
                xScale = ((double) getWidth() - 2 * padding - labelPadding) / Math.log(maxScale);
            } else {
                xScale = ((double) getWidth() - 2 * padding - labelPadding) / (maxScale - 1);
                if (maxScale == 1) xScale = 0;
            }
            
            double yScale;
            if (logY) {
                // Log scale: y = (log(val) - log(min)) / (log(max) - log(min)) * height
                double logMin = Math.log(minScore);
                double logMax = Math.log(maxScore);
                if (logMax == logMin) yScale = 0;
                else yScale = ((double) getHeight() - 2 * padding - labelPadding) / (logMax - logMin);
            } else {
                yScale = ((double) getHeight() - 2 * padding - labelPadding) / (maxScore - minScore);
            }

            List<Point> graphPoints = new ArrayList<>();
            for (DataPoint p : dataPoints) {
                int x1;
                if (logX) {
                    double logVal = Math.log(p.scale);
                    x1 = (int) (logVal * xScale + padding + labelPadding);
                } else {
                    x1 = (int) ((p.scale - 1) * xScale + padding + labelPadding);
                }
                
                int y1;
                if (logY) {
                    double val = p.value;
                    if (val <= 0) val = minScore; // Clamp to min
                    double logVal = Math.log(val);
                    double logMin = Math.log(minScore);
                    double logMax = Math.log(maxScore);
                    // Invert Y axis
                    y1 = (int) ((logMax - logVal) * yScale + padding);
                } else {
                    y1 = (int) ((maxScore - p.value) * yScale + padding);
                }
                
                graphPoints.add(new Point(x1, y1));
            }

            // Draw white background
            g2.setColor(Color.WHITE);
            g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
            g2.setColor(Color.BLACK);

            // Y Axis Grid and Labels
            int numYDivisions = 10;
            for (int i = 0; i <= numYDivisions; i++) {
                int x0 = padding + labelPadding;
                int x1 = padding + labelPadding + 5;
                int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numYDivisions + padding + labelPadding);
                int y1 = y0;
                
                double val;
                if (logY) {
                    double logMin = Math.log(minScore);
                    double logMax = Math.log(maxScore);
                    double logVal = logMin + (logMax - logMin) * ((double)i / numYDivisions);
                    val = Math.exp(logVal);
                } else {
                    val = minScore + (maxScore - minScore) * ((double)i / numYDivisions);
                }
                
                if (dataPoints.size() > 0) {
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.drawLine(padding + labelPadding + 1 + labelPadding, y0, getWidth() - padding, y1);
                    g2.setColor(Color.BLACK);
                    String yLabel = String.format("%.2f", val);
                    FontMetrics metrics = g2.getFontMetrics();
                    int labelWidth = metrics.stringWidth(yLabel);
                    g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
                }
                g2.drawLine(x0, y0, x1, y1);
            }

            // X Axis Grid and Labels
            // For Log X, we want 1, 10, 100, etc.
            if (logX) {
                int maxPower = (int)Math.log10(maxScale);
                for (int i = 0; i <= maxPower + 1; i++) {
                    int val = (int)Math.pow(10, i);
                    if (val > maxScale && i > 0) break; // Show at least 1
                    
                    double logVal = Math.log(val);
                    int x0 = (int) (logVal * xScale + padding + labelPadding);
                    int x1 = x0;
                    int y0 = getHeight() - padding - labelPadding;
                    int y1 = y0 - 5;
                    
                    if (x0 > getWidth() - padding) continue;

                    if (dataPoints.size() > 0) {
                        g2.setColor(Color.LIGHT_GRAY);
                        g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - labelPadding, x1, padding);
                        g2.setColor(Color.BLACK);
                        String xLabel = val + "";
                        FontMetrics metrics = g2.getFontMetrics();
                        int labelWidth = metrics.stringWidth(xLabel);
                        g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                    }
                    g2.drawLine(x0, y0, x1, y1);
                }
            } else {
                int numXLabels = 10;
                if (maxScale < 10) numXLabels = maxScale;
                for (int i = 0; i <= numXLabels; i++) {
                    if (maxScale <= 1) break;
                    int val = 1 + (int)((maxScale - 1) * ((double)i / numXLabels));
                    
                    int x0 = i * (getWidth() - padding * 2 - labelPadding) / numXLabels + padding + labelPadding;
                    int x1 = x0;
                    int y0 = getHeight() - padding - labelPadding;
                    int y1 = y0 - 5;
                    
                    if (dataPoints.size() > 0) {
                        g2.setColor(Color.LIGHT_GRAY);
                        g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - labelPadding, x1, padding);
                        g2.setColor(Color.BLACK);
                        String xLabel = val + "";
                        FontMetrics metrics = g2.getFontMetrics();
                        int labelWidth = metrics.stringWidth(xLabel);
                        g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                    }
                    g2.drawLine(x0, y0, x1, y1);
                }
            }

            // Draw axes
            g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
            g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

            // Draw lines
            Stroke oldStroke = g2.getStroke();
            g2.setColor(Color.BLUE);
            g2.setStroke(new BasicStroke(2f));
            for (int i = 0; i < graphPoints.size() - 1; i++) {
                int x1 = graphPoints.get(i).x;
                int y1 = graphPoints.get(i).y;
                int x2 = graphPoints.get(i + 1).x;
                int y2 = graphPoints.get(i + 1).y;
                g2.drawLine(x1, y1, x2, y2);
            }

            g2.setStroke(oldStroke);
            g2.setColor(Color.RED);
            for (int i = 0; i < graphPoints.size(); i++) {
                int x = graphPoints.get(i).x - 2;
                int y = graphPoints.get(i).y - 2;
                int ovalW = 4;
                int ovalH = 4;
                g2.fillOval(x, y, ovalW, ovalH);
            }
            
            // Labels
            g2.setColor(Color.BLACK);
            g2.drawString("Scale (pixels)" + (logX ? " [Log]" : ""), getWidth() / 2, getHeight() - 10);
            g2.drawString("Normalized GZIP Info" + (logY ? " [Log]" : ""), 10, getHeight() / 2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MultiscaleAverageInfoAnalyzer().setVisible(true);
        });
    }
}
