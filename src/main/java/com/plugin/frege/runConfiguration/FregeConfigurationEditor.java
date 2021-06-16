package com.plugin.frege.runConfiguration;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.AbstractTableCellEditor;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.EditableModel;
import com.intellij.util.ui.ListTableModel;
import com.plugin.frege.gradle.GradleFregePropertiesUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FregeConfigurationEditor extends SettingsEditor<FregeRunConfiguration> {
    private JPanel myPanel;
    private JLabel moduleNameLabel;
    private JComboBox<String> moduleNameComboBox; // TODO custom listener for names
    private ListTableModel<JCheckBox> loadedModulesTableModel;
    private JTable loadedModulesTable;
    private JTextField arguments;


    public FregeConfigurationEditor(Project project) {
        moduleNameLabel.setToolTipText("Module containing " + GradleFregePropertiesUtils.GRADLE_PROPERTIES_FILENAME + " file in root");
        setModulesToComboBox(project);
//        loadedModulesTable.setModel(loadedModulesTableModel);
//        loadedModulesTable.getModel().
        setModulesToLoad(project, new ArrayList<>());
//        ProjectRootManager.getInstance(project).
    }
    private static class MyTableModel extends AbstractTableModel implements EditableModel {
        List<String> moduleNames = new ArrayList<>();

        @Override
        public void addRow() {
            moduleNames.add("");
        }

        @Override
        public void exchangeRows(int oldIndex, int newIndex) {
            throw new UnsupportedOperationException("Row exchancing is not supported");
        }

        @Override
        public boolean canExchangeRows(int oldIndex, int newIndex) {
            return false;
        }

        @Override
        public void removeRow(int idx) {
            moduleNames.remove(idx);
        }

        @Override
        public int getRowCount() {
            return moduleNames.size();
        }

        @Override
        public int getColumnCount() {
            return 1;
        }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return moduleNames.get(rowIndex);
        }

    }

    private void createUIComponents() {
        loadedModulesTableModel = new ListTableModel<>(new ColumnInfo[]{new ColumnInfo<>("Modules to Load") {
            @Override
            public @Nullable Object valueOf(Object o) {
                return o;
            }

            @Override
            public boolean isCellEditable(Object o) {
                return true;
            }
        }
        });

        loadedModulesTable = new JBTable(loadedModulesTableModel);

        class MyCellRenderer extends JCheckBox implements TableCellRenderer {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return (JCheckBox) value;
            }
        }
        loadedModulesTable.getColumnModel().getColumn(0).setCellRenderer(new MyCellRenderer());

        loadedModulesTable.getColumnModel().getColumn(0).setCellEditor(new AbstractTableCellEditor() {
            @Override
            public Object getCellEditorValue() {
                return null;
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                return (Component) value;
            }
        });
    }


    public void setModule(String moduleName) {
        moduleNameComboBox.setSelectedItem(moduleName);
    }

    private String getSelectedModule() {
        return (String) moduleNameComboBox.getSelectedItem();
    }

    private void setModulesToComboBox(Project project) {
        moduleNameComboBox.removeAllItems();
        Arrays.stream(ModuleManager.getInstance(project).getModules()).map(Module::getName).forEach(moduleNameComboBox::addItem);
    }

    public String getArguments() {
        return arguments.getText();
    }

    public void setArguments(String text) {
        arguments.setText(text);
    }

    public List<String> getModulesToLoad() {
        List<String> result = new ArrayList<>();
        for (int i = 0;i < loadedModulesTableModel.getRowCount(); i++) {
            JCheckBox checkbox = loadedModulesTableModel.getRowValue(i);
            if (checkbox.isSelected()) {
                result.add(checkbox.getText());
            }
        }
        return result;
    }

    public void setModulesToLoad(Project project, List<String> loadedModules) {
        loadedModulesTableModel.setItems(new ArrayList<>()); // clearing
        Arrays.stream(ModuleManager.getInstance(project).getModules()).
                map(m -> new JCheckBox(m.getName(), loadedModules.contains(m.getName()))).
                forEach(m -> loadedModulesTableModel.addRow(m));
    }

    @Override
    protected void resetEditorFrom(FregeRunConfiguration runConfiguration) {
        System.err.println("Reset called");
        setArguments(runConfiguration.getAdditionalArguments());
        setModulesToComboBox(runConfiguration.getProject());
        setModule(runConfiguration.getSelectedModuleName());
        setModulesToLoad(runConfiguration.getProject(), runConfiguration.getModulesToLoad());
        // TODO
    }

    @Override
    protected void applyEditorTo(FregeRunConfiguration runConfiguration) throws ConfigurationException {
        System.err.println("Apply called");
        runConfiguration.setAdditionalArguments(getArguments());
        runConfiguration.setSelectedModuleName(getSelectedModule());
        runConfiguration.setModulesToLoad(getModulesToLoad());
        // TODO
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myPanel;
    }
}