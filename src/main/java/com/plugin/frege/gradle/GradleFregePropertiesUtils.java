package com.plugin.frege.gradle;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class GradleFregePropertiesUtils {
    public static final String GRADLE_PROPERTIES_FILENAME = "gradle.properties";
    public static final String FREGE_RELEASE_PROPERTY = "fregeRelease";
    public static final String FREGE_VERSION_PROPERTY = "fregeVersion";
    public static final String FREGE_JAR_PROPERTY = "fregeJar";
    public static final String DEFAULT_PATH_TO_FREGE_DIR = "lib/org/frege-lang/frege/"; //todo

    private static String getRelativePathToCompiler(String version) {
        return "lib/org/frege-lang/frege/" + version + "/frege-" + version + ".jar";
    }

    public static Pair<String, VirtualFile> getFregeRelease(Module module) throws GradleFregeException {
        return getPropertyFromResourceBundles(getResourceBundles(module), FREGE_RELEASE_PROPERTY);
    }

    public static Pair<String, VirtualFile> getFregeVersion(Module module) throws GradleFregeException {
        return getPropertyFromResourceBundles(getResourceBundles(module), FREGE_VERSION_PROPERTY);
    }

    public static VirtualFile createGradlePropertiesFile(Module module) throws GradleFregeException {
        String pathToProjectRoot = module.getProject().getBasePath();
        if (pathToProjectRoot == null) {
            throw new GradleFregeException("Unable to get project root while creating new project");
        }
        VirtualFile moduleContentRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(pathToProjectRoot));
        if (moduleContentRoot == null) {
            throw new GradleFregeException("Unable to get project root VirtualFile while creating new project");
        }
        Path pathToModuleRoot = moduleContentRoot.toNioPath();
        boolean ignored = pathToModuleRoot.toFile().mkdirs();
        Path pathToGradleProperties = pathToModuleRoot.resolve(GRADLE_PROPERTIES_FILENAME);
        try {
            boolean ignored2 = pathToGradleProperties.toFile().createNewFile();
        } catch (IOException e) {
            throw new GradleFregeException("Unable to create new " + GRADLE_PROPERTIES_FILENAME + " on path " + pathToGradleProperties, e);
        }
        return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(pathToGradleProperties.toFile());
    }

    public static String getGradlePropertiesFileContentBySettings(GradleFregeForm settings) {
        StringJoiner sj = new StringJoiner("\n");
        sj.add("javaTarget = " + settings.getJavaTarget());
        if (settings.isCompilerAutoDownloaded()) {
            sj.add("fregeRelease = " + settings.getFregeRelease());
            sj.add("fregeVersion = " + settings.getFregeVersion());
        } else {
            sj.add("fregeJar = " + settings.getFregeCompilerPath());
        }
        return sj.toString();
    }

    public static void writeSettingsToGradlePropertiesFile(VirtualFile file, GradleFregeForm settings) throws GradleFregeException {
        byte[] contentBytes = getGradlePropertiesFileContentBySettings(settings).getBytes(StandardCharsets.UTF_8);
        try {
            file.setBinaryContent(contentBytes);
        } catch (IOException e) {
            throw new GradleFregeException("Unable to write properties to " + file.getPresentableName(), e);
        }
    }

    public static String getCompilerJar(Module module) throws GradleFregeException {
        List<Pair<PropertyResourceBundle, VirtualFile>> resourceBundles = getResourceBundles(module);
        List<Pair<String, VirtualFile>> jarProperties = getPropertiesListFromResourceBundle(resourceBundles, FREGE_JAR_PROPERTY);

        if (jarProperties.size() > 1)
            throw new GradleFregeException("More than one " + FREGE_JAR_PROPERTY + " found in different" + GRADLE_PROPERTIES_FILENAME);

        String fregeJar;
        if (jarProperties.isEmpty()) {
            String fregeVersion = getPropertyFromResourceBundles(resourceBundles, FREGE_VERSION_PROPERTY).first;
            String relativePathToCompiler = getRelativePathToCompiler(fregeVersion);
            List<VirtualFile> compilerFilesInModule = getFilesInModuleByRelativePath(module, relativePathToCompiler);
            if (compilerFilesInModule.isEmpty())
                throw new GradleFregeException("No compiler found in " + module.getName() + ". Tried " + relativePathToCompiler);
            fregeJar = compilerFilesInModule.get(0).toNioPath().toString();
        } else {
            fregeJar = jarProperties.get(0).first;
        }

        return fregeJar;
    }

    private static Pair<String, VirtualFile> getPropertyFromResourceBundles(List<Pair<PropertyResourceBundle, VirtualFile>> resourceBundles, String propertyName) throws GradleFregeException {
        List<Pair<String, VirtualFile>> foundProperties = getPropertiesListFromResourceBundle(resourceBundles, propertyName);
        if (foundProperties.isEmpty())
            throw new GradleFregeException("No property " + propertyName + " found in any " + GRADLE_PROPERTIES_FILENAME);
        if (foundProperties.size() > 1)
            throw new GradleFregeException("More than one " + propertyName + " found in different " + GRADLE_PROPERTIES_FILENAME);
        return foundProperties.get(0);
    }

    private static @NotNull List<Pair<String, VirtualFile>> getPropertiesListFromResourceBundle(List<Pair<PropertyResourceBundle, VirtualFile>> resourceBundles, String propertyName) {
        return resourceBundles.stream().
                filter(b -> b.first.containsKey(propertyName)).map(b -> new Pair<>(b.first.getString(propertyName), b.second)).
                collect(Collectors.toList());
    }

    private static List<Pair<PropertyResourceBundle, VirtualFile>> getResourceBundles(Module module) throws GradleFregeException {
        List<VirtualFile> gradlePropertiesFiles = getFilesInModuleByRelativePath(module, GRADLE_PROPERTIES_FILENAME);
        if (gradlePropertiesFiles.isEmpty())
            throw new GradleFregeException("No " + GRADLE_PROPERTIES_FILENAME + " was found in module " + module.getName());

        List<Pair<PropertyResourceBundle, VirtualFile>> resourceBundles = new ArrayList<>();
        for (VirtualFile gradlePropertyFile : gradlePropertiesFiles) {
            try {
                PropertyResourceBundle propertyResourceBundle = new PropertyResourceBundle(gradlePropertyFile.getInputStream());
                resourceBundles.add(new Pair<>(propertyResourceBundle, gradlePropertyFile));
            } catch (IOException e) {
                throw new GradleFregeException(e);
            }
        }
        return resourceBundles;
    }

    @NotNull
    private static List<VirtualFile> getFilesInModuleByRelativePath(Module module, String path) {
        VirtualFile[] moduleContentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        return Arrays.stream(moduleContentRoots).map(cr -> cr.findFileByRelativePath(path)).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
