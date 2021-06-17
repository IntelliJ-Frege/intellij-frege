package com.plugin.frege.gradle;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class GradleFregeForm {
    boolean autoDownloadCompilerMode = true;
    private JPanel panel;


    public GradleFregeForm() {
        javaTarget.setText("11");
        fregeRelease.setText("3.25alpha");
        fregeVersion.setText("3.25.84");

        autoDownloadTheCompilerCheckBox.addItemListener(e -> {
            int state = e.getStateChange();
            switch (state) {
                case ItemEvent.SELECTED:
                    selectAutodownloadCompilerMode(true);
                    break;
                case ItemEvent.DESELECTED:
                    selectAutodownloadCompilerMode(false);
                    break;
            }
        });

        fregeCompilerPath.addBrowseFolderListener("Choose Path to Frege Compiler Jar", null, null,
                new FileChooserDescriptor(false, false, true, true, false, false));
    }

    private void selectAutodownloadCompilerMode(boolean value) {
        autoDownloadCompilerMode = value;
        fregeCompilerPathLabel.setVisible(!value);
        fregeCompilerPath.setVisible(!value);
    }

    public JPanel getPanel() {
        return panel;
    }

    public String getJavaTarget() {
        return javaTarget.getText();
    }

    public String getFregeRelease() {
        return fregeRelease.getText();
    }

    public String getFregeVersion() {
        return fregeVersion.getText();
    }

    public boolean isCompilerAutoDownloaded() {
        return autoDownloadTheCompilerCheckBox.isSelected();
    }

    public String getFregeCompilerPath() {
        return fregeCompilerPath.getText();
    }

    public void createUIComponents() {
    }

    private JTextField javaTarget;
    private JTextField fregeRelease;
    private JTextField fregeVersion;

    private JCheckBox autoDownloadTheCompilerCheckBox;
    private JLabel fregeCompilerPathLabel;
    private TextFieldWithBrowseButton fregeCompilerPath;
}
