<a href="https://github.com/IntelliJ-Frege/intellij-frege/">
    <img src="https://raw.githubusercontent.com/IntelliJ-Frege/intellij-frege/master/src/main/resources/META-INF/pluginIcon.svg" alt="Plugin logo" title="Plugin logo" align="right" height="60" />
</a>

# IntelliJ IDEA plugin for Frege language
[![Frege Plugin Build & Test](https://github.com/IntelliJ-Frege/intellij-frege/actions/workflows/tests.yml/badge.svg)](https://github.com/IntelliJ-Frege/intellij-frege/actions/workflows/tests.yml)

## What is Frege

Frege is a Haskell for the JVM. It is functional, has strong static type system and lazy evaluations, 
but Frege compiles to Java and runs on JVM. Also, it can use any Java library and can be used inside any Java project.

Read more at [Frege github](https://github.com/Frege/frege).

## Installation

Plugin is available on [JetBrains plugin repository](https://plugins.jetbrains.com/plugin/17187-frege).

## Current progress

Features implemented partially or completely:
- Lexer
- Parser 
- Syntax highlighting and color schemes
- Gradle-based build system
- Navigation
- Find usages
- Code completion
- Rename refactoring
- File templates
- Index files
- Frege REPL
- 'Add import' quickfix for unresolved references
- Documentation viewer

Features desired:
- Structure view
- Formatter
- More quickfixes
- More refactorings
- Type system
- and more


## Examples

Below you can see examples of our plugin:

- Navigation
  <br></br>
  ![Navigation example](assets/images/Navigation1.png)
  <br></br>
  ![Navigation example](assets/images/Navigation2.png)
  <br></br>
  ![Navigation example](assets/images/Navigation3.png)
  <br></br>

- Navigation depends on imports
  <br></br>
  ![Navigation imports example](assets/gifs/NavigationImports.gif)
  <br></br>

- Navigation to Java (and other JVM-languages)
  <br></br>
  ![Navigation to Java example](assets/gifs/NavigationToJava.gif)
  <br></br>

- Navigation from Java (and other JVM-languages)
  <br></br>
  ![Navigation from Java example](assets/gifs/NavigationFromJava.gif)
  <br></br>

- Line markers
  - To type annotation
    <br></br>
    ![Line marker annotation example](assets/gifs/LineMarkerAnnotation.gif)
    <br></br>
  - To instanced methods and vice versa
    <br></br>
    ![Line marker to from instance](assets/gifs/LineMarkerToFromInstance.gif)
    <br></br>
  - Run Frege
    <br></br>
    ![Line marker run Frege](assets/gifs/RunFrege.gif)
    <br></br>

- Find usages
  <br></br>
  ![Find usages](assets/images/FindUsages.png)
  <br></br>

- Autocompletion
  <br></br>
  ![Autocompletion](assets/images/Autocompletion1.png)
  <br></br>
  ![Autocompletion](assets/images/Autocompletion2.png)
  <br></br>
  
- Rename refactoring
  <br></br>
  ![Rename refactoring](assets/gifs/Renaming.gif)
  <br></br>

- Add import quickfix
  <br></br>
  ![Add import quickfix](assets/gifs/AddImportQuickfix.gif)
  <br></br>

- Documentation viewer
  <br></br>
  ![Documentation viewer](assets/gifs/DocumentationViewer.gif)
  <br></br>

- REPL integration
  <br></br>
  ![REPL integration](assets/gifs/ReplIntegration.gif)
  <br></br>

- REPL: execute parts of code
  <br></br>
  ![REPL execution](assets/gifs/ReplExecution.gif)
  <br></br>
  

## Contributors

[Peter Surkov](https://github.com/psurkov/)

[Kirill Karnaukhov](https://github.com/kkarnauk)

[Jura Khudyakov](https://github.com/23jura23/)

Special thanks to [Semyon Atamas](https://github.com/satamas) and [Dierk König](https://github.com/Dierk)!

## License

Apache-2.0
