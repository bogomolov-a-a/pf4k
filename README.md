# pf4k

Plugin framework for Kotlin

Based on ideas:

- https://www.gunsmoker.ru/2011/12/delphi.html (older, but some idea is actually)
- https://github.com/pf4j/pf4j (plugin framework for java)

## Motivation

[Motivation for develop](docs/motivation.md)

## Structure application

Application contains:
- application.jar -> main file with simplify manifest.
    - The manifest contains "Application-Name" attribute for using in preferences.
    - Main class - ModularityApplication. Don't extend it.
    - Run as **java -jar ${program-name}.jar -Dprogram-name="${program-name}"**, where ${program-name} - value of "Application-Name" attribute in the manifes
- modules:
    - directory "common" contains jars used for each module and main app jar.
    - directory "core" contains core module jar file and its dependencies in the subdirectory "dependencies". ModularityApplication with
      ModuleManager.ModuleLoader start core module.
    - directory "plugins" contains one or more plugged modules, each into separated directory "{uuid}". Where uuid - unique plugin identifier. Dependencies
      locate in the subdirectory "dependencies"
