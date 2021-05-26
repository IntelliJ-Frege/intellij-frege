package com.plugin.frege.resolve

import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.intellij.util.io.exists
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Testcase for testing reference resolving in Frege plugin.
 *
 * There are two kinds of tests: files and directories.
 * * `File`. The format of testcase name is '`test file dir1 dir2 dirN filename`'.
 *   First of all specify `file`, then the relative path to the test file with slashes replaced with spaces.
 *   Also you must omit the extensions.
 * * `Directory`. The format of testcase name is '`test dir dir1 dir2 dirN filename`'.
 *   First of all specify `dir`,
 *   then the relative path to the test directory with marked `filename` (with omitted extension).
 *   It means that the whole directory '`dir1/dir2/.../dirN`' will be tested as a project
 *   and the marked `filename` will be searched for resolving reference.
 *
 * Example: '`test file parameters Guard`' means to test the file '`parameters/Guard.fr`'.
 *
 * Example: '`test dir betweenFiles binding Second`' means to test the whole directory '`betweenFiles/binding`'
 * and search for the reference to resolve in '`betweenFiles/binding/Second.fr`' or '`betweenFiles/binding/Second.java`'.
 *
 * @see [JavaCodeInsightTestFixture.getReferenceAtCaretPositionWithAssertion]
 */
class FregeResolveTest : LightJavaCodeInsightFixtureTestCase() {
    private val extensions = listOf("fr", "java")

    @BeforeEach
    override fun setUp() = super.setUp()

    @AfterEach
    override fun tearDown() = super.tearDown()

    override fun getTestDataPath(): String {
        return getTestDataPathValue().toString()
    }

    private fun getTestDataPathValue(): Path {
        return Paths.get("src", "test", "testData", "resolve").toAbsolutePath()
    }

    private fun doTest(verify: (elem: PsiElement?) -> Boolean) {
        val name = getTestName(false)
        val parts = name.split(' ').drop(1).toTypedArray()
        assertTrue("Incorrect format of test", parts.size > 1)

        val mode = parts[0]
        val fileParts = parts.copyOfRange(1, parts.size)
        val path = getTestDataPathValue().resolve(Paths.get("", *fileParts))
        val filePath = Path.of("$path.${findAppropriateExtension(path)}")

        when (mode) {
            "file" -> doTestSingleFile(filePath, verify)
            "dir" -> doTestDirectory(filePath, verify)
            else -> fail("Incorrect format of test")
        }
    }

    private fun doTestSingleFile(path: Path, verify: (elem: PsiElement?) -> Boolean) {
        doTestReference(path.toString(), verify = verify)
    }

    private fun doTestDirectory(mainFilePath: Path, verify: (elem: PsiElement?) -> Boolean) {
        val filePathString = mainFilePath.toString()
        val dirPath = mainFilePath.parent
        val files = dirPath.toFile()
            .listFiles { file -> file.isFile && extensions.contains(file.extension) }
            ?.map { it.path }
            ?.filter { it != filePathString }
            ?.toTypedArray() ?: return

        doTestReference(filePathString, *files, verify = verify)
    }

    private fun findAppropriateExtension(path: Path): String {
        for (extension in extensions) {
            val current = "$path.$extension"
            if (Path.of(current).exists()) {
                return extension
            }
        }

        throw IllegalArgumentException("Cannot find an extension.")
    }

    private fun doTestReference(vararg filePaths: String, verify: (elem: PsiElement?) -> Boolean) {
        val reference = myFixture.getReferenceAtCaretPositionWithAssertion(*filePaths)
        val resolved = reference.resolve()
        assertTrue(verify(resolved))
    }
}
