package com.plugin.frege.module;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.plugin.frege.FregeIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class FregeModuleType extends ModuleType<FregeModuleBuilder> {

    private static final String ID = "FREGE_MODULE_TYPE";

    public FregeModuleType() {
        super(ID);
    }

    public static FregeModuleType getInstance() {
        return (FregeModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public FregeModuleBuilder createModuleBuilder() {
        return new FregeModuleBuilder();
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getName() {
        return "Frege Module";
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String getDescription() {
        return "The module for building Frege projects";
    }

    @Override
    public @NotNull Icon getNodeIcon(boolean isOpened) {
        return FregeIcons.FILE;
    }
}
