package org.artembogomolova.build.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.dokka.gradle.DokkaPlugin

internal class DocumentationPlugin : Plugin<Project> {
    private val dokkaPluginApplier: DokkaPluginApplier = DokkaPluginApplier()
    override fun apply(target: Project) {
        dokkaPluginApplier.apply(target)
    }

}

private class DokkaPluginApplier : PluginApplier<DokkaPlugin>(DokkaPlugin::class.java)
