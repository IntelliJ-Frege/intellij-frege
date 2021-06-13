package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.documentation.DocBuilder
import com.plugin.frege.documentation.FregeDocUtil
import com.plugin.frege.documentation.buildDoc
import com.plugin.frege.psi.*
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.psi.impl.FregePsiUtilImpl
import com.plugin.frege.resolve.FregeResolveUtil
import com.plugin.frege.stubs.FregeProgramStub

@Suppress("UnstableApiUsage")
abstract class FregeProgramMixin : FregePsiClassImpl<FregeProgramStub>, FregeProgram {
    private companion object {
        private const val DEFAULT_MODULE_NAME: String = "Main"
    }

    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeProgramStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameIdentifier(): PsiIdentifier? {
        return packageName?.conidUsage
    }

    override fun getQualifiedName(): @NlsSafe String {
        val nameFromStub = greenStub?.name
        if (nameFromStub != null) {
            return nameFromStub
        }
        return packageName?.text ?: DEFAULT_MODULE_NAME
    }

    override fun getNameWithoutStub(): @NlsSafe String {
        return nameIdentifier?.text ?: DEFAULT_MODULE_NAME
    }

    override fun isInterface(): Boolean {
        return false
    }

    override fun getMethods(): Array<PsiMethod> {
        val body = body ?: return PsiMethod.EMPTY_ARRAY
        return FregePsiUtilImpl.subprogramsFromScopeOfElement(body) { (it as? FregeDecl)?.binding }
            .asSequence()
            .sortedBy { it.textOffset }
            .distinctBy { it.name } // pattern-matching
            .toList().toTypedArray()
    }

    override fun getScope(): PsiElement {
        return this
    }

    override fun generateDoc(): String {
        return buildDoc {
            definition {
                appendText("Module ")
                appendBoldText(qualifiedName)
            }
            content {
                appendDocs(FregeDocUtil.collectDocComments(this@FregeProgramMixin))
                val body = PsiTreeUtil.findChildOfType(this@FregeProgramMixin, FregeBody::class.java)
                if (body != null) {
                    section("Functions:") {
                        for (method in methods) {
                            if (method is FregePsiMethod) {
                                paragraph { appendPsiMethodLink(method) }
                            }
                        }
                    }
                    val psiClasses = FregeResolveUtil.findClassesInCurrentFile(body)
                    generateSectionWithLinksToPsiClasses(
                        this, "Classes:", psiClasses.filterIsInstance<FregeClassDecl>()
                    )
                    generateSectionWithLinksToPsiClasses(
                        this, "Instances:", psiClasses.filterIsInstance<FregeInstanceDecl>()
                    )
                    generateSectionWithLinksToPsiClasses(
                        this, "Dates:", psiClasses.filterIsInstance<FregeDataDecl>()
                    )
                    generateSectionWithLinksToPsiClasses(
                        this, "Native dates:", psiClasses.filterIsInstance<FregeNativeDataDecl>()
                    )
                    generateSectionWithLinksToPsiClasses(
                        this, "Types:", psiClasses.filterIsInstance<FregeTypeDecl>()
                    )
                    generateSectionWithLinksToPsiClasses(
                        this, "Newtypes:", psiClasses.filterIsInstance<FregeNewtypeDecl>()
                    )
                }
            }
        }
    }

    private fun generateSectionWithLinksToPsiClasses(
        stringBuilder: DocBuilder,
        title: String,
        psiClasses: List<FregePsiClass>
    ): DocBuilder {
        if (psiClasses.isEmpty()) {
            return stringBuilder
        }
        return stringBuilder.section(title) {
            for (psiClass in psiClasses) {
                paragraph { appendPsiClassLink(psiClass) }
            }
        }
    }
}

object FregeProgramUtil {
    val FregeProgram.imports: List<FregeImportDecl>
        get() {
            val stub = (this as? FregeProgramMixin)?.greenStub
            if (stub != null) {
                return stub.importStrings.map { FregeElementFactory.createImportDecl(project, it) }
            }
            return body?.topDeclList?.mapNotNull { it.firstChild as? FregeImportDecl } ?: emptyList()
        }
}
