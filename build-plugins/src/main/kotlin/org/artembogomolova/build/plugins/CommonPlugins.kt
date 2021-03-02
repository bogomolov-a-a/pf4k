package org.artembogomolova.build.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.TaskContainer

const val BUILD_DIR_PATH_PROPERTY_NAME = "buildDirPath"
const val ROOT_PROJECT_DIR_PATH_PROPERTY_NAME = "rootProjectDirPath"

abstract class PluginApplier<T : Plugin<out Any>>(private val pluginClass: Class<T>) : Plugin<Project> {
    final override fun apply(target: Project) {
        println("try add plugin with class ${pluginClass.name} for project '${target.name}'")
        applyPlugin(target)
        println("try to configure properties for plugin with class ${pluginClass.name}")
        @Suppress("UNCHECKED_CAST")
        val properties = target.properties as MutableMap<String, Any>
        configureProperties(properties, target)
        println("all properties for plugin with class ${pluginClass.name} configured!")
        println("try to apply additional plugins for plugin with class ${pluginClass.name}")
        applyAdditionalPlugins(target.plugins, properties)
        println("all additional plugins for plugin with class ${pluginClass.name} applied!")
        println("try to configure dependencies for plugin with class ${pluginClass.name}")
        configureDependencies(target.dependencies, properties)
        println("all dependencies for plugin with class ${pluginClass.name} configured!")
        println("try to apply additional plugins from configured dependencies for plugin with class ${pluginClass.name}")
        applyPluginFromConfiguredDependencies(target.plugins, properties)
        println("all additional plugins from configured dependencies for plugin with class ${pluginClass.name} applied!!")
        println("try to configure extensions for plugin with class ${pluginClass.name}")
        configureExtensions(target.extensions, properties)
        println("all extensions for plugin with class ${pluginClass.name} configured!")
        println("try to configure tasks for plugin with class ${pluginClass.name}")
        configureTasks(target.tasks, properties)
        println("all tasks for plugin with class ${pluginClass.name} configured!")
        println("try to apply additional plugins after configure for plugin with class ${pluginClass.name}")
        applyAdditionalPluginsAfterConfigure(target.plugins, properties)
        println("all additional plugins after configure for plugin with class ${pluginClass.name} applied!")
        applyPluginAfterConfigure(target)
        println("plugin with class ${pluginClass.name} for project '${target.name}' applied!")
    }

    protected open fun configureTasks(target: TaskContainer, properties: MutableMap<String, Any>) {
        println("plugin has no tasks to configure for project ")
    }

    protected open fun configureExtensions(target: ExtensionContainer, properties: MutableMap<String, Any>) {
        println("plugin has no extensions to configure for project ")
    }

    protected open fun configureDependencies(target: DependencyHandler, properties: MutableMap<String, Any>) {
        println("plugin has no dependencies to configure for project")
    }

    protected open fun configureProperties(properties: MutableMap<String, Any>, target: Project) {
        val buildDirPath: String = target.buildDir.absolutePath.toString()
        properties[BUILD_DIR_PATH_PROPERTY_NAME] = buildDirPath
        println("build dir path for plugin '$buildDirPath'")
        val rootProjectDirPath: String = target.rootProject.rootDir.absolutePath.toString()
        properties[ROOT_PROJECT_DIR_PATH_PROPERTY_NAME] = rootProjectDirPath
        println("root project dir path for plugin '$buildDirPath'")
    }

    protected open fun applyAdditionalPlugins(plugins: PluginContainer, properties: MutableMap<String, Any>) {
        println("plugin has no another plugin to apply for project")
    }

    private fun applyPlugin(target: Project) {
        if (isAllowApplied(target, pluginClass)) {
            target.plugins.apply(pluginClass)
            return
        }
        println("plugin already applied")
    }

    protected open fun isAllowApplied(target: Project, pluginClass: Class<T>): Boolean = !target.plugins.hasPlugin(pluginClass)

    private fun applyPluginAfterConfigure(target: Project) = if (isAllowReApplyPluginAfterConfigure(pluginClass)) {
        applyPlugin(target)
    } else {
        println("plugin can't re apply after configure")
    }

    protected open fun isAllowReApplyPluginAfterConfigure(pluginClass: Class<T>): Boolean = false

    protected open fun applyPluginFromConfiguredDependencies(plugins: PluginContainer, properties: MutableMap<String, Any>) {
        println("plugin has no another plugin from configured dependencies to apply for project")
    }

    protected open fun applyAdditionalPluginsAfterConfigure(plugins: PluginContainer, properties: MutableMap<String, Any>) {
        println("plugin has no another plugin after configure to apply for project")
    }
}