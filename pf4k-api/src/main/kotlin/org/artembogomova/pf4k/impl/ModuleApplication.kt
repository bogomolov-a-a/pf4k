package org.artembogomova.pf4k.impl

import java.nio.file.Paths
import java.util.prefs.Preferences



class ModuleApplication {
    fun run(args: Array<String>) {
        val applicationJarPath = System.getProperty("user.dir")
        val excludedModuleIdList: ExcludedModuleListType = getExcludedModules(applicationJarPath)
        val moduleManager = ModuleManager(Paths.get(applicationJarPath),
            excludedModuleIdList)
        moduleManager
    }

    private fun getExcludedModules(applicationJarPath: String): List<String> {
        val applicationName = getApplicationName()
        val prefs = Preferences.userRoot().node(applicationName)
        return prefs.get(EXCLUDED_MODULE_ID_LIST, "").split(MODULE_ID_SEPARATOR)
    }

    private fun getApplicationName(): String {
        val programName=System.getProperty(PROGRAM_NAME_PROPERTY,"")
        //ModuleApplication::class.java.classLoader.getResource(Manifest)
        return ""
    }

    companion object {
        const val APPLICATION_NAME_PROPERTY = "Application-Name"
        const val EXCLUDED_MODULE_ID_LIST = "excludedModuleIdList"
        const val MODULE_ID_SEPARATOR = ","
        const val PROGRAM_NAME_PROPERTY = "program.name"

        @JvmStatic
        fun main(args: Array<String>) {
            ModuleApplication().run(args)
        }
    }
}


