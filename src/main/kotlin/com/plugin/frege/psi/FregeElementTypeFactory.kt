package com.plugin.frege.psi

import com.intellij.psi.tree.IElementType
import com.plugin.frege.stubs.types.*

object FregeElementTypeFactory {
    @JvmStatic
    fun factory(name: String): IElementType {
        return when (name) {
            "NATIVE_DATA_DECL" -> FregeNativeDataDeclElementType(name)
            "PROGRAM" -> FregeProgramElementType(name)
            "CLASS_DECL" -> FregeClassDeclElementType(name)
            "DATA_DECL" -> FregeDataDeclElementType(name)
            "NEWTYPE_DECL" -> FregeNewtypeDeclElementType(name)
            "TYPE_DECL" -> FregeTypeDeclElementType(name)
            "CONSTRUCT" -> FregeConstructElementType(name)
            "BINDING" -> FregeBindingElementType(name)
            "ANNOTATION_ITEM" -> FregeAnnotationItemElementType(name)
            "NATIVE_FUNCTION" -> FregeNativeFunctionElementType(name)
            else -> throw IllegalStateException("Unknown element name: $name")
        }
    }
}
