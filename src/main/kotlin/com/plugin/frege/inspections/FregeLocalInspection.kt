package com.plugin.frege.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.plugin.frege.psi.FregeCompositeElement

abstract class FregeLocalInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement) {
                super.visitElement(element)
                if (element is FregeCompositeElement) {
                    visitElement(element, holder, isOnTheFly)
                }
            }
        }
    }

    abstract fun visitElement(element: FregeCompositeElement, holder: ProblemsHolder, isOnTheFly: Boolean)
}
