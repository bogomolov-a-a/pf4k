package org.artembogomolova.build.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.sonarqube.gradle.SonarQubeExtension
import org.sonarqube.gradle.SonarQubePlugin

internal class ExternalToolIntegrationPlugin : Plugin<Project> {
    private val sonarApplier: SonarApplier = SonarApplier()

    override fun apply(target: Project) {
        sonarApplier.apply(target)
    }
}

private class SonarApplier : PluginApplier<SonarQubePlugin>(SonarQubePlugin::class.java) {

    companion object {
        const val PROJECT_KEY_PROPERTY = "sonar.projectKey"
        const val LOGIN_PROPERTY = "sonar.login"
        const val HOST_URL_PROPERTY = "sonar.host.url"
        const val ORGANIZATION_PROPERTY = "sonar.organization"
        const val PROJECT_NAME_PROPERTY = "sonar.projectName"
        const val LANGUAGE_PROPERTY = "sonar.language"
        const val VERBOSE_PROPERTY = "sonar.verbose"
    }

    override fun configureProperties(properties: MutableMap<String, Any>, target: Project) {
        super.configureProperties(properties, target)
        properties[PROJECT_KEY_PROPERTY] = target.rootProject.name
        properties[PROJECT_NAME_PROPERTY] = target.rootProject.name
    }

    override fun isAllowReApplyPluginAfterConfigure(pluginClass: Class<SonarQubePlugin>): Boolean = true
    override fun configureExtensions(target: ExtensionContainer, properties: MutableMap<String, Any>) {
        super.configureExtensions(target, properties)
        configSonarQubeExtension(target, properties)
    }

    private fun configSonarQubeExtension(target: ExtensionContainer, properties: MutableMap<String, Any>) {
        val sonarqubeTask = target.getByName(SonarQubeExtension.SONARQUBE_TASK_NAME) as SonarQubeExtension
        with(sonarqubeTask) {
            properties {
                property(PROJECT_NAME_PROPERTY, properties[PROJECT_NAME_PROPERTY] as String)
                property(PROJECT_KEY_PROPERTY, properties[PROJECT_KEY_PROPERTY] as String)
                property(LOGIN_PROPERTY, properties[LOGIN_PROPERTY] as String)
                property(HOST_URL_PROPERTY, properties[HOST_URL_PROPERTY] as String)
                property(ORGANIZATION_PROPERTY, properties[ORGANIZATION_PROPERTY] as String)
                property(LANGUAGE_PROPERTY, KOTLIN_LANGUAGE_NAME)
                property(VERBOSE_PROPERTY, true.toString())
            }
        }
    }

}
