package com.plugin.frege.stubs.index

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.IntStubIndexExtension
import com.plugin.frege.psi.FregeNamedElement

abstract class FregeNameIndex<Psi : FregeNamedElement> protected constructor() : IntStubIndexExtension<Psi>() {
    fun findByName(name: String, project: Project, scope: GlobalSearchScope): List<Psi> =
        get(name.hashCode(), project, scope).filter { nameMatched(it, name) }

    protected open fun nameMatched(element: Psi, name: String): Boolean = element.name == name
}
