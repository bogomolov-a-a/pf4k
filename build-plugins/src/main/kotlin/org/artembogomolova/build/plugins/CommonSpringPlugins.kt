package org.artembogomolova.build.plugins

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.testing.Test
import org.springframework.boot.gradle.plugin.SpringBootPlugin

const val SPRING_BOOT_VERSION_PROPERTY_NAME = "springBootVersion"
const val DEFAULT_SPRING_BOOT_VERSION = "2.4.0"

/**
 *
 */
class CoreSpringBootPlugin : Plugin<Project> {
    private val springBootPluginApplier: SpringBootPluginApplier = SpringBootPluginApplier()
    private val springBootDependencyManagementPluginApplier: SpringBootDependencyManagementPluginApplier = SpringBootDependencyManagementPluginApplier()
    override fun apply(target: Project) {
        springBootPluginApplier.apply(target)
        springBootDependencyManagementPluginApplier.apply(target)
    }
}

internal class SpringBootPluginApplier : PluginApplier<SpringBootPlugin>(SpringBootPlugin::class.java) {

    companion object {
        const val KOTLIN_ALLOPEN_SPRING_PROFILE_PLUGIN = "kotlin-spring"
        const val SLF_4J_API_DEPENDENCY = "org.slf4j:slf4j-api"
        const val LOGBACK_CLASSIC_DEPENDENCY = "ch.qos.logback:logback-classic"
        const val LOGBACK_CORE_DEPENDENCY = "ch.qos.logback:logback-core"
        const val ROOT_VALIDATION_DEPENDENCY = "org.springframework.boot:spring-boot-starter-validation"
        const val COMMONS_LANG3_DEPENDENCY = "org.apache.commons:commons-lang3"
    }

    override fun applyAdditionalPlugins(plugins: PluginContainer, properties: MutableMap<String, Any>) {
        super.applyAdditionalPlugins(plugins, properties)
        plugins.apply("java")
        plugins.apply(KOTLIN_ALLOPEN_SPRING_PROFILE_PLUGIN)
    }

    override fun configureDependencies(target: DependencyHandler, properties: MutableMap<String, Any>) {
        super.configureDependencies(target, properties)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, SLF_4J_API_DEPENDENCY)
        target.add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, SLF_4J_API_DEPENDENCY)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, LOGBACK_CLASSIC_DEPENDENCY)
        target.add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, LOGBACK_CLASSIC_DEPENDENCY)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, LOGBACK_CORE_DEPENDENCY)
        target.add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, LOGBACK_CORE_DEPENDENCY)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, ROOT_VALIDATION_DEPENDENCY)
        target.add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, ROOT_VALIDATION_DEPENDENCY)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, COMMONS_LANG3_DEPENDENCY)
        target.add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, COMMONS_LANG3_DEPENDENCY)
    }

}

abstract class AbstractSpringBootModulePlugin : PluginApplier<CoreSpringBootPlugin>(CoreSpringBootPlugin::class.java)

/**
 *
 */
class SpringBootWebPlugin : AbstractSpringBootModulePlugin() {
    companion object {
        const val WEB_ROOT_DEPENDENCY = "org.springframework.boot:spring-boot-starter-web"
        const val DEFAULT_SERVLET_CONTAINER_DEPENDENCY = "org.springframework.boot:spring-boot-starter-tomcat"
        const val SERVLET_API_DEPENDENCY = "javax.servlet:javax.servlet-api"
    }

    override fun configureDependencies(target: DependencyHandler, properties: MutableMap<String, Any>) {
        super.configureDependencies(target, properties)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, WEB_ROOT_DEPENDENCY)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, DEFAULT_SERVLET_CONTAINER_DEPENDENCY)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, SERVLET_API_DEPENDENCY)
    }
}

/**
 *
 */
class SpringBootSecurityPlugin : AbstractSpringBootModulePlugin() {
    companion object {
        const val SECURITY_ROOT_DEPENDENCY = "org.springframework.boot:spring-boot-starter-security"
        const val SECURITY_CONFIG_DEPENDENCY = "org.springframework.security:spring-security-config"
        const val SESSION_DEPENDENCY = "org.springframework.session:spring-session-core"
    }

    override fun configureDependencies(target: DependencyHandler, properties: MutableMap<String, Any>) {
        super.configureDependencies(target, properties)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, SECURITY_ROOT_DEPENDENCY)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, SECURITY_CONFIG_DEPENDENCY)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, SESSION_DEPENDENCY)
    }
}

class SpringBootJpaPlugin : AbstractSpringBootModulePlugin() {
    companion object {
        const val JPA_ROOT_DEPENDENCY = "org.springframework.boot:spring-boot-starter-data-jpa"
        const val DEFAULT_MIGRATION_TOOL_DEPENDENCY = "org.liquibase:liquibase-core"
        const val DEFAULT_ORM_DEPENDENCY = "org.hibernate:hibernate-core"
        const val DEFAULT_STATIC_MODEL_GEN_DEPENDENCY = "org.hibernate:hibernate-jpamodelgen"
        const val KOTLIN_JPA_PLUGIN_ID = "kotlin-jpa"
    }

    override fun applyAdditionalPlugins(plugins: PluginContainer, properties: MutableMap<String, Any>) {
        super.applyAdditionalPlugins(plugins, properties)
        plugins.apply(KOTLIN_JPA_PLUGIN_ID)
    }

    override fun configureDependencies(target: DependencyHandler, properties: MutableMap<String, Any>) {
        super.configureDependencies(target, properties)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, JPA_ROOT_DEPENDENCY)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, DEFAULT_MIGRATION_TOOL_DEPENDENCY)
        target.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, DEFAULT_ORM_DEPENDENCY)
        target.add(KAPT_CONFIGURATION_NAME, DEFAULT_STATIC_MODEL_GEN_DEPENDENCY)
    }
}

const val SPRING_ACTIVE_PROFILE_ENV_NAME = "spring_profiles_active"
const val SPRING_ACTIVE_PROFILE_ENV_VALUE = "test"

class SpringBootTestPlugin : AbstractSpringBootModulePlugin() {
    companion object {
        const val TEST_ROOT_DEPENDENCY = "org.springframework.boot:spring-boot-starter-test"
        const val DEFAULT_TEST_ENGINE_DEPENDENCY = "org.junit.jupiter:junit-jupiter-engine"

    }

    override fun configureDependencies(target: DependencyHandler, properties: MutableMap<String, Any>) {
        super.configureDependencies(target, properties)
        target.add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, TEST_ROOT_DEPENDENCY)
        target.add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, DEFAULT_TEST_ENGINE_DEPENDENCY)
    }

    override fun configureTasks(target: TaskContainer, properties: MutableMap<String, Any>) {
        super.configureTasks(target, properties)
        target.withType(Test::class.java) {
            environment[SPRING_ACTIVE_PROFILE_ENV_NAME] = SPRING_ACTIVE_PROFILE_ENV_VALUE

        }
    }
}

internal class SpringBootDependencyManagementPluginApplier : PluginApplier<DependencyManagementPlugin>(DependencyManagementPlugin::class.java) {
    companion object {
        const val EXTENSION_NAME = "dependencyManagement"
        const val SPRING_BOOT_BOM_DEPENDENCY_COORDINATE_FORMAT = "org.springframework.boot:spring-boot-dependencies:%s"
    }

    override fun configureExtensions(target: ExtensionContainer, properties: MutableMap<String, Any>) {
        super.configureExtensions(target, properties)
        val extension: DependencyManagementExtension = target.getByName(EXTENSION_NAME) as DependencyManagementExtension
        val springBootVersion = properties[SPRING_BOOT_VERSION_PROPERTY_NAME] ?: DEFAULT_SPRING_BOOT_VERSION
        extension.imports {
            mavenBom(SPRING_BOOT_BOM_DEPENDENCY_COORDINATE_FORMAT.format(springBootVersion))
        }
    }

    override fun isAllowReApplyPluginAfterConfigure(pluginClass: Class<DependencyManagementPlugin>): Boolean = true
}
