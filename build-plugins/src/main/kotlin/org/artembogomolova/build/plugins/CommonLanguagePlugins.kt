package org.artembogomolova.build.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.TaskContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

const val KOTLIN_LANGUAGE_NAME = "kotlin"
const val KOTLIN_VERSION_PROPERTY_NAME = "kotlinVersion"
const val DEFAULT_KOTLIN_KOTLIN_OPTION_VALUE = "1.4.20"
const val KAPT_CONFIGURATION_NAME = "kapt"
fun getKotlinVersion(properties: MutableMap<String, Any>): String =
    properties[KOTLIN_VERSION_PROPERTY_NAME] as String? ?: DEFAULT_KOTLIN_KOTLIN_OPTION_VALUE

internal class ProjectLanguagesPlugin : Plugin<Project> {
    private val kotlinPluginApplier: KotlinPluginApplier = KotlinPluginApplier()
    override fun apply(target: Project) {
        target.plugins.apply("base")
        kotlinPluginApplier.apply(target)
    }

}

private class KotlinPluginApplier : PluginApplier<KotlinPluginWrapper>(KotlinPluginWrapper::class.java) {
    companion object {
        const val JVM_TARGET_VERSION_PROPERTY_NAME = "jvmTargetVersion"
        const val DEFAULT_JVM_TARGET_KOTLIN_OPTION_VALUE = "11"
        const val KOTLIN_ANNOTATION_PROCESSOR_PLUGIN = "kotlin-kapt"
        const val KOTLIN_REFLECT_DEPENDENCY_NOTATION = "org.jetbrains.kotlin:kotlin-reflect:%s"
        const val KOTLIN_COMPILE_EMBEDDABLE_DEPENDENCY_NOTATION = "org.jetbrains.kotlin:kotlin-compiler-embeddable:%s"
    }

    override fun applyAdditionalPlugins(plugins: PluginContainer, properties: MutableMap<String, Any>) {
        super.applyAdditionalPlugins(plugins, properties)
        plugins.apply(KOTLIN_ANNOTATION_PROCESSOR_PLUGIN)
    }

    override fun configureDependencies(target: DependencyHandler, properties: MutableMap<String, Any>) {
        super.configureDependencies(target, properties)
        val kotlinVersion = getKotlinVersion(properties)
        val kotlinReflectDependencyNotation = KOTLIN_REFLECT_DEPENDENCY_NOTATION.format(kotlinVersion)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, kotlinReflectDependencyNotation)
        target.add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, kotlinReflectDependencyNotation)
        val kotlinCompileEmbeddableDependencyNotation = KOTLIN_COMPILE_EMBEDDABLE_DEPENDENCY_NOTATION.format(kotlinVersion)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, kotlinCompileEmbeddableDependencyNotation)
        target.add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, kotlinCompileEmbeddableDependencyNotation)
    }

    override fun configureTasks(target: TaskContainer, properties: MutableMap<String, Any>) {
        super.configureTasks(target, properties)
        configureKotlinCompileTask(target, properties)
    }

    private fun configureKotlinCompileTask(target: TaskContainer, properties: MutableMap<String, Any>) {
        target.withType(KotlinCompile::class.java) {
            kotlinOptions {
                jvmTarget = properties[JVM_TARGET_VERSION_PROPERTY_NAME] as String? ?: DEFAULT_JVM_TARGET_KOTLIN_OPTION_VALUE// default 11
                allWarningsAsErrors = true
                /*https://stackoverflow.com/questions/52631827/why-cant-kotlin-result-be-used-as-a-return-type
                *
                * */
                freeCompilerArgs += "-Xallow-result-return-type"
            }

        }
    }
}
