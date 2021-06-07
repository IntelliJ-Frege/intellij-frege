package com.plugin.frege.psi

interface FregeElementProvideDocumentation : FregeNamedElement {
    fun generateDoc(): String
}
