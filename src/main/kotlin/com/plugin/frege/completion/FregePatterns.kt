package com.plugin.frege.completion

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.*

object FregePatterns {
    object AccessModifierPatterns : PlatformPatterns() {
        @JvmStatic
        fun accessModifierPattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().atStartOf(psiElement(FregeTopDecl::class.java))
        }
    }

    object BooleanLiteralPatterns : PlatformPatterns() {
        @JvmStatic
        fun booleanLiteralPattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().withSuperParent(2, FregeTerm::class.java)
        }

        // TODO support pTerm
    }

    object CaseExpressionPatterns : PlatformPatterns() {
        @JvmStatic
        fun casePattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().atStartOf(psiElement(FregeTopEx::class.java))
        }

        @JvmStatic
        fun ofPattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().inside(
                true,
                psiElement(FregeCaseEx::class.java),
                psiElement().afterLeaf(FregeKeywords.OF)
            )
        }
    }

    object ClassDclPatterns {
        @JvmStatic
        fun classOrInterfacePattern(): PsiElementPattern.Capture<PsiElement> {
            return PlatformPatterns.psiElement().andOr(
                PlatformPatterns.psiElement().atStartOf(PlatformPatterns.psiElement(FregeTopDecl::class.java)),
                PlatformPatterns.psiElement().afterLeaf(AccessModifierPatterns.accessModifierPattern())
            )
        }
    }

    object CondPatterns : PlatformPatterns() {
        @JvmStatic
        fun ifPattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().atStartOf(psiElement(FregeTopEx::class.java))
        }

        @JvmStatic
        fun thenPattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().inside(
                true,
                psiElement(FregeCond::class.java),
                psiElement().afterLeaf(FregeKeywords.THEN)
            )
        }

        @JvmStatic
        fun elsePattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().inside(
                true,
                psiElement(FregeCond::class.java),
                or(
                    psiElement().afterLeaf(FregeKeywords.IF),
                    psiElement().afterLeaf(FregeKeywords.ELSE)
                )
            )
        }
    }

    object DataDclConstructorsPatterns : PlatformPatterns() {
        @JvmStatic
        fun abstractPattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().andOr(
                psiElement().atStartOf(psiElement(FregeTopDecl::class.java)),
                psiElement().afterLeaf(AccessModifierPatterns.accessModifierPattern())
            )
        }

        @JvmStatic
        fun dataPattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().withParent(
                psiElement().afterLeaf(FregeKeywords.ABSTRACT)
            )
        }
    }

    object DataDclNativePatterns : PlatformPatterns() {
        @JvmStatic
        fun dataPattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().andOr(
                psiElement().atStartOf(psiElement(FregeTopDecl::class.java)),
                psiElement().afterLeaf(AccessModifierPatterns.accessModifierPattern())
            )
        }

        @JvmStatic
        fun nativePattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().andOr(
                psiElement().atStartOf(psiElement(FregeTopDecl::class.java)),
                psiElement().afterLeaf(AccessModifierPatterns.accessModifierPattern())
            )
        }
    }

    object DeclPatterns {
        @JvmStatic
        fun declPattern(): PsiElementPattern.Capture<PsiElement> {
            return PlatformPatterns.psiElement().andOr(
                PlatformPatterns.psiElement().atStartOf(PlatformPatterns.psiElement(FregeTopDecl::class.java)),
                PlatformPatterns.psiElement().afterLeaf(AccessModifierPatterns.accessModifierPattern()),
                PlatformPatterns.psiElement().withParent(FregeLetExpression::class.java),
                PlatformPatterns.psiElement().withParent(FregeWhereSection::class.java)
            )
        }
    }

    object DeriveDclPatterns : PlatformPatterns() {
        @JvmStatic
        fun derivePattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().andOr(
                psiElement().atStartOf(psiElement(FregeTopDecl::class.java)),
                psiElement().afterLeaf(AccessModifierPatterns.accessModifierPattern()),
                psiElement().inside(
                    true,
                    psiElement(FregeDataDcl::class.java),
                    psiElement(FregeDeriveDcl::class.java)
                )
            )
        }
    }

    object DoExpressionPatterns : PlatformPatterns() {
        @JvmStatic
        fun doExpressionPattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().atStartOf(psiElement(FregeTopEx::class.java))
        }
    }

    object ImportDclPatterns : PlatformPatterns() {
        @JvmStatic
        fun importPattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().atStartOf(psiElement(FregeTopDecl::class.java))
        }

        // TODO as and public modifier patterns
    }

    object InfixRulePatterns : PlatformPatterns() {
        @JvmStatic
        fun infixRulePattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().atStartOf(psiElement(FregeTopDecl::class.java))
        }
    }

    object InstDclPatterns : PlatformPatterns() {
        @JvmStatic
        fun instancePattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().andOr(
                psiElement().atStartOf(psiElement(FregeTopDecl::class.java)),
                psiElement().afterLeaf(AccessModifierPatterns.accessModifierPattern())
            )
        }
    }

    object LetExpressionPatterns {
        @JvmStatic
        fun letPattern(): PsiElementPattern.Capture<PsiElement> {
            return PlatformPatterns.psiElement().atStartOf(PlatformPatterns.psiElement(FregeTopEx::class.java))
        }
    }

    object LetInExpressionPatterns {
        @JvmStatic
        fun inPattern(): PsiElementPattern.Capture<PsiElement> {
            return PlatformPatterns.psiElement().inside(
                true,
                PlatformPatterns.psiElement(FregeLetInExpression::class.java).afterLeaf(FregeKeywords.LET),
                PlatformPatterns.psiElement().afterLeaf(FregeKeywords.IN)
            )
        }
    }

    object NativeFunPatterns : PlatformPatterns() {
        @JvmStatic
        fun purePattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().andOr(
                psiElement().atStartOf(psiElement(FregeTopDecl::class.java)),
                psiElement().afterLeaf(AccessModifierPatterns.accessModifierPattern())
            )
        }

        @JvmStatic
        fun nativePattern(): PsiElementPattern.Capture<PsiElement> {
            return psiElement().andOr(
                psiElement().atStartOf(psiElement(FregeTopDecl::class.java)),
                psiElement().afterLeaf(AccessModifierPatterns.accessModifierPattern())
            )
        }
    }

    object WhereSectionPatterns : PlatformPatterns() {
        @JvmStatic
        fun whereSectionPattern(): PsiElementPattern.Capture<PsiElement> {
            val wherePattern = psiElement().andOr(
                psiElement().withParent(FregeAlt::class.java),
                psiElement().withParent(FregeDataDclConstructors::class.java),
                psiElement().withParent(FregeDataDclNative::class.java),
                psiElement().withParent(FregeClassDcl::class.java),
                psiElement().withParent(FregeInstDcl::class.java),
                psiElement().withParent(FregeRhs::class.java)
            )
            return psiElement().inside(true, wherePattern, psiElement().beforeLeaf(FregeKeywords.WHERE))
        }
    }
}
