package com.plugin.frege.psi

interface FregeElementProvideDocumentation : FregeCompositeElement {
    fun getDocs(): List<FregeDocumentationElement>
}
