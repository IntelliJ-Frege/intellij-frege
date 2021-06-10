package com.plugin.frege.refactor

import com.plugin.frege.FregeCodeInsightTest
import junit.framework.TestCase
import java.nio.file.Path
import java.nio.file.Paths

class FregeRenameTest : FregeCodeInsightTest() {
    override val extensions = listOf("fr")

    override val testDataPathValue: Path =
        Paths.get("src", "test", "testData", "rename")

    fun `test binding functionName Binding`() = doTest("newFunction")

    fun `test binding operator Operator`() = doTest("**+--+**")

    fun `test binding quotes Quotes`() = doTest("test''")

    fun `test binding fromQuotes Quotes`() = doTest("test'")

    fun `test data name Data`() = doTest("Petya")

    fun `test data constructor Constructor`() = doTest("Jury")

    fun `test typeParameter class Class`() = doTest("kirill")

    fun `test typeParameter annotation Annotation`() = doTest("b")

    fun `test typeParameter data Data`() = doTest("kek")

    fun `test label Label`() = doTest("getNow")

    private fun doTest(newName: String) {
        val name = getTestName(false)
        val parts = name.split(' ').drop(1).toTypedArray()
        TestCase.assertTrue("Incorrect format of tests", parts.isNotEmpty())

        val path = Paths.get("", *parts).toString()
        val beforeRename = findFileWithoutExtension(Path.of(path + "Before"))
        val afterRename = findFileWithoutExtension(Path.of(path + "After"))
        doTest(beforeRename, afterRename, newName)
    }

    private fun doTest(beforeRename: Path, afterRename: Path, newName: String) {
        myFixture.configureByFile(beforeRename.toString())
        myFixture.testRename(afterRename.toString(), newName)
    }
}
