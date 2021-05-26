package com.plugin.frege.stubs

import com.intellij.psi.stubs.PsiFileStubImpl
import com.plugin.frege.psi.FregeFile

class FregeFileStub(file: FregeFile?) : PsiFileStubImpl<FregeFile>(file)
