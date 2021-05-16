package com.plugin.frege.psi

import com.intellij.psi.tree.IElementType
import com.plugin.frege.stubs.types.FregeDataNativeNameElementType
import com.plugin.frege.stubs.types.FregePackageClassNameElementType
import com.plugin.frege.stubs.types.FregeFunctionNameElementType
import java.lang.IllegalStateException

object FregeElementTypeFactory {
    @JvmStatic
    fun factory(name: String): IElementType {
        return when (name) {
            "DATA_NAME_NATIVE" -> FregeDataNativeNameElementType(name)
            "PACKAGE_CLASS_NAME" -> FregePackageClassNameElementType(name)
            "FUNCTION_NAME" -> FregeFunctionNameElementType(name)
            else -> throw IllegalStateException("Unknown element name: $name")
        }
    }
}
