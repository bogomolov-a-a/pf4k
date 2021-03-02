package org.artembogomolova.build.plugins

import org.artembogomolova.build.tasks.TaskRegistration
import org.gradle.api.Plugin
import org.gradle.api.Project

class CommonBuildPlugin : Plugin<Project> {
    private val repositoryApplier: Plugin<Project> = RepositoryApplier()
    private val taskRegistrations: TaskRegistration = TaskRegistration()
    private val projectLanguagesPlugin: ProjectLanguagesPlugin = ProjectLanguagesPlugin()
    private val staticAnalysisPlugin: StaticAnalysisPlugin = StaticAnalysisPlugin()
    private val codeCoveragePlugin: CodeCoveragePlugin = CodeCoveragePlugin()
    private val externalToolIntegrationPlugin: ExternalToolIntegrationPlugin = ExternalToolIntegrationPlugin()
    private val documentationPlugin: DocumentationPlugin = DocumentationPlugin()
    override fun apply(target: Project) {
        taskRegistrations.registerTasks(target)
        repositoryApplier.apply(target)
        projectLanguagesPlugin.apply(target)
        staticAnalysisPlugin.apply(target)
        codeCoveragePlugin.apply(target)
        externalToolIntegrationPlugin.apply(target)
        documentationPlugin.apply(target)
    }
}

internal class RepositoryApplier : Plugin<Project> {
    override fun apply(target: Project) {
        target.repositories.add(target.repositories.mavenCentral())
        target.repositories.add(target.repositories.gradlePluginPortal())
        target.repositories.add(target.repositories.jcenter())
    }

}
