# pf4k

Plugin framework for Kotlin

Based on ideas:

- https://www.gunsmoker.ru/2011/12/delphi.html (older, but some idea is actually)
- https://github.com/pf4j/pf4j (plugin framework for java)

## Motivation

I want to develop modular application a long time ago. I start developing in 2005 on Delphi. I stumbled upon an article
series https://www.gunsmoker.ru/2011/12/delphi.html, and my eyes lit up! Delphi hadn't modularity and COM seemed too heavy for my small applications.

Further I learned Java and I work Java developer till now.

I stumbled upon an repository  https://github.com/pf4j/pf4j
and I wanted to develop an analogous framework for Kotlin, because this language interested me.

### WHY I DON'T USE OSGI?

OSGi as COM too heavy. I don't want it in my applications.

### WHY I DON'T USE MICROSERVICES?

I haven't money for clouds, and my apps are pet projects.

### MAY BE SPRING?

Yes, I use spring framework in my current work but.... clouds!)

### MAY BE LEARNING?

Bingo! I want to develop this framework, because:

- I want to learn Kotlin to deeper(Kotlin developer).
- I want to learn the multithreading to deeper.
- I want to learn.... framework developing? Patterns? I want to apply they to real work.
- And I want, of course, learn modularity to deeper.

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
