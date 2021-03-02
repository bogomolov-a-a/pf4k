package org.artembogomolova.build.plugins

import org.artembogomolova.build.plugins.CodeCoveragePlugin.Companion.JACOCO_XML_REPORT_FILE_PATH
import org.artembogomolova.build.utils.excludeGeneratedModelClasses
import org.artembogomolova.build.utils.findCoverageClasses
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestOutputEvent
import org.gradle.api.tasks.testing.TestOutputListener
import org.gradle.api.tasks.testing.TestResult
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jacoco.core.analysis.ICounter
import org.jacoco.core.analysis.ICoverageNode
import java.io.File
import java.math.BigDecimal

internal class CodeCoveragePlugin : Plugin<Project> {
    companion object {
        const val JACOCO_XML_REPORT_FILE_PATH = "%s/reports/jacoco/jacoco.xml"
    }

    private val jacocoPluginApplier: JacocoPluginApplier = JacocoPluginApplier()
    override fun apply(target: Project) {
        jacocoPluginApplier.apply(target)
    }

}

private class JacocoPluginApplier : PluginApplier<JacocoPlugin>(JacocoPlugin::class.java) {
    companion object {
        val TEST_CLASS_NAME_PATTERNS = listOf("*Test")
        const val CLASS_DUMP_DIR_RELATIVE_PATH = "%s/jacoco/classpathdumps"
        const val JACOCO_XML_REPORT_FILE_PROPERTY_NAME = "jacocoXmlReportFileName"
        const val CLASS_DUMP_DIR_PROPERTY_NAME = "classDumpDir"
        const val COVERAGE_RATIO_MINIMUM = 0.949
        const val NO_TEST_FOUND_MESSAGE = "Error occurred test execute: no tests found!"

    }

    override fun configureProperties(properties: MutableMap<String, Any>, target: Project) {
        super.configureProperties(properties, target)
        properties[CLASS_DUMP_DIR_PROPERTY_NAME] = CLASS_DUMP_DIR_RELATIVE_PATH.format(properties[BUILD_DIR_PATH_PROPERTY_NAME])
        properties[JACOCO_XML_REPORT_FILE_PROPERTY_NAME] = JACOCO_XML_REPORT_FILE_PATH.format(properties[BUILD_DIR_PATH_PROPERTY_NAME])
    }

    override fun configureTasks(target: TaskContainer, properties: MutableMap<String, Any>) {
        super.configureTasks(target, properties)
        configureJacocoTestIntegrationTask(target, properties)
        configureJacocoReportTask(target, properties)
        configureJacocoVerificationTask(target, properties)
    }

    private fun configureJacocoTestIntegrationTask(target: TaskContainer, properties: MutableMap<String, Any>) {


        target.withType(Test::class.java) {

            useJUnitPlatform()
            failFast = true
            ignoreFailures = false
            setTestNameIncludePatterns(TEST_CLASS_NAME_PATTERNS)
            doFirst {
                if (testClassesDirs.isEmpty) {
                    throw IllegalStateException(NO_TEST_FOUND_MESSAGE)
                }
                extensions.configure(JacocoTaskExtension::class.java) {
                    this.classDumpDir = File(properties[CLASS_DUMP_DIR_PROPERTY_NAME] as String)

                }
            }
            addTestListener(object : TestListener {
                override fun beforeSuite(suite: TestDescriptor) {
                    println("starting suite $suite")
                }

                override fun beforeTest(testDescriptor: TestDescriptor) {
                    println("Running test $testDescriptor")
                }

                override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
                    println("test $testDescriptor ended successful")
                }

                override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                    if (result.testCount == 0L) {
                        throw IllegalStateException(NO_TEST_FOUND_MESSAGE)
                    }
                    println("suite '${suite.name}' ended. Results:")
                    println("successful : ${result.successfulTestCount}")
                    println("failed : ${result.failedTestCount}")
                    println("skipped : ${result.skippedTestCount}")
                    println("total : ${result.testCount}")
                }
            })
            addTestOutputListener(
                object : TestOutputListener {
                    override fun onOutput(testDescriptor: TestDescriptor?, outputEvent: TestOutputEvent?) {
                        if (System.getProperty("testWithoutLogs") != null) {
                            return
                        }
                        println(outputEvent?.message)
                    }
                }
            )

        }
    }

    private fun configureJacocoVerificationTask(target: TaskContainer, properties: MutableMap<String, Any>) {

        target.withType(JacocoCoverageVerification::class.java) {

            doFirst {
                val coverageClasses = findCoverageClasses(properties, ::excludeGeneratedModelClasses)
                with(violationRules) {
                    println("start creating rules")
                    rule {
                        this.includes = coverageClasses.first
                        this.excludes = coverageClasses.second
                        limit {
                            println("creating ${ICoverageNode.CounterEntity.METHOD.name} with ${ICounter.CounterValue.COVEREDRATIO.name} equals $COVERAGE_RATIO_MINIMUM")
                            counter = ICoverageNode.CounterEntity.METHOD.name
                            value = ICounter.CounterValue.COVEREDRATIO.name
                            minimum = BigDecimal.valueOf(COVERAGE_RATIO_MINIMUM)
                        }
                    }
                    rule {
                        this.includes = coverageClasses.first
                        this.excludes = coverageClasses.second
                        limit {
                            println("creating ${ICoverageNode.CounterEntity.CLASS.name} with ${ICounter.CounterValue.COVEREDRATIO.name} equals $COVERAGE_RATIO_MINIMUM")
                            counter = ICoverageNode.CounterEntity.CLASS.name
                            value = ICounter.CounterValue.COVEREDRATIO.name
                            minimum = BigDecimal.valueOf(COVERAGE_RATIO_MINIMUM)

                        }
                    }
                    rule {
                        this.includes = coverageClasses.first
                        this.excludes = coverageClasses.second
                        limit {
                            println("creating ${ICoverageNode.CounterEntity.LINE.name} with ${ICounter.CounterValue.COVEREDRATIO.name} equals $COVERAGE_RATIO_MINIMUM")
                            counter = ICoverageNode.CounterEntity.LINE.name
                            value = ICounter.CounterValue.COVEREDRATIO.name
                            minimum = BigDecimal.valueOf(COVERAGE_RATIO_MINIMUM)

                        }
                    }
                    rule {
                        this.includes = coverageClasses.first
                        this.excludes = coverageClasses.second
                        limit {
                            println("creating ${ICoverageNode.CounterEntity.BRANCH.name} with ${ICounter.CounterValue.COVEREDRATIO.name} equals $COVERAGE_RATIO_MINIMUM")
                            counter = ICoverageNode.CounterEntity.BRANCH.name
                            value = ICounter.CounterValue.COVEREDRATIO.name
                            minimum = BigDecimal.valueOf(COVERAGE_RATIO_MINIMUM)

                        }
                    }
                    rule {
                        this.includes = coverageClasses.first
                        this.excludes = coverageClasses.second
                        limit {
                            println("creating ${ICoverageNode.CounterEntity.INSTRUCTION.name} with ${ICounter.CounterValue.COVEREDRATIO.name} equals $COVERAGE_RATIO_MINIMUM")
                            counter = ICoverageNode.CounterEntity.INSTRUCTION.name
                            value = ICounter.CounterValue.COVEREDRATIO.name
                            minimum = BigDecimal.valueOf(COVERAGE_RATIO_MINIMUM)

                        }
                    }
                    rule {
                        this.includes = coverageClasses.first
                        this.excludes = coverageClasses.second
                        limit {
                            println("creating ${ICoverageNode.CounterEntity.COMPLEXITY.name} with ${ICounter.CounterValue.COVEREDRATIO.name} equals $COVERAGE_RATIO_MINIMUM")
                            counter = ICoverageNode.CounterEntity.COMPLEXITY.name
                            value = ICounter.CounterValue.COVEREDRATIO.name
                            minimum = BigDecimal.valueOf(COVERAGE_RATIO_MINIMUM)

                        }
                    }
                }
                println("test coverage verification rules created")
            }
        }
    }

    private fun configureJacocoReportTask(target: TaskContainer, properties: MutableMap<String, Any>) {
        target.withType(JacocoReport::class.java) {

            reports {
                /*only XML reports*/
                csv.isEnabled = false
                html.isEnabled = false
                xml.isEnabled = true
                xml.destination = File(properties[JACOCO_XML_REPORT_FILE_PROPERTY_NAME] as String)
            }
        }
    }
}