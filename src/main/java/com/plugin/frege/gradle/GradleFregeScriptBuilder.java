package com.plugin.frege.gradle;

import org.jetbrains.plugins.gradle.frameworkSupport.BuildScriptDataBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.StringJoiner;

import static com.intellij.util.ResourceUtil.getResourceAsStream;

/**
 * Similiar to {@link BuildScriptDataBuilder}
 * by its purpose, but does not inherit it, because {@link BuildScriptDataBuilder} is not convenient enough
 */
public class GradleFregeScriptBuilder {
    private static final String RESOURCES_PATH = "templates/gradle";
    private static final String DISCLAIMER_PATH = "disclaimer.gradle";

    private final GradleFregeForm settings;
    private final StringJoiner lines = new StringJoiner("\n");

    public GradleFregeScriptBuilder(GradleFregeForm settings) {
        this.settings = settings;
    }

    private static String getFregeDir(GradleFregeForm settings) {
        return Path.of(settings.getFregeCompilerPath()).getParent().toString();
    }

    private static String getFregeName(GradleFregeForm settings) {
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
        lines.add(String.format("\tid \"%s\"", pluginName));
    }

    @SuppressWarnings("SameParameterValue")
    private void addPlugin(String pluginName, String version) {
        lines.add(String.format("\tid \"%s\" version \"%s\"", pluginName, version));
    }

    private void addPlugins() {
        lines.add("plugins {");
        addPlugin("application");
        addPlugin("idea");
        addPlugin("de.undercouch.download", "4.1.1");
        lines.add("}");
        addNewline();
    }

    private void addProjectProperty(String name, String value) {
        lines.add(String.format("\t%s = \"%s\"", name, value));
    }

    private void addProjectProperties() {
        lines.add("ext {");
        addProjectProperty("DEFAULT_JAVA_TARGET", "3.11");
        addProjectProperty("DEFAULT_FREGE_RELEASE", "3.24alpha");
        addProjectProperty("DEFAULT_FREGE_VERSION", "3.24.100");
        addNewline();
        addProjectProperty("javaTarget", "project.findProperty(\"javaTarget\") ?: DEFAULT_JAVA_TARGET");
        addProjectProperty("fregeRelease", "project.findProperty(\"fregeRelease\") ?: DEFAULT_FREGE_RELEASE");
        addProjectProperty("fregeVersion", "project.findProperty(\"fregeVersion\") ?: DEFAULT_FREGE_VERSION");
        addNewline();
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
        lines.add("\tmodule {");
        lines.add("\t\tsourceDirs += file(fregeMainSourceDir)");
        lines.add("\t}");
        lines.add("}");
        addNewline();
    }

    private void addRepositories() {
        lines.add("repositories {");
        lines.add("\tflatDir {");
        lines.add("\t\tdirs fregeDir");
        lines.add("\t}");
        lines.add("}");
        addNewline();
    }

    private void addDownloadCompilerFunction() {
        lines.add("void downloadCompiler() {");
        lines.add("\tant.mkdir(dir: fregeDir)");
        lines.add("\tant.get(src: fregeCompilerUrl,");
        lines.add("\t\t\tdest: fregeJar,");
        lines.add("\t\t\tskipexisting: 'true')");
        lines.add("}");
        addNewline();
    }

    private void addDependencies() {
        if (settings.isCompilerAutoDownloaded()) {
            addDownloadCompilerFunction();
            lines.add("configurations.implementation.defaultDependencies { deps ->");
            lines.add("\tdownloadCompiler()");
            lines.add("\tdeps.add(project.dependencies.create(\"org.frege-lang:frege:${fregeVersion}\"))");
            lines.add("}");
            lines.add("");

            lines.add("dependencies {");
            lines.add("}");
        } else {
            lines.add("dependencies {");
            lines.add(String.format("\timplementation name: \"%s\"", getFregeName(settings)));
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
