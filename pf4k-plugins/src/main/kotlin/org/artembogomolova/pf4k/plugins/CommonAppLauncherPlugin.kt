package org.artembogomolova.pf4k.plugins

import java.util.jar.Attributes
import org.artembogomolova.pf4k.api.app.APPLICATION_NAME_ATTRIBUTE_NAME
import org.artembogomolova.pf4k.impl.app.DefaultModularizedApplication
import org.artembogomolova.pf4k.impl.launcher.DefaultApplicationLauncher
import org.artembogomolova.pf4k.logger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.file.FileTree
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskContainer
import org.gradle.jvm.tasks.Jar

/**
 *
 */
class CommonAppLauncherPlugin : Plugin<Project> {

    companion object {
        const val ID = "common-app-launcher-plugin"
        const val PF4K_VERSION_PROJECT_PROPERTY = "pf4k.version"
        const val PF4K_VERSION_DEFAULT = "SNAPSHOT"
        const val APPLICATION_NAME_PROJECT_PROPERTY = "application.name"
        private const val CLASS_PATH_ENTRY_NAME_PATTERN = " common/%s"
        private const val COPY_COMMON_DEPENDENCIES_PATH_PATTERN = "%s/libs/common"
        private const val PF4K_GROUP = "org.artembogomolova.pf4k"
        private const val API_DEPENDENCY_PATTERN = "$PF4K_GROUP:pfk4-api:%s"
        private const val BASIC_IMPL_DEPENDENCY_PATTERN = "$PF4K_GROUP:pfk4-basic-impl:%s"
        private const val CORE_MODULE_IMPL_DEPENDENCY_PATTERN = "$PF4K_GROUP%:pfk4-core-module-impl:%s"
        private const val PLUGGABLE_MODULE_IMPL_DEPENDENCY_PATTERN = "$PF4K_GROUP%:pfk4-pluggable-module-impl:%s"
        private const val MODULE_MANAGEMENT_IMPL_DEPENDENCY_PATTERN = "$PF4K_GROUP%:pfk4-module-management-impl:%s"
        private const val APP_IMPL_DEPENDENCY_PATTERN = "$PF4K_GROUP%:pfk4-app-impl:%s"
        private const val APP_LAUNCHER_IMPL_DEPENDENCY_PATTERN = "$PF4K_GROUP:pfk4-app-launcher-impl:%s"

    }

    private val log = logger(CommonAppLauncherPlugin::class)

    override fun apply(target: Project) {
        log.info("applying '$ID' plugin")
        @Suppress("UNCHECKED_CAST")
        val properties: Map<String, String?> = target.properties as Map<String, String?>
        configureDependencies(target.dependencies, properties)
        applyDependPlugins(target.plugins)
        configureTasks(target.tasks, target, properties)
        log.info("'$ID' plugin applied")
    }

    private fun configureDependencies(dependencies: DependencyHandler, properties: Map<String, String?>) {
        dependencies.add(
            JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
            getDependencyNotationWithVersion(API_DEPENDENCY_PATTERN, properties)
        )
        dependencies.add(
            JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
            getDependencyNotationWithVersion(BASIC_IMPL_DEPENDENCY_PATTERN, properties)
        )
        dependencies.add(
            JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
            getDependencyNotationWithVersion(CORE_MODULE_IMPL_DEPENDENCY_PATTERN, properties)
        )
        dependencies.add(
            JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
            getDependencyNotationWithVersion(PLUGGABLE_MODULE_IMPL_DEPENDENCY_PATTERN, properties)
        )
        dependencies.add(
            JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
            getDependencyNotationWithVersion(MODULE_MANAGEMENT_IMPL_DEPENDENCY_PATTERN, properties)
        )
        dependencies.add(
            JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
            getDependencyNotationWithVersion(APP_IMPL_DEPENDENCY_PATTERN, properties)
        )
        dependencies.add(
            JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
            getDependencyNotationWithVersion(APP_LAUNCHER_IMPL_DEPENDENCY_PATTERN, properties)
        )

    }

    private fun getDependencyNotationWithVersion(pattern: String, properties: Map<String, String?>): String {
        val result = pattern.format(properties[PF4K_VERSION_PROJECT_PROPERTY] ?: PF4K_VERSION_DEFAULT)
        log.debug("dependency '$result' added to project")
        return result
    }

    private fun configureTasks(
        tasks: TaskContainer,
        target: Project,
        properties: Map<String, String?>
    ) {
        val dependenciesSet = target.configurations
            .getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
            .asFileTree
        configureCopyDependenciesTask(tasks, dependenciesSet, target.buildDir.absolutePath)
        configureJarTask(tasks, dependenciesSet, properties, target.name)
    }

    private fun configureJarTask(
        tasks: TaskContainer,
        dependenciesSet: FileTree,
        properties: Map<String, String?>,
        projectName: String
    ) {
        tasks.withType(Jar::class.java) {
            manifest {
                attributes[Attributes.Name.CLASS_PATH.toString()] = getClassPath(dependenciesSet)
                attributes[Attributes.Name.MAIN_CLASS.toString()] = getMainClassName(properties)
                attributes[DefaultApplicationLauncher.APPLICATION_IMPL_CLASS_NAME_PROPERTY] = getApplicationClassName(properties)
                attributes[APPLICATION_NAME_ATTRIBUTE_NAME] = getApplicationName(properties, projectName)
            }
            log.debug("print manifest properties")
            manifest.attributes.forEach {
                log.debug("attribute: ${it.key}, value: ${it.value}")
            }
            finalizedBy(CopyRuntimeClassPathDependenciesTask.NAME)
        }
    }

    private fun getApplicationName(properties: Map<String, String?>, projectName: String): String = properties[APPLICATION_NAME_PROJECT_PROPERTY] ?: projectName

    private fun getApplicationClassName(properties: Map<String, String?>): String =
        properties[DefaultApplicationLauncher.APPLICATION_IMPL_CLASS_NAME_PROJECT_PROPERTY_NAME] ?: DefaultModularizedApplication::class.java.name

    private fun getMainClassName(properties: Map<String, String?>): String =
        properties[Attributes.Name.MAIN_CLASS.toString()] ?: DefaultApplicationLauncher::class.java.name

    private fun configureCopyDependenciesTask(
        tasks: TaskContainer,
        dependenciesSet: FileTree,
        buildDir: String
    ) {
        tasks.register(
            CopyRuntimeClassPathDependenciesTask.NAME,
            CopyRuntimeClassPathDependenciesTask::class.java
        ) {
            dependenciesSet.forEach {
                from(it.absolutePath)
                log.info("copying from '${it.absolutePath}'")
            }
            val targetDependencyDir = COPY_COMMON_DEPENDENCIES_PATH_PATTERN.format(buildDir)
            into(targetDependencyDir)
            log.info("copying into '$targetDependencyDir'")
        }
        log.info("task with name ${CopyRuntimeClassPathDependenciesTask.NAME} registered")
    }

    /*
    Based on
    * https://stackoverflow.com/questions/33758244/add-classpath-in-manifest-file-of-jar-in-gradle-in-java-8
    */
    private fun getClassPath(dependenciesSet: FileTree): String {
        var result = ""
        var i = 0
        dependenciesSet.forEach {
            val classpathEntryName = CLASS_PATH_ENTRY_NAME_PATTERN.format(it.name)
            result += if (i++ == 0) {
                String.format("%0\$-60s", classpathEntryName)
            } else {
                String.format("%0\$-71s", classpathEntryName)
            }
        }
        log.debug("generated classpath manifest attribute value: $result")
        return result
    }

    private fun applyDependPlugins(plugins: PluginContainer) {
        applyApplicationPlugin(plugins)
    }

    private fun applyApplicationPlugin(plugins: PluginContainer) {
        log.debug("applying 'application' plugin")
        plugins.apply(ApplicationPlugin::class.java)
        log.debug("'application' plugin applied!")
    }

}

private class CopyRuntimeClassPathDependenciesTask : Copy() {
    companion object {
        const val NAME = "copyRuntimeClassPathDependencies"
    }

}