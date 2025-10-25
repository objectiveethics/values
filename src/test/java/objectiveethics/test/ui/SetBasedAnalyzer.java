/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package objectiveethics.test.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import objectiveethics.commons.diagram.DataSeries;
import objectiveethics.values.logic.ValueLogic;
import objectiveethics.values.structure.Value;
import objectiveethics.values.structure.BaseValue;

/**
 *
 * @author zsolt
 */
public class SetBasedAnalyzer extends javax.swing.JPanel {

    static {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
    }

    /**
     * Creates new form StringAnalyzer
     */
    public SetBasedAnalyzer() {
        initComponents();
        randomnessSlider.addChangeListener(e -> randomnessSpinner.setValue(randomnessSlider.getValue() / 100.0));
        randomnessSpinner.addChangeListener(e -> randomnessSlider.setValue((int) ((double) randomnessSpinner.getValue() * 100)));

        values.setModel(new ValueTableModel(ValueLogic.values()));
        values.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(50);
        values.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(50);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        values.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        values.setRowHeight(30);

        randomnessSpinner.addChangeListener((ChangeEvent ce) -> {
            generate();
        });

        values.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                generate();
            }

        });

        generate();
    }

    /**
     * Helper method to get the name from BaseValue annotation
     */
    private String getValueName(Value value) {
        BaseValue annotation = value.getClass().getAnnotation(BaseValue.class);
        return annotation != null ? annotation.name() : value.getClass().getSimpleName();
    }

    private void generate() {
        int lengthValue = (int) length.getValue();
        double randomness = (double) randomnessSpinner.getValue();

        progress.setString("Generate strings...");
        
        StringBuilder stringListBuilder = new StringBuilder();
        List<String> stringList = new ArrayList<>();
        for (int setSize = 1; setSize < 256; setSize++) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i<setSize; i++){
                sb.append((char)i);
            }
            String text = generateRandomText(sb.toString(), lengthValue, randomness);
            stringList.add(text);
            int p = (int) ((double) setSize / 256 * 100);
            if (lengthValue < 100) {
                stringListBuilder.append(text).append("\n");
            } else {
                stringListBuilder.append(text.substring(0, 93)).append("...").append("\n");
            }
            SwingUtilities.invokeLater(() -> progress.setValue(p));
        }
        strings.setText(stringListBuilder.toString());
        strings.setCaretPosition(0);

        new Thread(() -> {
            List<DataSeries> dataSeriesList = new ArrayList<>();
            List<Value> selectedValues = getSelectedValues();
            List<Color> colors = generateDistinctColors(selectedValues.size());
            int i = 0;
            for (Value value : selectedValues) {
                progress.setString("Generate values: " + getValueName(value) + "...");
                List<Point2D> points = new ArrayList<>();
                for (int j = 0; j<stringList.size(); j++) {
                    double val = value.value(stringList.get(j));
                    points.add(new Point2D.Double(j, val));
                    int p = (int) ((double) j / 256 * 100);
                    SwingUtilities.invokeLater(() -> progress.setValue(p));
                }
                dataSeriesList.add(
                        new DataSeries(getValueName(value), points, colors.get(i++), true, false));
            }
            SwingUtilities.invokeLater(() -> {
                plot.setDataSeries(dataSeriesList);
                progress.setString("");
                progress.setValue(0);
            });
        }).start();
    }

    public static List<Color> generateDistinctColors(int n) {
        List<Color> colors = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            float hue = (float) i / n; // Ensures even distribution of hues
            float saturation = 1.0f;
            float brightness = 0.8f;
            Color color = Color.getHSBColor(hue, saturation, brightness);
            colors.add(color);
        }

        return colors;
    }

    private String generateRandomText(String charSet, int length, double randomness) {
        if (charSet.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (Math.random() < randomness) {
                sb.append(charSet.charAt((int) (Math.random() * charSet.length())));
            } else {
                sb.append(i == 0 ? charSet.charAt(0) : sb.charAt(i - 1));
            }
        }
        return sb.toString();
    }

    private List<Value> getSelectedValues() {
        ValueTableModel model = (ValueTableModel) values.getModel();
        return model.getSelectedValues();
    }

    class ValueTableModel extends AbstractTableModel {

        private final List<Value> values;
        private final List<Boolean> selected;

        public ValueTableModel(List<Value> values) {
            this.values = values;
            this.selected = new ArrayList<>(values.size());
            for (int i = 0; i < values.size(); i++) {
                selected.add(false);
            }
        }

        @Override
        public int getRowCount() {
            return values.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return selected.get(rowIndex);
                case 1:
                    return getValueName(values.get(rowIndex));
                case 2:
                    return ""; // Remove version column content
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                selected.set(rowIndex, (Boolean) aValue);
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Boolean.class;
                case 1:
                    return Value.class;
                case 2:
                    return Integer.class;
            }
            return Object.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Select";
                case 1:
                    return "Name";
                case 2:
                    return "Version";
            }
            return super.getColumnName(column);
        }

        public List<Value> getSelectedValues() {
            List<Value> selectedValues = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                if (selected.get(i)) {
                    selectedValues.add(values.get(i));
                }
            }
            return selectedValues;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SetBasedAnalyzer analyzer = new SetBasedAnalyzer();
            JFrame frame = new JFrame();
            frame.setTitle("String Analyzer");
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setPreferredSize(new Dimension(950, 600));
            frame.setContentPane(analyzer);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        plot = new objectiveethics.commons.diagram.PlotPanel2D();
        west = new javax.swing.JPanel();
        tabs = new javax.swing.JTabbedPane();
        settingsPanel = new javax.swing.JPanel();
        maxLengthLabel = new javax.swing.JLabel();
        length = new javax.swing.JSpinner();
        randomnessLabel = new javax.swing.JLabel();
        randomnessSlider = new javax.swing.JSlider();
        randomnessSpinner = new javax.swing.JSpinner();
        filler = new javax.swing.JLabel();
        stringsPanel = new javax.swing.JPanel();
        stringsScroll = new javax.swing.JScrollPane();
        strings = new javax.swing.JTextPane();
        valuesPanel = new javax.swing.JPanel();
        valuesScroll = new javax.swing.JScrollPane();
        values = new javax.swing.JTable();
        progress = new javax.swing.JProgressBar();

        setLayout(new java.awt.BorderLayout());
        add(plot, java.awt.BorderLayout.CENTER);

        west.setLayout(new java.awt.BorderLayout());

        tabs.setFont(tabs.getFont().deriveFont(tabs.getFont().getSize()+2f));
        tabs.setPreferredSize(new java.awt.Dimension(350, 300));

        settingsPanel.setLayout(new java.awt.GridBagLayout());

        maxLengthLabel.setFont(maxLengthLabel.getFont().deriveFont(maxLengthLabel.getFont().getSize()+2f));
        maxLengthLabel.setText("Length");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 5, 5);
        settingsPanel.add(maxLengthLabel, gridBagConstraints);

        length.setModel(new javax.swing.SpinnerNumberModel(1000, 1, null, 1000));
        length.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        settingsPanel.add(length, gridBagConstraints);

        randomnessLabel.setFont(randomnessLabel.getFont().deriveFont(randomnessLabel.getFont().getSize()+2f));
        randomnessLabel.setText("Randomness");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        settingsPanel.add(randomnessLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        settingsPanel.add(randomnessSlider, gridBagConstraints);

        randomnessSpinner.setFont(randomnessSpinner.getFont().deriveFont(randomnessSpinner.getFont().getSize()+2f));
        randomnessSpinner.setModel(new javax.swing.SpinnerNumberModel(0.5d, 0.0d, 1.0d, 0.1d));
        randomnessSpinner.setMinimumSize(new java.awt.Dimension(80, 27));
        randomnessSpinner.setPreferredSize(new java.awt.Dimension(80, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        settingsPanel.add(randomnessSpinner, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        settingsPanel.add(filler, gridBagConstraints);

        tabs.addTab("Settings", settingsPanel);

        stringsPanel.setLayout(new java.awt.BorderLayout());

        stringsScroll.setViewportView(strings);

        stringsPanel.add(stringsScroll, java.awt.BorderLayout.CENTER);

        tabs.addTab("Strings", stringsPanel);

        valuesPanel.setLayout(new java.awt.BorderLayout());

        values.setFont(values.getFont().deriveFont(values.getFont().getSize()+2f));
        values.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        valuesScroll.setViewportView(values);

        valuesPanel.add(valuesScroll, java.awt.BorderLayout.CENTER);

        tabs.addTab("Values", valuesPanel);

        west.add(tabs, java.awt.BorderLayout.WEST);

        progress.setStringPainted(true);
        west.add(progress, java.awt.BorderLayout.SOUTH);

        add(west, java.awt.BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel filler;
    private javax.swing.JSpinner length;
    private javax.swing.JLabel maxLengthLabel;
    private objectiveethics.commons.diagram.PlotPanel2D plot;
    private javax.swing.JProgressBar progress;
    private javax.swing.JLabel randomnessLabel;
    private javax.swing.JSlider randomnessSlider;
    private javax.swing.JSpinner randomnessSpinner;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JTextPane strings;
    private javax.swing.JPanel stringsPanel;
    private javax.swing.JScrollPane stringsScroll;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable values;
    private javax.swing.JPanel valuesPanel;
    private javax.swing.JScrollPane valuesScroll;
    private javax.swing.JPanel west;
    // End of variables declaration//GEN-END:variables
}
