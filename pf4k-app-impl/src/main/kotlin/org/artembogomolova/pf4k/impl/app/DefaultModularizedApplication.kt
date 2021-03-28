package org.artembogomolova.pf4k.impl.app

import java.nio.file.Paths
import java.util.jar.Manifest
import java.util.prefs.Preferences
import org.artembogomolova.pf4k.api.CoreModuleStartingException
import org.artembogomolova.pf4k.api.app.APPLICATION_NAME_ATTRIBUTE_NAME
import org.artembogomolova.pf4k.api.app.APPLICATION_PATH_PROPERTY
import org.artembogomolova.pf4k.api.app.EXCLUDED_MODULE_ID_LIST
import org.artembogomolova.pf4k.api.app.IModularizedApplication
import org.artembogomolova.pf4k.api.app.MODULE_ID_SEPARATOR
import org.artembogomolova.pf4k.api.module.management.ExcludedModuleListType
import org.artembogomolova.pf4k.api.module.management.IModuleManager
import org.artembogomolova.pf4k.api.module.management.ModuleLoaderFactoryBuilder
import org.artembogomolova.pf4k.logger

class DefaultModularizedApplication : IModularizedApplication {
    private lateinit var applicationName: String
    private val log = logger(DefaultModularizedApplication::class)
    override fun run(manifest: Manifest, args: Array<String>) {
        val applicationStartPath = System.getProperty(APPLICATION_PATH_PROPERTY)
        log.info("start application at '$applicationStartPath'")
        //    log.debug("start application name reading")
        applicationName = getApplicationName(manifest)
        log.info("application name is '$applicationName'")
        val excludedModuleIdList: ExcludedModuleListType = getExcludedModules()
        val moduleManager: IModuleManager = ModuleLoaderFactoryBuilder
            .createFactory("")
            .createModuleManager(
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

    private fun getApplicationName(manifest: Manifest): String =
        manifest.mainAttributes.getValue(APPLICATION_NAME_ATTRIBUTE_NAME)

}


