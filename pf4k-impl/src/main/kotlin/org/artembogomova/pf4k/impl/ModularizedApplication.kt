package org.artembogomova.pf4k.impl

import java.io.FileInputStream
import java.nio.file.Paths
import java.util.jar.JarInputStream
import java.util.jar.Manifest
import java.util.prefs.Preferences
import org.artembogomova.pf4k.api.module.management.IModuleManager
import org.artembogomova.pf4k.impl.module.management.ExcludedModuleListType
import org.artembogomova.pf4k.impl.module.management.ModuleManagerFactory

class ModularizedApplication {
    lateinit var applicationName: String

    fun run(args: Array<String>) {
        val applicationStartPath = System.getProperty(APPLICATION_PATH_PROPERTY)
        val applicationJarPath = getApplicationJarPath(applicationStartPath)
        val manifest: Manifest = getManifest(applicationJarPath)
        applicationName = getApplicationName(manifest)
        val excludedModuleIdList: ExcludedModuleListType = getExcludedModules()
        val moduleManager: IModuleManager = ModuleManagerFactory.getModuleManager(
            Paths.get(applicationStartPath),
            excludedModuleIdList
        )
        moduleManager.startCoreModule(applicationStartPath, args)
        val excludedModuleUuidList: List<String> = moduleManager.getExcludedModuleUuidList()
        saveExcludeModuleUuidList(excludedModuleUuidList)
    }

    private fun saveExcludeModuleUuidList(excludedModuleUuidList: List<String>) {
        val prefs = Preferences.userRoot().node(applicationName)
        prefs.remove(EXCLUDED_MODULE_ID_LIST)
        var result = excludedModuleUuidList.toString()
        result = result.substring(1, result.length - 1)
        prefs.put(EXCLUDED_MODULE_ID_LIST, result)
    }

    private fun getApplicationJarPath(applicationStartPath: String): String =
        APPLICATION_JAR_FILENAME_PATTERN.format(applicationStartPath, System.getProperty(PROGRAM_NAME_PROPERTY))


    private fun getExcludedModules(): List<String> {
        val prefs = Preferences.userRoot().node(applicationName)
        return prefs.get(EXCLUDED_MODULE_ID_LIST, "").split(MODULE_ID_SEPARATOR)
    }

    private fun getManifest(applicationJarPath: String): Manifest {
        val result: Manifest
        FileInputStream(applicationJarPath).use { fileStream ->
            JarInputStream(fileStream).use {
                result = it.manifest
            }
        }
        return result
    }

    private fun getApplicationName(manifest: Manifest): String =
        manifest.mainAttributes[PROGRAM_NAME_PROPERTY] as String

    companion object {
        const val APPLICATION_NAME_PROPERTY = "Application-Name"
        const val EXCLUDED_MODULE_ID_LIST = "excludedModuleIdList"
        const val MODULE_ID_SEPARATOR = ","
        const val PROGRAM_NAME_PROPERTY = "program.name"
        const val APPLICATION_PATH_PROPERTY = "user.dir"
        const val APPLICATION_JAR_FILENAME_PATTERN = "%s%s.jar"

        @JvmStatic
        fun main(args: Array<String>) {
            ModularizedApplication().run(args)
        }
    }
}


