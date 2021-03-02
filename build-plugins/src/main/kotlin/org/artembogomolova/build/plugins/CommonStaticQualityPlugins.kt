package org.artembogomolova.build.plugins

import com.github.spotbugs.snom.SpotBugsExtension
import com.github.spotbugs.snom.SpotBugsPlugin
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.plugins.quality.PmdPlugin
import org.gradle.api.tasks.TaskContainer

internal class StaticAnalysisPlugin : Plugin<Project> {
    private val checkstyleApplier: CheckstyleApplier = CheckstyleApplier()
    private val pmdApplier: PmdApplier = PmdApplier()
    private val detektApplier: DetektApplier = DetektApplier()
    private val spotBugsApplier: SpotBugsApplier = SpotBugsApplier()
    override fun apply(target: Project) {
        checkstyleApplier.apply(target)
        pmdApplier.apply(target)
        detektApplier.apply(target)
        spotBugsApplier.apply(target)
    }
}

private class PmdApplier : PluginApplier<PmdPlugin>(PmdPlugin::class.java) {
    override fun configureTasks(target: TaskContainer, properties: MutableMap<String, Any>) {
        super.configureTasks(target, properties)
        configurePmdTask(target, properties)
    }

    private fun configurePmdTask(target: TaskContainer, properties: MutableMap<String, Any>) {
        target.withType(Pmd::class.java) {
            isConsoleOutput = true
        }
    }

}

private class CheckstyleApplier : PluginApplier<CheckstylePlugin>(CheckstylePlugin::class.java) {

    override fun configureTasks(target: TaskContainer, properties: MutableMap<String, Any>) {
        super.configureTasks(target, properties)
        configureCheckStyleTask(target, properties)
    }

    private fun configureCheckStyleTask(target: TaskContainer, properties: MutableMap<String, Any>) {
        target.withType(Checkstyle::class.java) {
            isIgnoreFailures = false
            isShowViolations = true
        }
    }
}

private class DetektApplier : PluginApplier<DetektPlugin>(DetektPlugin::class.java) {
    companion object {
        const val SETTINGS_PATH = "%s/config/detekt/detekt.yml"
    }

    override fun configureTasks(target: TaskContainer, properties: MutableMap<String, Any>) {
        super.configureTasks(target, properties)
        configureDetektTask(target, properties)
    }

    private fun configureDetektTask(target: TaskContainer, properties: MutableMap<String, Any>) {
        target.withType(Detekt::class.java) {
            config.from.add(SETTINGS_PATH.format(properties[ROOT_PROJECT_DIR_PATH_PROPERTY_NAME]))
            reports {
                xml.enabled = true
                html.enabled = false
                sarif.enabled = false
                txt.enabled = false
            }
        }

    }
}


private class SpotBugsApplier : PluginApplier<SpotBugsPlugin>(SpotBugsPlugin::class.java) {
    companion object {
        const val SETTINGS_PATH = "%s/config/spotbugs/excludes.xml"
    }

    override fun configureExtensions(target: ExtensionContainer, properties: MutableMap<String, Any>) {
        super.configureExtensions(target, properties)
        configureSpotBugsExtension(target, properties)
    }

    private fun configureSpotBugsExtension(target: ExtensionContainer, properties: MutableMap<String, Any>) {
        target.configure(SpotBugsExtension::class.java) {
            excludeFilter.set(File(SETTINGS_PATH.format(properties[ROOT_PROJECT_DIR_PATH_PROPERTY_NAME])))
            ignoreFailures.set(false)
            showProgress.set(true)
            showStackTraces.set(true)
        }
    }

    override fun isAllowReApplyPluginAfterConfigure(pluginClass: Class<SpotBugsPlugin>): Boolean = true
}