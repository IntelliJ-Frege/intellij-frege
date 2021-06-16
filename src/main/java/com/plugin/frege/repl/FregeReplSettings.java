package com.plugin.frege.repl;

import com.intellij.openapi.project.Project;

public class FregeReplSettings {
    private final Project project;
    private String arguments;

    FregeReplSettings(Project project, String arguments) {
        this.arguments = arguments;
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public String getArguments() {
        return arguments;
    }
}
