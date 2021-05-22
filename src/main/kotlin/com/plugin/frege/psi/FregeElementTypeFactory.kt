package com.plugin.frege.psi

import com.intellij.psi.tree.IElementType
import com.plugin.frege.stubs.types.*

object FregeElementTypeFactory {
    @JvmStatic
    fun factory(name: String): IElementType {
        return when (name) {
            "DATA_DCL_NATIVE" -> FregeDataDclNativeElementType(name)
            "PACKAGE_CLASS_NAME" -> FregePackageClassNameElementType(name)
            "CLASS_DCL" -> FregeClassDclElementType(name)
            "BINDING" -> FregeBindingElementType(name)
            "ANNOTATION" -> FregeAnnotationElementType(name)
            else -> throw IllegalStateException("Unknown element name: $name")
        }
    }
}
