package com.plugin.frege.gradle;

import javax.swing.*;

public class GradleAutoDownloadCompilerForm {
    private JTextField fregeRelease;
    private JTextField fregeVersion;
    private JPanel panel;

    public JPanel getPanel() {
        return panel;
    }

    public JTextField getFregeRelease() {
        return fregeRelease;
    }

    public JTextField getFregeVersion() {
        return fregeVersion;
    }

    public void createUIComponents() {
    }
}
