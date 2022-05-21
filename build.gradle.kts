import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.intellij.mainSourceSet

plugins {
    kotlin("jvm") version "1.5.10"

    id("java")
    id("idea")
    id("org.jetbrains.intellij") version "1.5.3"
    id("org.jetbrains.grammarkit") version "2021.1.2"
}

val pluginVersion: String by project
val pluginGroup: String by project

val kotlinVersion: String by project
val junitVersion: String by project

val ideaVersion: String by project
val ideaPlugins: String by project
val ideaDownloadSources: String by project
val ideaUpdateSinceUntilBuild: String by project

val genPath: String by project

val flexPath: String by project
val genLexerPath: String by project
val genLexerClassName: String by project
val genLexerPurgeOldFiles: String by project

val bnfPath: String by project
val genParserClassPath: String by project
val genPsiPath: String by project
val genParserPurgeOldFiles: String by project


group = pluginGroup
version = pluginVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

intellij {
    version.set(ideaVersion)
    plugins.set(ideaPlugins.split(","))
    downloadSources.set(ideaDownloadSources.toBoolean())
    updateSinceUntilBuild.set(ideaUpdateSinceUntilBuild.toBoolean())
}

sourceSets {
    mainSourceSet(project).java.srcDir(genPath)
}

tasks {
    val generateFregeLexer by registering(GenerateLexer::class) {
        source = flexPath
        targetDir = genLexerPath
        targetClass = genLexerClassName
        purgeOldFiles = genLexerPurgeOldFiles.toBoolean()
    }

    val generateFregeParser by registering(GenerateParser::class) {
        dependsOn(generateFregeLexer)

        source = bnfPath
        targetRoot = genPath
        pathToParser = genParserClassPath
        pathToPsiRoot = genPsiPath
        purgeOldFiles = genParserPurgeOldFiles.toBoolean()
    }

    compileKotlin {
        dependsOn(generateFregeParser)
    }

    compileJava {
        dependsOn(generateFregeParser)
    }

    test {
        useJUnitPlatform()
    }
}
