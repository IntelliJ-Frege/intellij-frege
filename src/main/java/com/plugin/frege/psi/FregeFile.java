package com.plugin.frege.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.plugin.frege.FregeFileType;
import com.plugin.frege.FregeLanguage;
import org.jetbrains.annotations.NotNull;

public class FregeFile extends PsiFileBase {

    public FregeFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, FregeLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return FregeFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Frege File";
    }
}
