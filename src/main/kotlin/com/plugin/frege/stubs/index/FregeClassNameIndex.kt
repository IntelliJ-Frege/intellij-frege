package com.plugin.frege.stubs.index

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import com.plugin.frege.psi.FregePsiClass

class FregeClassNameIndex private constructor() : StringStubIndexExtension<FregePsiClass>() {
    companion object {
        @JvmStatic
        val INSTANCE = FregeClassNameIndex()

        private val KEY = StubIndexKey.createIndexKey<String, FregePsiClass>(
            "com.plugin.frege.stubs.index.FregeClassNameIndex"
        )
    }

    override fun getKey(): StubIndexKey<String, FregePsiClass> = KEY
}
