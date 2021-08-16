package com.plugin.frege.inspections

import com.intellij.codeInsight.navigation.NavigationUtil
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.ide.DataManager
import com.intellij.lang.ASTNode
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.parentOfType
import com.intellij.ui.ColoredListCellRenderer
import com.plugin.frege.psi.*
import com.plugin.frege.psi.mixin.FregeProgramUtil.imports
import com.plugin.frege.stubs.index.FregeMethodNameIndex
import com.plugin.frege.stubs.index.FregeShortClassNameIndex
import javax.swing.JList

class FregeAddImportQuickFix : LocalQuickFix {
    override fun getFamilyName(): String {
        return "Import module"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val usage = descriptor.psiElement ?: return
        if (usage !is FregeCompositeElement || usage is FregeNamedElement) {
            return
        }
        val module = usage.parentOfType<FregeProgram>() ?: return
        val name = FregeName(usage)
        val candidates = findCandidates(name, project)
        showPromptToPickCandidate(module, name, candidates, project)
    }

    private fun findCandidates(name: FregeName, project: Project): List<FregeProgram> {
        val methods = FregeMethodNameIndex.INSTANCE.findByName(
            name.shortName, project, GlobalSearchScope.everythingScope(project)
        ).mapNotNull { it.parentOfType<FregeProgram>(true) }
        val classes = FregeShortClassNameIndex.INSTANCE.findByName(
            name.shortName, project, GlobalSearchScope.everythingScope(project)
        ).mapNotNull { it.parentOfType<FregeProgram>(true) }
        return (methods + classes).distinct()
    }

    private fun showPromptToPickCandidate(
        module: FregeProgram,
        name: FregeName,
        candidates: List<FregeProgram>,
        project: Project
    ) {
        if (candidates.isEmpty()) {
            return
        }
        DataManager.getInstance().dataContextFromFocusAsync.onSuccess { dataContext ->
            val picker = ImportPickerUI(dataContext, name, project)
            picker.pick(candidates) { candidate ->
                WriteCommandAction.runWriteCommandAction(project) {
                    ImportAdder.addImport(module, candidate)
                }
            }
        }
    }
}

private class ImportPickerUI(
    private val dataContext: DataContext,
    private val name: FregeName,
    private val project: Project
) {
    fun pick(candidates: List<FregeProgram>, callback: (FregeProgram) -> Unit) {
        val popup = JBPopupFactory.getInstance().createPopupChooserBuilder(candidates)
            .setTitle("Import '${name.shortName}' from module:")
            .setItemChosenCallback { callback(it) }
            .setNamerForFiltering { it.qualifiedName }
            .setRenderer(CandidateRenderer())
            .createPopup()
        NavigationUtil.hidePopupIfDumbModeStarts(popup, project)
        popup.showInBestPositionFor(dataContext)
    }
}

private class CandidateRenderer : ColoredListCellRenderer<FregeProgram>() {
    override fun customizeCellRenderer(
        list: JList<out FregeProgram>,
        value: FregeProgram,
        index: Int,
        selected: Boolean,
        hasFocus: Boolean
    ) {
        value.qualifiedName?.let { append(it) }
    }
}

private object ImportAdder {
    fun addImport(toModule: FregeProgram, moduleToAdd: FregeProgram) {
        val qualifiedName = moduleToAdd.qualifiedName ?: return
        val project = toModule.project
        val newImport = FregeElementFactory.createTopDecl(project, "import $qualifiedName").node
        val lastImport = toModule.imports.lastOrNull()
        val newLine = FregeElementFactory.createNewLine(project).node
        if (lastImport != null) {
            val position = getInsertPositionAfterImport(lastImport)
            doInsert(position, newLine, newImport)
        } else {
            val position = getInsertPositionForFirstImport(toModule)
            val newLine2 = FregeElementFactory.createNewLine(project).node
            doInsert(position, newLine, newLine2, newImport)
        }
    }

    private fun getInsertPositionAfterImport(import: FregeImportDecl): ASTNode {
        val topDecl = import.parentOfType<FregeTopDecl>()!!
        var node = topDecl.node
        while (!node.textContains('\n')) {
            node = node.treeNext!!
        }
        return node.treeNext!!
    }

    private fun getInsertPositionForFirstImport(module: FregeProgram): ASTNode {
        return module.body?.firstChild?.node!!
    }

    private fun doInsert(insertPosition: ASTNode, vararg elements: ASTNode) {
        var lastNode = insertPosition
        val parentNode = lastNode.treeParent
        for (element in elements) {
            parentNode.addChild(element, lastNode)
            lastNode = element
        }
    }
}
