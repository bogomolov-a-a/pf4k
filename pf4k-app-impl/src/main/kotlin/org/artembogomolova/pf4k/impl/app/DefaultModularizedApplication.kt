package org.artembogomolova.pf4k.impl.app

import java.nio.file.Paths
import java.util.prefs.Preferences
import kotlinx.coroutines.delay
import org.artembogomolova.pf4k.THREAD_INTERRUPT_INTERVAL
import org.artembogomolova.pf4k.api.CoreModuleStartingException
import org.artembogomolova.pf4k.api.app.APPLICATION_PATH_PROPERTY
import org.artembogomolova.pf4k.api.app.EXCLUDED_MODULE_ID_LIST
import org.artembogomolova.pf4k.api.app.IModularizedApplication
import org.artembogomolova.pf4k.api.app.MODULE_ID_SEPARATOR
import org.artembogomolova.pf4k.api.module.management.ExcludedModuleListType
import org.artembogomolova.pf4k.api.module.management.IModuleManager
import org.artembogomolova.pf4k.api.module.management.ModuleManagerFactoryBuilder
import org.artembogomolova.pf4k.api.module.types.LoadableModuleRuntimeStatus
import org.artembogomolova.pf4k.impl.module.management.DefaultModuleManagerFactory
import org.artembogomolova.pf4k.logger

class DefaultModularizedApplication : IModularizedApplication {
    private val log = logger(DefaultModularizedApplication::class)

    override suspend fun run(applicationName: String, args: Array<String>) {
        val applicationStartPath = System.getProperty(APPLICATION_PATH_PROPERTY)
        log.info("start application at '$applicationStartPath'")
        log.info("application name is '${applicationName}'")
        val excludedModuleIdList: ExcludedModuleListType = getExcludedModules(applicationName)
        val moduleManager: IModuleManager = ModuleManagerFactoryBuilder
            .createFactory(DefaultModuleManagerFactory::class.java.name)
            .createModuleManager(
                Paths.get(applicationStartPath),
                excludedModuleIdList
            )
        val moduleResult = moduleManager.startCoreModule(applicationStartPath, args)
        if (moduleResult.isFailure) {
            throw CoreModuleStartingException(
                "application can't start because core module can't load or start successful." +
                        "Cause ${moduleResult.exceptionOrNull()!!.message}"
            )
        }
        val coreModule = moduleResult.getOrNull()!!
        do {
            delay(THREAD_INTERRUPT_INTERVAL)
        } while (coreModule.loadableModuleState.runtimeStatus != LoadableModuleRuntimeStatus.STOPPED)
        val excludedModuleUuidList: List<String> = moduleManager.getExcludedModuleUuidList()
        saveExcludeModuleUuidList(applicationName, excludedModuleUuidList)
    }

    private fun saveExcludeModuleUuidList(applicationName: String, excludedModuleUuidList: List<String>) {
        val prefs = Preferences.userRoot().node(applicationName)
        prefs.remove(EXCLUDED_MODULE_ID_LIST)
        var result = excludedModuleUuidList.toString()
        result = result.substring(1, result.length - 1)
        prefs.put(EXCLUDED_MODULE_ID_LIST, result)
    }

    private fun getExcludedModules(applicationName: String): List<String> {
        val prefs = Preferences.userRoot().node(applicationName)
        return prefs.get(EXCLUDED_MODULE_ID_LIST, "").split(MODULE_ID_SEPARATOR)
    }


}


