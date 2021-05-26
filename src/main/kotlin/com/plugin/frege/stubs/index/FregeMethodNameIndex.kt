package com.plugin.frege.stubs.index

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import com.plugin.frege.psi.FregePsiMethod

class FregeMethodNameIndex private constructor() : StringStubIndexExtension<FregePsiMethod>() {
    companion object {
        @JvmStatic
        val INSTANCE = FregeMethodNameIndex()

        private val KEY = StubIndexKey.createIndexKey<String, FregePsiMethod>(
            "com.plugin.frege.stubs.index.FregeMethodNameIndex"
        )
    }

    override fun getKey(): StubIndexKey<String, FregePsiMethod> = KEY
}
