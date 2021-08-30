package com.plugin.frege.stubs.index

import com.intellij.psi.stubs.StubIndexKey
import com.plugin.frege.psi.FregePsiMethod

object FregeMethodNameIndex : FregeNameIndex<FregePsiMethod>() {
    private val KEY = StubIndexKey.createIndexKey<Int, FregePsiMethod>(
        "com.plugin.frege.stubs.index.FregeMethodNameIndex"
    )

    override fun getKey(): StubIndexKey<Int, FregePsiMethod> = KEY
}
