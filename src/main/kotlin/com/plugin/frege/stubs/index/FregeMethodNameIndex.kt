package com.plugin.frege.stubs.index

import com.intellij.psi.stubs.StubIndexKey
import com.plugin.frege.psi.FregePsiMethod

class FregeMethodNameIndex private constructor() : FregeNameIndex<FregePsiMethod>() {
    companion object {
        @JvmStatic
        val INSTANCE = FregeMethodNameIndex()

        private val KEY = StubIndexKey.createIndexKey<Int, FregePsiMethod>(
            "com.plugin.frege.stubs.index.FregeMethodNameIndex"
        )
    }

    override fun getKey(): StubIndexKey<Int, FregePsiMethod> = KEY
}
