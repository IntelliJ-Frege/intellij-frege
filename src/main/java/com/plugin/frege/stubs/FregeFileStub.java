package com.plugin.frege.stubs;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.plugin.frege.psi.FregeFile;

public class FregeFileStub extends PsiFileStubImpl<FregeFile> {
    public FregeFileStub(FregeFile file) {
        super(file);
    }
}
