package com.plugin.frege.documentation

import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.FregeClassDecl
import com.plugin.frege.psi.FregeProgram
import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.psi.impl.FregeAnnotationItemImpl
import com.plugin.frege.psi.impl.FregeBindingImpl
import com.plugin.frege.psi.impl.FregeClassDeclImpl
import com.plugin.frege.psi.impl.FregePsiUtilImpl

object FregeGenerateDocUtil {
    @JvmStatic
    fun generateFregeMethodDoc(method: FregePsiMethod): String {
        val annoItem = when (method) {
            is FregeBindingImpl -> method.getAnnoItem() as? FregeAnnotationItemImpl
            is FregeAnnotationItemImpl -> method
            else -> null
        }
        return buildDoc {
            definition {
                appendModuleLink(method.parentOfType())
                appendNewline()
                appendText("Function ")
                appendBoldText(method.name)
                val type = annoItem?.getAnnotation()?.sigma?.text
                if (type != null) {
                    appendNewline()
                    appendCode("Type: $type")
                }
                val fregeClass = method.containingClass as? FregeClassDecl
                if (fregeClass != null) {
                    appendNewline()
                    appendText("within ")
                    appendPsiClassLink(fregeClass)
                }
            }
            content {
                appendDocs(annoItem)
                section("Implementations:") {
                    for (binding in FregePsiUtilImpl.getAllBindingsOfMethod(method)) {
                        paragraph {
                            appendDocs(binding)
                            appendNewline()
                            appendCode(binding.lhs.text)
                        }
                    }
                }
            }
        }
    }

    @JvmStatic
    fun generateFregeClassDoc(fregeClass: FregeClassDeclImpl): String {
        val uniqueMethods = fregeClass.allMethods.distinctBy { it.name }.mapNotNull { it as? FregePsiMethod }
        return buildDoc {
            definition {
                appendModuleLink(fregeClass.parentOfType())
                appendNewline()
                appendText("Class ")
                appendBoldText(fregeClass.name)
                appendNewline()
            }
            content {
                appendDocs(fregeClass)
                section("Functions:") {
                    for (method in uniqueMethods) {
                        paragraph { appendPsiMethodLink(method) }
                    }
                }
            }
        }
    }

    @JvmStatic
    fun generateFregeProgramDoc(fregeProgram: FregeProgram): String {
        val moduleName = fregeProgram.packageName?.text ?: return ""
        return buildDoc {
            definition {
                appendText("Module ")
                appendBoldText(moduleName)
            }
            content {
                appendDocs(fregeProgram)
            }
        }
    }
}
