package com.plugin.frege.resolve

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.parentOfTypes
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import com.plugin.frege.FregeCodeInsightTest
import com.plugin.frege.psi.*
import org.opentest4j.AssertionFailedError
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
class FregeResolveTest : FregeCodeInsightTest() {
    override val extensions = listOf("fr", "java")

    override val testDataPathValue: Path = Paths.get("src", "test", "testData", "resolve")

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

    fun `test file bindings NoBinding`() = doNullTest()

    fun `test file bindings MultipleDefinitions1`() = doTest {
        it is FregeBinding && it.text == "sum x y = x * y"
    }

    fun `test file bindings MultipleDefinitions2`() = doTest {
        it is FregeBinding && it.text.startsWith("sum x y = sum x y")
    }

    // Testing parameters

    fun `test file parameters Parameters`() = doTest {
        it is FregeParameter && it.name == "jury"
    }

    fun `test file parameters Guard`() = doTest {
        it is FregeParameter && it.name == "n"
    }

    fun `test file parameters Lambda`() = doTest {
        it is FregeParameter && it.name == "x" && it.parentOfTypes(FregeLambda::class) != null
    }

    fun `test file parameters NoParameter`() = doNullTest()

    // There was a problem with the last symbol in an indent section.
    fun `test file parameters LastSymbol`() = doTest {
        it is FregeParameter && it.name == "x"
    }

    // Issue #66
    fun `test file parameters LambdaInDo`() = doTest {
        it is FregeParameter && it.name == "it"
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

    fun `test file operators NoOperator`() = doNullTest()

    // Issue #86
    fun `test file operators AnnotationInClass`() = doTest {
        it is FregeAnnotationItem && it.name == "++--++"
    }

    fun `test file operators dots ToBinding`() = doTest {
        it is FregeBinding && it.name == "."
    }

    fun `test file operators dots ToClassAnnotation`() = doTest {
        it is FregeAnnotationItem && it.name == "." && it.containingClass?.qualifiedName == "ToClassAnnotation.Test"
    }

    fun `test file operators word FromUsage`() = doTest {
        it is FregeBinding && it.name == "myOperator"
    }

    fun `test file operators word FromUsageToRegularBinding`() = doTest {
        it is FregeBinding && it.name == "operator"
    }

    fun `test file operators word FromAnnotation`() = doTest {
        it is FregeBinding && it.name == "myTest"
    }

    fun `test file operators word FromRegularBindingInDeclaration`() = doTest {
        it is FregeBinding && it.text == "1 `myTest` 2 = 3"
    }

    fun `test file operators word FromRegularBinding`() = doTest {
        it is FregeBinding && it.name == "kek"
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

    fun `test file where NoBindingBelow`() = doNullTest()

    fun `test file where NearestParameter`() = doTest {
        it is FregeParameter && it.name == "x" && it.parentOfTypes(FregeBinding::class)?.name == "saySmth"
    }

    fun `test file where NearestBinding`() = doTest {
        it is FregeBinding && it.text == "bindingImpl a b = a - b"
    }

    fun `test file where NoAvailableBinding`() = doNullTest()

    // Testing classes

    fun `test file classes FromInstance`() = doTest {
        it is FregeClassDecl && it.qualifiedName == "FromInstance.SuperClass"
    }

    fun `test file classes FromFunctionUsage`() = doTest {
        it is FregeAnnotationItem && it.name == "checkIfPetya"
                && it.containingClass?.qualifiedName == "FromFunctionUsage.Petya"
    }

    fun `test file classes WithConstraints`() = doTest {
        it is FregeClassDecl && it.qualifiedName == "WithConstraints.Petya"
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

    fun `test file newtype NoBinding`() = doNullTest()

    // Testing data

    fun `test file type ToDeclaration`() = doTest {
        it is FregeTypeDecl && it.qualifiedName == "ToDeclaration.NewMaybe"
    }

    fun `test file type ToOriginConstructor`() = doTest {
        it is FregeConstruct && it.name == "First"
                && it.containingClass?.qualifiedName == "ToOriginConstructor.MyData"
    }

    // Testing instance

    fun `test file instance ToBaseMethod`() = doTest {
        it is FregeAnnotationItem && it.name == "love"
                && it.containingClass?.qualifiedName == "ToBaseMethod.Petya"
    }

    fun `test file instance ToInstancedMethod`() = doTest {
        it is FregeBinding && it.name == "love"
                && it.containingClass?.qualifiedName == "ToInstancedMethod.Partner"
    }

    // Testing native data

    fun `test file nativeData FromType`() = doTest {
        it is FregeNativeDataDecl && it.qualifiedName == "FromType.JRandom"
    }

    fun `test file nativeData ToMethod`() = doTest {
        it is FregeNativeFunction && it.name == "new"
                && it.containingClass?.qualifiedName == "ToMethod.JList"
    }

    fun `test file nativeData NoMethod`() = doNullTest()

    // Testing let

    fun `test file let LetIn`() = doTest {
        it is FregeBinding && it.name == "sum"
    }

    fun `test file let LetInBraces`() = doTest {
        it is FregeBinding && it.name == "approx"
    }

    fun `test file let LetUsePreviousVariable`() = doTest {
        it is FregeBinding && it.name == "fir"
    }

    fun `test file let LetUseNextVariable`() = doTest {
        it is FregeBinding && it.name == "sec"
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

    // Testing type parameters

    fun `test file typeParameters Annotation`() = doTest {
        it is FregeTypedVarid && it.name == "all" && it.parentOfType<FregeRho>() != null
                && it.parentOfType<FregeBinding>() != null
    }

    fun `test file typeParameters Class`() = doTest {
        it is FregeTypedVarid && it.name == "show" && it.parentOfType<FregeAnnotation>() == null
    }

    fun `test file typeParameters Label`() = doTest {
        it is FregeTypedVarid && it.name == "a"
    }

    fun `test file typeParameters NativeData`() = doTest {
        it is FregeTypedVarid && it.name == "element" && it.parentOfType<FregeNativeFunction>() == null
    }

    fun `test file typeParameters Newtype`() = doTest {
        it is FregeTypedVarid && it.name == "pet" && it.parentOfType<FregeAnnotation>() == null
    }

    fun `test file typeParameters Type`() = doTest {
        it is FregeTypedVarid && it.name == "maybe" && it.parentOfType<FregeSigma>() == null
    }

    // Testing labels

    fun `test file labels Label`() = doTest {
        it is FregeLabel && it.name == "test1" && it.containingClass?.qualifiedName == "Label.Hello"
    }

    // Testing program

    fun `test file program ToProgram`() = doTest {
        it is FregeProgram && it.qualifiedName == "other.ToProgram"
    }

    fun `test file program ToInnerData`() = doTest {
        it is FregeDataDecl && it.qualifiedName == "there.ToInnerData.Mine"
    }

    fun `test file program NoInnerData`() = doNullTest()

    // Testing between files

    fun `test dir betweenFiles binding Second`() = doTest {
        it is FregeBinding && it.name == "sayHello" && it.containingClass?.qualifiedName == "pack.First"
    }

    fun `test dir betweenFiles qualifiedBinding First`() = doTest {
        it is FregeBinding && it.name == "check" && it.containingClass?.qualifiedName == "Second"
    }

    // Issue #68
    fun `test dir betweenFiles operators dotOperator First`() = doTest {
        it is FregeBinding && it.name == "." && it.containingClass?.qualifiedName == "Second"
    }

    fun `test dir betweenFiles operators operatorWithDots First`() = doTest {
        it is FregeBinding && it.name == "*..*" && it.containingClass?.qualifiedName == "Second"
    }

    fun `test dir betweenFiles operators wordOperator First`() = doTest {
        it is FregeBinding && it.name == "myKek" && it.containingClass?.qualifiedName == "Second"
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

    fun `test dir betweenFiles data noReference Usage`() = doNullTest()

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

    fun `test dir betweenFiles imports public publicSequence Fourth`() = doTest {
        it is FregeBinding && it.name == "test1" && it.containingClass?.qualifiedName == "first.First"
    }

    fun `test dir betweenFiles imports public notPublicSequence Fourth`() = doNullTest()

    fun `test dir betweenFiles imports public publicClassImport Third`() = doTest {
        it is FregeDataDecl && it.qualifiedName == "first.First.Kek"
    }

    fun `test dir betweenFiles imports public publicClassImport2 Third`() = doTest {
        it is FregeDataDecl && it.qualifiedName == "first.First.Kek"
    }

    fun `test dir betweenFiles imports public notPublicClassImport Third`() = doNullTest()

    fun `test dir betweenFiles imports public publicNoItems Hello`() = doNullTest()

    fun `test dir betweenFiles imports public publicBinding Usage`() = doTest {
        it is FregeBinding && it.name == "sayHello"
                && it.containingClass?.qualifiedName == "second.Second"
    }

    fun `test dir betweenFiles imports public notPublicBinding Usage`() = doNullTest()

    fun `test dir betweenFiles imports alias importAliasClass Jury`() = doTest {
        it is FregeDataDecl && it.qualifiedName == "ru.hse.Petya.Lover"
    }

    fun `test dir betweenFiles imports alias noImportAliasClass Third`() = doNullTest()

    fun `test dir betweenFiles imports alias toImportAlias Alias`() = doTest {
        it is FregeImportDeclAlias && it.name == "Oth"
    }

    fun `test dir betweenFiles imports alias importAliasBinding Mine`() = doTest {
        it is FregeBinding && it.name == "getKek" && it.containingClass?.qualifiedName == "other.Other"
    }

    fun `test dir betweenFiles imports alias noImportAliasBinding Mine`() = doNullTest()

    fun `test dir betweenFiles imports list toClass Usage`() = doTest {
        it is FregeDataDecl && it.qualifiedName == "other.Other.Kek"
    }

    fun `test dir betweenFiles imports list noClass Usage`() = doNullTest()

    fun `test dir betweenFiles imports list toBinding Usage`() = doTest {
        it is FregeBinding && it.name == "love" && it.containingClass?.qualifiedName == "lover.Lover"
    }

    fun `test dir betweenFiles imports list noBinding Usage`() = doNullTest()

    fun `test dir betweenFiles imports list toOperator First`() = doTest {
        it is FregeBinding && it.name == "+++--" && it.containingClass?.qualifiedName == "Second"
    }

    fun `test dir betweenFiles imports list noOperator First`() = doNullTest()

    // Issue #85
    fun `test dir betweenFiles imports module withImportList First`() = doTest {
        it is FregeProgram && it.name == "Second"
    }

    fun `test dir betweenFiles bindingMultipleDefinitions First`() = doTest {
        it is FregeBinding && it.containingClass?.qualifiedName == "First"
    }

    fun `test dir betweenFiles accessModifiers public binding First`() = doTest {
        it is FregeBinding && it.name == "hello" && it.containingClass?.qualifiedName == "Second"
    }

    fun `test dir betweenFiles accessModifiers public data First`() = doTest {
        it is FregeDataDecl && it.qualifiedName == "second.Second.TestData"
    }

    fun `test dir betweenFiles accessModifiers protected binding First`() = doTest {
        it is FregeBinding && it.name == "test" && it.containingClass?.qualifiedName == "Second"
    }

    fun `test dir betweenFiles accessModifiers protected noBinding First`() = doNullTest()

    fun `test dir betweenFiles accessModifiers protected data First`() = doTest {
        it is FregeDataDecl && it.qualifiedName == "Second.MyData"
    }

    fun `test dir betweenFiles accessModifiers protected noData First`() = doNullTest()

    fun `test dir betweenFiles accessModifiers protected noConstructor First`() = doNullTest()

    fun `test dir betweenFiles accessModifiers private noBinding First`() = doNullTest()

    fun `test dir betweenFiles accessModifiers private noBinding2 First`() = doNullTest()

    fun `test dir betweenFiles accessModifiers private noData First`() = doNullTest()

    fun `test dir betweenFiles accessModifiers private noData2 First`() = doNullTest()

    // Testing from Java

    fun `test dir fromJava ToModule JavaClass`() = doTest {
        it is FregeProgram && it.qualifiedName == "main.MainModule"
    }

    fun `test dir fromJava ToBinding BindingUsage`() = doTest {
        it is FregeBinding && it.name == "sayHello" && it.containingClass?.qualifiedName == "hello.Binding"
    }


    private fun doTest(verify: (elem: PsiElement?) -> Boolean) {
        val name = getTestName(false)
        val parts = name.split(' ').drop(1).toTypedArray()
        assertTrue("Incorrect format of test", parts.size > 1)

        val mode = parts[0]
        val fileParts = parts.copyOfRange(1, parts.size)
        val path = Paths.get("", *fileParts)
        val filePath = findFileWithoutExtension(path)

        when (mode) {
            "file" -> doTestSingleFile(filePath, verify)
            "dir" -> doTestDirectory(filePath, verify)
            else -> fail("Incorrect format of test")
        }
    }

    private fun doNullTest() {
        doTest { it == null }
    }

    private fun doTestSingleFile(path: Path, verify: (elem: PsiElement?) -> Boolean) {
        doTestReference(path.toString(), verify = verify)
    }

    private fun doTestDirectory(mainFilePath: Path, verify: (elem: PsiElement?) -> Boolean) {
        val filePathString = mainFilePath.toString()
        val dirPath = testDataPathValue.resolve(mainFilePath.parent)
        val files = dirPath.toFile()
            .listFiles { file -> file.isFile && extensions.contains(file.extension) }
            ?.map { it.relativeTo(testDataPathValue.toFile()) }
            ?.map { it.path }
            ?.filter { it != filePathString }
            ?.toTypedArray() ?: throw AssertionFailedError("Cannot find directory: $dirPath")

        doTestReference(filePathString, *files, verify = verify)
    }

    private fun doTestReference(vararg filePaths: String, verify: (elem: PsiElement?) -> Boolean) {
        val reference = myFixture.getReferenceAtCaretPositionWithAssertion(*filePaths)
        val resolved = reference.resolve()
        assertTrue(verify(resolved))
    }
}
