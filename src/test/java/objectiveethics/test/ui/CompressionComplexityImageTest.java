package objectiveethics.test.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import objectiveethics.values.logic.method.information.GZIPInfo;

/**
 * Graphical test program to visualize compression complexity of image neighborhoods.
 */
public class CompressionComplexityImageTest extends JFrame {

    private BufferedImage originalImage;
    private BufferedImage grayscaleImage;
    private BufferedImage processedImage;
    private ImagePanel originalPanel;
    private ImagePanel processedPanel;
    private JSlider neighborhoodSlider;
    private JLabel statusLabel;
    private JLabel neighborhoodValueLabel;
    private JButton processButton;
    private GZIPInfo gzipInfo;

    public CompressionComplexityImageTest() {
        setTitle("Compression Complexity Image Analyzer");
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

        toolBar.add(new JLabel("Neighborhood Size (X): "));
        neighborhoodSlider = new JSlider(1, 100, 1);
        neighborhoodSlider.setEnabled(false);
        neighborhoodSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                neighborhoodValueLabel.setText(String.valueOf(neighborhoodSlider.getValue()));
            }
        });
        toolBar.add(neighborhoodSlider);
        
        neighborhoodValueLabel = new JLabel("1");
        toolBar.add(neighborhoodValueLabel);

        toolBar.addSeparator();

        processButton = new JButton("Process");
        processButton.setEnabled(false);
        processButton.addActionListener(e -> processImage());
        toolBar.add(processButton);

        add(toolBar, BorderLayout.NORTH);

        // Main Content
        originalPanel = new ImagePanel();
        processedPanel = new ImagePanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                new JScrollPane(originalPanel), new JScrollPane(processedPanel));
        splitPane.setResizeWeight(0.5);
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

                originalPanel.setImage(grayscaleImage); // Show grayscale version as base
                processedPanel.setImage(null);
                
                // Update slider max to max dimension of image
                int maxDim = Math.max(originalImage.getWidth(), originalImage.getHeight());
                neighborhoodSlider.setMaximum(maxDim);
                neighborhoodSlider.setValue(1);
                neighborhoodSlider.setEnabled(true);
                processButton.setEnabled(true);
                statusLabel.setText("Image loaded. Size: " + originalImage.getWidth() + "x" + originalImage.getHeight());

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void processImage() {
        if (grayscaleImage == null) return;

        int xSize = neighborhoodSlider.getValue();
        processButton.setEnabled(false);
        statusLabel.setText("Processing... (Neighborhood: " + xSize + ")");

        int width = grayscaleImage.getWidth();
        int height = grayscaleImage.getHeight();
        
        // Initialize processedImage immediately
        processedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        processedPanel.setImage(processedImage);

        SwingWorker<Void, RowUpdate> worker = new SwingWorker<Void, RowUpdate>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Let's use a safer way to get pixel values
                int[] grayPixels = new int[width * height];
                grayscaleImage.getRaster().getSamples(0, 0, width, height, 0, grayPixels);

                int totalPixels = width * height;
                int processedCount = 0;

                for (int y = 0; y < height; y++) {
                    int[] rowValues = new int[width];
                    for (int x = 0; x < width; x++) {
                        // Define neighborhood bounds
                        int startX = x - (xSize - 1) / 2;
                        int startY = y - (xSize - 1) / 2;
                        int endX = startX + xSize;
                        int endY = startY + xSize;

                        // Clamp to image bounds
                        int rStartX = Math.max(0, startX);
                        int rStartY = Math.max(0, startY);
                        int rEndX = Math.min(width, endX);
                        int rEndY = Math.min(height, endY);

                        int nWidth = rEndX - rStartX;
                        int nHeight = rEndY - rStartY;
                        
                        if (nWidth <= 0 || nHeight <= 0) {
                             rowValues[x] = 0;
                             continue;
                        }

                        byte[] neighborhood = new byte[nWidth * nHeight];
                        int idx = 0;
                        for (int ny = rStartY; ny < rEndY; ny++) {
                            for (int nx = rStartX; nx < rEndX; nx++) {
                                neighborhood[idx++] = (byte) grayPixels[ny * width + nx];
                            }
                        }

                        double complexity = gzipInfo.value(neighborhood);
                        // Average complexity per pixel (bits per pixel)
                        double avgComplexity = complexity / neighborhood.length;
                        
                        // Map 0-8 bits to 0-255
                        // 8 bits is theoretical max for byte.
                        int pixelVal = (int) (avgComplexity * 255.0 / 8.0);
                        if (pixelVal > 255) pixelVal = 255;
                        if (pixelVal < 0) pixelVal = 0;

                        rowValues[x] = pixelVal;
                    }
                    
                    publish(new RowUpdate(y, rowValues));
                    
                    processedCount += width;
                    setProgress((int)((double)processedCount / totalPixels * 100));
                }
                return null;
            }

            @Override
            protected void process(java.util.List<RowUpdate> chunks) {
                for (RowUpdate update : chunks) {
                    for (int x = 0; x < update.values.length; x++) {
                        processedImage.getRaster().setSample(x, update.y, 0, update.values[x]);
                    }
                }
                processedPanel.repaint();
            }

            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    statusLabel.setText("Processing complete.");
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
                statusLabel.setText("Processing... " + evt.getNewValue() + "%");
            }
        });

        worker.execute();
    }
    
    private static class RowUpdate {
        int y;
        int[] values;
        
        RowUpdate(int y, int[] values) {
            this.y = y;
            this.values = values;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CompressionComplexityImageTest().setVisible(true);
        });
    }
}
