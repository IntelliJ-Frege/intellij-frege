package com.plugin.frege.stubs.index

import com.intellij.psi.stubs.StubIndexKey
import com.plugin.frege.psi.FregePsiClass

object FregeShortClassNameIndex : FregeNameIndex<FregePsiClass>() {
    private val KEY = StubIndexKey.createIndexKey<Int, FregePsiClass>(
        "com.plugin.frege.stubs.index.FregeShortClassNameIndex"
    )

    override fun getKey(): StubIndexKey<Int, FregePsiClass> = KEY
}
