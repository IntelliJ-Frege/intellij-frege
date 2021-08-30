package com.plugin.frege.stubs.index

import com.intellij.psi.stubs.StubIndexKey
import com.plugin.frege.psi.FregePsiClass

object FregeClassNameIndex : FregeNameIndex<FregePsiClass>() {
    private val KEY = StubIndexKey.createIndexKey<Int, FregePsiClass>(
        "com.plugin.frege.stubs.index.FregeClassNameIndex"
    )

    override fun getKey(): StubIndexKey<Int, FregePsiClass> = KEY

    override fun nameMatched(element: FregePsiClass, name: String): Boolean {
        return element.qualifiedName == name
    }
}
