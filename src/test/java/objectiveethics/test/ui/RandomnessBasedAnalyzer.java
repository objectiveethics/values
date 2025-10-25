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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import objectiveethics.commons.diagram.DataSeries;
import objectiveethics.values.logic.ValueLogic;
import objectiveethics.values.logic.method.EstimatedValue;
import objectiveethics.values.structure.Value;
import objectiveethics.values.structure.BaseValue;

/**
 *
 * @author zsolt
 */
public class RandomnessBasedAnalyzer extends javax.swing.JPanel {

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
    public RandomnessBasedAnalyzer() {
        initComponents();

        randomnessSlider.addChangeListener((ChangeEvent ce) -> {
            randomnessSpinner.setValue(randomnessSlider.getValue());
        });
        randomnessSpinner.addChangeListener((ChangeEvent ce) -> {
            randomnessSlider.setValue((int)randomnessSpinner.getValue());
        });

        List<Value> methods = ValueLogic.values();
        List<Value> extendedMethods = new ArrayList<>(methods);

        for(Value v : methods){
            extendedMethods.add(new EstimatedValue(v));
        }

        values.setModel(new ValueTableModel(extendedMethods));
        values.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(50);
        values.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(50);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        values.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        values.setRowHeight(30);

        length.addChangeListener((ChangeEvent ce) -> {
            generate();
        });
        

        baseSet.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                generate();
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                generate();
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                generate();
            }
        });

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
        if(value instanceof EstimatedValue) {
            return "Est. " + getBaseName(((EstimatedValue)value).getBaseValue());
        }
        return getBaseName(value);
    }

    private String getBaseName(Value value) {
        BaseValue annotation = value.getClass().getAnnotation(BaseValue.class);
        return annotation != null ? annotation.name() : value.getClass().getSimpleName();
    }

    private void generate() {
        int lengthValue = (int) length.getValue();
        String charSet = baseSet.getText();
        double r = Math.pow(10.0, -((Number)randomnessSpinner.getValue()).doubleValue());

        progress.setString("Generate strings...");
        
        StringBuilder stringListBuilder = new StringBuilder();
        Map<Double, String> stringList = new LinkedHashMap<>();
        for (double randomness = 0; randomness <=1; randomness+=r) {
            String text = generateRandomText(charSet, lengthValue, randomness);
            stringList.put(randomness, text);
            int p = (int) ((double) randomness * 100);
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
                Set<Map.Entry<Double, String>> entrySet = stringList.entrySet();
                int j = 0;
                int n = 0;
                for (Iterator<Map.Entry<Double, String>> iterator = entrySet.iterator();iterator.hasNext();) {
                    Map.Entry<Double, String> entry = iterator.next();
                    double val = value.value(entry.getValue());
                    points.add(new Point2D.Double(entry.getKey(), val));
                    int p = (int) ((double) j++  / stringList.size() * 100);
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
            RandomnessBasedAnalyzer analyzer = new RandomnessBasedAnalyzer();
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
        baseSetLabel = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        baseSet = new javax.swing.JTextPane();
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
        maxLengthLabel.setText("Maximum length");
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

        baseSetLabel.setFont(baseSetLabel.getFont().deriveFont(baseSetLabel.getFont().getSize()+2f));
        baseSetLabel.setText("Base set");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        settingsPanel.add(baseSetLabel, gridBagConstraints);

        scrollPane.setMinimumSize(new java.awt.Dimension(10, 60));
        scrollPane.setPreferredSize(new java.awt.Dimension(10, 60));

        baseSet.setFont(baseSet.getFont().deriveFont(baseSet.getFont().getSize()+2f));
        baseSet.setText("abcdefghijklmnopqrstuvwxyz");
        scrollPane.setViewportView(baseSet);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        settingsPanel.add(scrollPane, gridBagConstraints);

        randomnessLabel.setFont(randomnessLabel.getFont().deriveFont(randomnessLabel.getFont().getSize()+2f));
        randomnessLabel.setText("Randomness resolution");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        settingsPanel.add(randomnessLabel, gridBagConstraints);

        randomnessSlider.setMaximum(5);
        randomnessSlider.setMinimum(1);
        randomnessSlider.setValue(3);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        settingsPanel.add(randomnessSlider, gridBagConstraints);

        randomnessSpinner.setFont(randomnessSpinner.getFont().deriveFont(randomnessSpinner.getFont().getSize()+2f));
        randomnessSpinner.setModel(new javax.swing.SpinnerNumberModel(3, 1, 5, 1));
        randomnessSpinner.setMinimumSize(new java.awt.Dimension(120, 27));
        randomnessSpinner.setPreferredSize(new java.awt.Dimension(120, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        settingsPanel.add(randomnessSpinner, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
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
    private javax.swing.JTextPane baseSet;
    private javax.swing.JLabel baseSetLabel;
    private javax.swing.JLabel filler;
    private javax.swing.JSpinner length;
    private javax.swing.JLabel maxLengthLabel;
    private objectiveethics.commons.diagram.PlotPanel2D plot;
    private javax.swing.JProgressBar progress;
    private javax.swing.JLabel randomnessLabel;
    private javax.swing.JSlider randomnessSlider;
    private javax.swing.JSpinner randomnessSpinner;
    private javax.swing.JScrollPane scrollPane;
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
