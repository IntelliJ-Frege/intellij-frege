package com.plugin.frege.repl;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.AbstractTableCellEditor;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.plugin.frege.gradle.GradleFregePropertiesUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FregeReplConfigurationEditor extends SettingsEditor<FregeReplRunConfiguration> {
    private JPanel myPanel;
    private JLabel moduleNameLabel;
    private JComboBox<String> moduleNameComboBox;
    private ListTableModel<JCheckBox> loadedModulesTableModel;
    private JTable loadedModulesTable;
    private JTextField arguments;


    public FregeReplConfigurationEditor(Project project) {
        moduleNameLabel.setToolTipText("Module containing " + GradleFregePropertiesUtils.GRADLE_PROPERTIES_FILENAME + " file in root");
        setModulesToComboBox(project);
        setModulesToLoad(project, new ArrayList<>());
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
        for (int i = 0; i < loadedModulesTableModel.getRowCount(); i++) {
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
    protected void resetEditorFrom(FregeReplRunConfiguration runConfiguration) {
        setArguments(runConfiguration.getAdditionalArguments());
        setModulesToComboBox(runConfiguration.getProject());
        setModule(runConfiguration.getSelectedModuleName());
        setModulesToLoad(runConfiguration.getProject(), runConfiguration.getModulesToLoad());
    }

    @Override
    protected void applyEditorTo(FregeReplRunConfiguration runConfiguration) throws ConfigurationException {
        runConfiguration.setAdditionalArguments(getArguments());
        runConfiguration.setSelectedModuleName(getSelectedModule());
        runConfiguration.setModulesToLoad(getModulesToLoad());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myPanel;
    }
}
