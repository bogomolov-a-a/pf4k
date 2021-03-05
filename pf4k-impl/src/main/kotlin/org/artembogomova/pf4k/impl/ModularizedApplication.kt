package org.artembogomova.pf4k.impl

import java.nio.file.Paths
import java.util.jar.Manifest
import java.util.prefs.Preferences
import org.artembogomova.pf4k.api.CoreModuleStartingException
import org.artembogomova.pf4k.api.module.management.IModuleManager
import org.artembogomova.pf4k.impl.module.management.ExcludedModuleListType
import org.artembogomova.pf4k.impl.module.management.ModuleManagerFactory

class ModularizedApplication {
    var applicationName: String = ""
    val log = logger(ModularizedApplication::class)
    fun run(args: Array<String>) {
        val applicationStartPath = System.getProperty(APPLICATION_PATH_PROPERTY)
        log.info("start application at '$applicationStartPath'")
        val manifest: Manifest = getManifest()
        //    log.debug("start application name reading")
        applicationName = getApplicationName(manifest)
        log.info("application name is '$applicationName'")
        val excludedModuleIdList: ExcludedModuleListType = getExcludedModules()
        val moduleManager: IModuleManager = ModuleManagerFactory.getModuleManager(
            Paths.get(applicationStartPath),
            excludedModuleIdList
        )
        if (moduleManager.startCoreModule(applicationStartPath, args).not()) {
            throw CoreModuleStartingException("application can't start because core module can't load or start successful.")
        }
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

    private fun getExcludedModules(): List<String> {
        val prefs = Preferences.userRoot().node(applicationName)
        return prefs.get(EXCLUDED_MODULE_ID_LIST, "").split(MODULE_ID_SEPARATOR)
    }

    private fun getManifest(): Manifest {
        val result: Manifest
        this.javaClass.classLoader.getResource(MANIFEST_URI).openStream().use {
            result = Manifest(it)
        }
        /*      log.debug("print attributes application manifest")
              result.mainAttributes.forEach {
                  log.debug("attribute name : '${it.key}' attribute value: '${it.value}' ")
              }
              log.debug("attributes application manifest printed")*/
        return result
    }

    private fun getApplicationName(manifest: Manifest): String =
        manifest.mainAttributes.getValue(APPLICATION_NAME_PROPERTY)


    companion object {
        const val APPLICATION_NAME_PROPERTY = "Application-Name"
        const val EXCLUDED_MODULE_ID_LIST = "excludedModuleIdList"
        const val MODULE_ID_SEPARATOR = ","
        const val APPLICATION_PATH_PROPERTY = "user.dir"
        const val MANIFEST_URI = "META-INF/MANIFEST.MF"

        @JvmStatic
        fun main(args: Array<String>) {
            ModularizedApplication().run(args)
        }
    }
}


