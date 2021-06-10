package com.plugin.frege.psi

interface FregeDocumentableElement : FregeNamedElement {
    fun generateDoc(): String
}
