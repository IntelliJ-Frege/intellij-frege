package com.plugin.frege.resolve

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.parentOfTypes
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.intellij.util.io.exists
import com.plugin.frege.psi.*
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

    // Testing bindings

    fun `test file bindings FromUsage`() = doTest {
        it is FregeBinding && it.name == "func"
    }

    fun `test file bindings FromAnnotation`() = doTest {
        it is FregeBinding && it.name == "function"
    }

    fun `test file bindings FromOtherBinding`() = doTest {
        it is FregeBinding && it.text == "binding 1 2 = 10"
    }

    fun `test file bindings MultipleAnnotations`() = doTest {
        it is FregeBinding && it.name == "second"
    }

    fun `test file bindings NoBinding`() = doTest {
        it == null
    }

    // Testing parameters

    fun `test file parameters Parameters`() = doTest {
        it is FregeParameter && it.name == "jury"
    }

    fun `test file parameters Guard`() = doTest {
        it is FregeParameter && it.name == "n"
    }

    fun `test file parameters Lambda`() = doTest {
        it is FregeParameter && it.text == "x" && it.parentOfTypes(FregeLambda::class) != null
    }

    fun `test file parameters NoParameter`() = doTest {
        it == null
    }

    // Testing operators

    fun `test file operators FromUsage`() = doTest {
        it is FregeBinding && it.name == "++***+"
    }

    fun `test file operators SingleCharOperator`() = doTest {
        it is FregeBinding && it.name == "$"
    }

    fun `test file operators FromInfix`() = doTest {
        it is FregeBinding && it.name == "+*+"
    }

    fun `test file operators PrefixNotation`() = doTest {
        it is FregeBinding && it.name == "$+*+"
    }

    fun `test file operators NoOperator`() = doTest {
        it == null
    }

    // Testing where

    fun `test file where BindingBelow`() = doTest {
        it is FregeBinding && it.name == "calculate"
    }

    fun `test file where BindingAbove`() = doTest {
        it is FregeBinding && it.name == "sayHello"
    }

    fun `test file where ParameterAbove`() = doTest {
        it is FregeParameter && it.name == "y"
    }

    fun `test file where NoBindingBelow`() = doTest {
        it == null
    }

    fun `test file where NearestParameter`() = doTest {
        it is FregeParameter && it.name == "x" && it.parentOfTypes(FregeBinding::class)?.name == "saySmth"
    }

    fun `test file where NearestBinding`() = doTest {
        it is FregeBinding && it.text == "bindingImpl a b = a - b"
    }

    fun `test file where NoAvailableBinding`() = doTest {
        it == null
    }

    // Testing classes

    fun `test file classes FromInstance`() = doTest {
        it is FregeClassDecl && it.qualifiedName == "FromInstance.SuperClass"
    }

    fun `test file classes FromFunctionUsage`() = doTest {
        it is FregeAnnotationItem && it.name == "checkIfPetya"
                && it.containingClass?.qualifiedName == "FromFunctionUsage.Petya"
    }

    // Testing data

    fun `test file data FromTypeUsage`() = doTest {
        it is FregeDataDecl && it.qualifiedName == "FromTypeUsage.MyTestData"
    }

    fun `test file data FromConstructorUsage`() = doTest {
        it is FregeConstruct && it.name == "Right"
                && it.containingClass?.qualifiedName == "FromConstructorUsage.Either"
    }

    // Testing newtype

    fun `test file newtype ToDeclaration`() = doTest {
        it is FregeNewtypeDecl && it.qualifiedName == "ToDeclaration.Hello"
    }

    fun `test file newtype ToConstructor`() = doTest {
        it is FregeConstruct && it.name == "CreateHello"
                && it.containingClass?.qualifiedName == "ToConstructor.Hello"
    }

    fun `test file newtype ToBinding`() = doTest {
        it is FregeBinding && it.name == "func"
                && it.containingClass?.qualifiedName == "ToBinding.MyType"
    }

    fun `test file newtype NoBinding`() = doTest {
        it == null
    }

    // Testing data

    fun `test file type ToDeclaration`() = doTest {
        it is FregeTypeDecl && it.qualifiedName == "ToDeclaration.NewMaybe"
    }

    fun `test file type ToOriginConstructor`() = doTest {
        it is FregeConstruct && it.name == "First"
                && it.containingClass?.qualifiedName == "ToOriginConstructor.MyData"
    }

    // Testing native data

    fun `test file nativeData FromType`() = doTest {
        it is FregeNativeDataDecl && it.qualifiedName == "FromType.JRandom"
    }

    fun `test file nativeData ToMethod`() = doTest {
        it is FregeNativeFunction && it.name == "new"
                && it.containingClass?.qualifiedName == "ToMethod.JList"
    }

    fun `test file nativeData NoMethod`() = doTest {
        it == null
    }

    // Testing let

    fun `test file let LetIn`() = doTest {
        it is FregeBinding && it.name == "sum"
    }

    fun `test file let LetInBraces`() = doTest {
        it is FregeBinding && it.name == "approx"
    }

    // Testing do

    fun `test file do ToLetVirtual`() = doTest {
        it is FregeBinding && it.name == "variable"
    }

    fun `test file do ToLetSemicolon`() = doTest {
        it is FregeBinding && it.name == "hey"
    }

    fun `test file do ToParamVirtual`() = doTest {
        it is FregeParameter && it.name == "xyz"
    }

    fun `test file do ToParamSemicolon`() = doTest {
        it is FregeParameter && it.name == "petya"
    }

    fun `test file do MultipleLet`() = doTest {
        it is FregeBinding && it.text == "hello = -1"
    }

    fun `test file do MultipleParam`() = doTest {
        it is FregeParameter && it.parentOfType<FregeDoDecl>()?.text == "name <- getStdin"
    }

    fun `test file do MultipleParam2`() = doTest {
        it is FregeParameter && it.parentOfType<FregeDoDecl>()?.text == "Just hey <- 10"
    }

    fun `test file do LetWithBraces`() = doTest {
        it is FregeBinding && it.name == "second"
    }

    // Testing case of

    fun `test file caseof FromCaseExpr`() = doTest {
        it is FregeParameter && it.name == "xss"
    }

    fun `test file caseof FromRightToLeft`() = doTest {
        it is FregeParameter && it.name == "first" && it.parentOfType<FregePattern>()?.text == "first:_"
    }

    fun `test file caseof FromDeclToParam`() = doTest {
        it is FregeParameter && it.name == "c"
    }

    fun `test file caseof WhereUnderDecl`() = doTest {
        it is FregeBinding && it.name == "hey"
    }

    fun `test file caseof WhereUnderBindingAbove`() = doTest() {
        it is FregeBinding && it.name == "calculate"
    }

    // Testing between files

    fun `test dir betweenFiles binding Second`() = doTest {
        it is FregeBinding && it.name == "sayHello" && it.containingClass?.qualifiedName == "pack.First"
    }

    fun `test dir betweenFiles qualifiedBinding First`() = doTest {
        it is FregeBinding && it.name == "check" && it.containingClass?.qualifiedName == "Second"
    }

    fun `test dir betweenFiles class fromInstance ClassUsage`() = doTest {
        it is FregeClassDecl && it.qualifiedName == "ClassDeclaration.MyClass"
    }

    fun `test dir betweenFiles class notQualified Usage`() = doTest {
        it is FregeAnnotationItem && it.name == "calc"
                && it.containingClass?.qualifiedName == "decl.ClassDeclaration.MyClass"
    }

    fun `test dir betweenFiles data toConstructor DataUsage`() = doTest {
        it is FregeConstruct && it.name == "Kirill"
                && it.containingClass?.qualifiedName == "first.DataDeclaration.PetyaFriend"
    }

    fun `test dir betweenFiles data toDeclaration Usage`() = doTest {
        it is FregeDataDecl && it.qualifiedName == "hello.world.Declaration.MyData"
    }

    fun `test dir betweenFiles data noReference Usage`() = doTest {
        it == null
    }

    fun `test dir betweenFiles nativeName ToClassWithoutString NativeName`() = doTest {
        it is PsiClass && it.qualifiedName == "my.pack.petya.Clazz"
    }

    fun `test dir betweenFiles nativeName ToClassString Usage`() = doTest {
        it is PsiClass && it.qualifiedName == "ru.hse.Petya"
    }

    fun `test dir betweenFiles nativeName ToClassStringMix FromKirill`() = doTest {
        it is PsiClass && it.qualifiedName == "project.failed.BelovedPetya"
    }

    fun `test dir betweenFiles imports toClass Usage`() = doTest {
        it is PsiClass && it.qualifiedName == "other.Clazz"
    }

    // Testing from Java

    fun `test dir fromJava ToModule JavaClass`() = doTest {
        it is FregeProgram && it.qualifiedName == "main.MainModule"
    }

    fun `test dir fromJava ToBinding BindingUsage`() = doTest {
        it is FregeBinding && it.name == "sayHello" && it.containingClass?.qualifiedName == "hello.Binding"
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
