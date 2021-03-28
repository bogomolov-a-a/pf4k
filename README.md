# pf4k

Plugin framework for Kotlin

Based on ideas:

- https://www.gunsmoker.ru/2011/12/delphi.html (older, but some idea is actually)
- https://github.com/pf4j/pf4j (plugin framework for java)

## Project quality

### Summary

![CI](https://github.com/bogomolov-a-a/pf4k/workflows/CI/badge.svg)

[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=pf4k&metric=ncloc)](https://sonarcloud.io/dashboard?id=pf4k)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=pf4k&metric=alert_status)](https://sonarcloud.io/dashboard?id=pf4k)

### Code Quality

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=pf4k&metric=coverage)](https://sonarcloud.io/dashboard?id=pf4k)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=pf4k&metric=code_smells)](https://sonarcloud.io/dashboard?id=pf4k)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=pf4k&metric=sqale_index)](https://sonarcloud.io/dashboard?id=pf4k)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=pf4k&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=pf4k)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=pf4k&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=pf4k)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=pf4k&metric=sqale_index)](https://sonarcloud.io/dashboard?id=pf4k)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=pf4k&metric=bugs)](https://sonarcloud.io/dashboard?id=pf4k)

### Code ratings

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=pf4k&metric=security_rating)](https://sonarcloud.io/dashboard?id=pf4k)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=pf4k&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=pf4k)

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
