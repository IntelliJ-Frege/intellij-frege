import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser

plugins {
    kotlin("jvm") version "1.5.10"

    id("java")
    id("idea")
    id("org.jetbrains.intellij") version "1.4.0"
    id("org.jetbrains.grammarkit") version "2021.1.2"
}

group = "com.plugin.frege"
version = "1.1.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.10")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

intellij {
    version.set("2021.1")
    plugins.set(listOf("java", "gradle", "gradle-java"))
    downloadSources.set(true)
    updateSinceUntilBuild.set(false)
}

sourceSets {
    getByName("main").java.srcDirs("src/gen")
}

tasks {
    val generateFregeLexer by registering(GenerateLexer::class) {
        source = "src/main/java/com/plugin/frege/lexer/FregeLexer.flex"
        targetDir = "src/gen/com/plugin/frege/lexer/"
        targetClass = "FregeLexer"
        purgeOldFiles = true
    }

    val generateFregeParser by registering(GenerateParser::class) {
        dependsOn(generateFregeLexer)

        source = "src/main/java/com/plugin/frege/Frege.bnf"
        targetRoot = "src/gen"
        pathToParser = "/com/plugin/frege/parser/FregeParser.java"
        pathToPsiRoot = "/com/plugin/frege/psi"
        purgeOldFiles = true
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
