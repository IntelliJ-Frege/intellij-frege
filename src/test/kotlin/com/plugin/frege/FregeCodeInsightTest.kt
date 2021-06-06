package com.plugin.frege

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.intellij.util.io.exists
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.nio.file.Path

abstract class FregeCodeInsightTest : LightJavaCodeInsightFixtureTestCase() {
    @BeforeEach
    override fun setUp() = super.setUp()

    @AfterEach
    override fun tearDown() = super.tearDown()

    override fun getTestDataPath(): String {
        return testDataPathValue.toString()
    }

    protected abstract val testDataPathValue: Path

    protected abstract val extensions: List<String>

    protected fun findFileWithoutExtension(path: Path): Path {
        val extension = findAppropriateExtension(testDataPathValue.resolve(path))
        return Path.of("$path.$extension")
    }

    private fun findAppropriateExtension(path: Path): String {
        for (extension in extensions) {
            if (Path.of("$path.$extension").exists()) {
                return extension
            }
        }

        throw IllegalArgumentException("Cannot find an extension.")
    }
}
