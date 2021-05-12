package com.plugin.frege.gradle;

import org.jetbrains.plugins.gradle.frameworkSupport.BuildScriptDataBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.StringJoiner;

import static com.intellij.util.ResourceUtil.getResourceAsStream;

/**
 * Similiar to {@link BuildScriptDataBuilder BuildScriptDataBuilder}
 * by its purpose, but does not inherit it, because {@link BuildScriptDataBuilder BuildScriptDataBuilder} is not convenient enough
 */
public class GradleFregeScriptBuilder {
    private static final String RESOURCES_PATH = "templates/gradle";
    private static final String DISCLAIMER_PATH = "disclaimer.gradle";

    private final GradleMinimalFregeForm settings;
    private final StringJoiner lines = new StringJoiner("\n");

    public GradleFregeScriptBuilder(GradleMinimalFregeForm settings) {
        this.settings = settings;
    }

    private static String getFregeDir(GradleMinimalFregeForm settings) {
        return Path.of(settings.getFregeCompilerPath()).getParent().toString();
    }

    private static String getFregeName(GradleMinimalFregeForm settings) {
        Path pathToCompiler = Path.of(settings.getFregeCompilerPath());
        String fileName = pathToCompiler.getFileName().toString();
        if (!fileName.endsWith(".jar"))
            throw new IllegalStateException("Frege compiler name does not end with .jar");
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    private String readTemplate(String filename) throws IOException {
        return new String(getResourceAsStream(GradleFregeScriptBuilder.class.getClassLoader(),
                RESOURCES_PATH, filename).readAllBytes());
    }

    private void addTemplate(String filename) throws IOException {
        lines.add(readTemplate(filename));
        addNewline();
    }

    private void addDisclaimer() throws IOException {
        addTemplate(DISCLAIMER_PATH);
        addNewline();
    }

    private void addPlugin(String pluginName) {
        lines.add(String.format("    id \"%s\"", pluginName));
    }

    private void addPlugins() {
        lines.add("plugins {");
        addPlugin("application");
        addPlugin("idea");
        lines.add("}");
        addNewline();
    }

    private void addProjectProperty(String name, String value) {
        lines.add(String.format("    %s = \"%s\"", name, value));
    }

    private void addProjectProperties() {
        lines.add("ext {");
        addProjectProperty("javaTarget", settings.getJavaTarget());
        addProjectProperty("fregeRelease", settings.getFregeRelease());
        addProjectProperty("fregeVersion", settings.getFregeVersion());
        if (settings.isCompilerAutoDownloaded()) {
            addProjectProperty("fregeDir",
                    "${rootProject.projectDir}/lib/org/frege-lang/frege/${fregeVersion}");
            addProjectProperty("fregeJar", "${fregeDir}/frege-${fregeVersion}.jar");
            addProjectProperty("fregeCompilerUrl",
                    "https://github.com/Frege/frege/releases/download/${fregeRelease}/frege${fregeVersion}.jar");
        } else {
            addProjectProperty("fregeDir", getFregeDir(settings));
            addProjectProperty("fregeJar", settings.getFregeCompilerPath());
        }
        addProjectProperty("fregeMainSourceDir", "${projectDir}/src/main/frege");
        addProjectProperty("fregeMainJavaDir", "${buildDir}/src/main/frege");
        lines.add("}");
        addNewline();
    }

    private void addIdeaSupport() {
        lines.add("idea {");
        lines.add("    module {");
        lines.add("        sourceDirs += file(fregeMainSourceDir)");
        lines.add("    }");
        lines.add("}");
        addNewline();
    }

    private void addRepositories() {
        lines.add("repositories {");
        lines.add("    flatDir {");
        lines.add("        dirs fregeDir");
        lines.add("    }");
        lines.add("}");
        addNewline();
    }

    private void addDownloadCompilerFunction() {
        lines.add("void downloadCompiler() {");
        lines.add("    ant.mkdir(dir: fregeDir)");
        lines.add("    ant.get(src: fregeCompilerUrl,");
        lines.add("            dest: fregeJar,");
        lines.add("            skipexisting: 'true')");
        lines.add("}");
        addNewline();
    }

    private void addDependencies() {
        if (settings.isCompilerAutoDownloaded()) {
            addDownloadCompilerFunction();
            lines.add("configurations.implementation.defaultDependencies { deps ->");
            lines.add("    downloadCompiler()");
            lines.add("    deps.add(project.dependencies.create(\"org.frege-lang:frege:${fregeVersion}\"))");
            lines.add("}");
            lines.add("");

            lines.add("dependencies {");
            lines.add("}");
        } else {
            lines.add("dependencies {");
            lines.add(String.format("    implementation name: \"%s\"", getFregeName(settings)));
            lines.add("}");
        }
        addNewline();
    }

    private void addJavaSupport() {
        lines.add("compileJava.dependsOn fregeCompile");
        lines.add("sourceSets.main.compileClasspath = files (sourceSets.main.compileClasspath + sourceSets.main.java.outputDir)");
    }

    private void addTasks() throws IOException {
        addTemplate("tasks/fregeInit.gradle");
        addTemplate("tasks/prepareCompileDirs.gradle");
        addTemplate("tasks/fregeCompile.gradle");
        addTemplate("tasks/fregeRun.gradle");
    }

    public byte[] build() throws IOException {
        addDisclaimer();
        addPlugins();
        addProjectProperties();
        addIdeaSupport();
        addRepositories();
        addDependencies();
        addTasks();
        addJavaSupport();

        return lines.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void addNewline() {
        lines.add("");
    }
}
