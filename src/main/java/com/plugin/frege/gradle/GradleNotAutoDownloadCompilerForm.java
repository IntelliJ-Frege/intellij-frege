package com.plugin.frege.gradle;

import javax.swing.*;

public class GradleNotAutoDownloadCompilerForm {
    public JTextField getFregeCompilerPath() {
        return fregeCompilerPath;
    }

    private JTextField fregeCompilerPath;
    private JPanel panel;

    public JPanel getPanel() {
        return panel;
    }

    public void createUIComponents() {
    }
}
