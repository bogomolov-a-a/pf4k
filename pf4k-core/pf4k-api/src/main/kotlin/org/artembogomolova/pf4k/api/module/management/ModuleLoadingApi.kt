package org.artembogomolova.pf4k.api.module.management

import java.nio.file.Path
import java.util.UUID
import kotlin.properties.Delegates
import org.artembogomolova.pf4k.api.module.ILoadableModule
import org.artembogomolova.pf4k.api.module.MutableExceptionListType
import org.artembogomolova.pf4k.api.module.OnAfterUnloadEvent
import org.artembogomolova.pf4k.api.module.OnBeforeUnloadEvent
import org.artembogomolova.pf4k.api.module.OnFailedEvent
import org.artembogomolova.pf4k.api.module.OnLoadEvent
import org.artembogomolova.pf4k.api.module.OnResolvedEvent
import org.artembogomolova.pf4k.api.module.types.LoadableModuleDescriptor

interface IModuleDescriptorReader {
    fun readFromModuleJar(modulePath: Path): LoadableModuleDescriptor
}

interface IModuleLoader {
    val descriptorReader: IModuleDescriptorReader
    fun loadModules(exceptionList: MutableExceptionListType): Int
    fun loadModule(modulePath: Path, exceptionList: MutableExceptionListType): Boolean
    fun onResolve(event: OnResolvedEvent): Boolean
    fun onLoad(event: OnLoadEvent): Boolean
    fun onBeforeUnload(event: OnBeforeUnloadEvent): Boolean
    fun onAfterUnload(event: OnAfterUnloadEvent): Boolean
    fun onFailed(event: OnFailedEvent): Boolean
    fun unloadModule(module: ILoadableModule, exceptionList: MutableExceptionListType): Boolean
    fun unloadModules(exceptionList: MutableExceptionListType): Int
    fun isAllDependenciesLoaded(uuid: UUID, exceptionList: MutableExceptionListType): Result<Boolean>
}

class ModuleStatistic {
    var total by Delegates.notNull<Int>()
    var loaded by Delegates.notNull<Int>()
    var unloaded by Delegates.notNull<Int>()
    var starting by Delegates.notNull<Int>()
    var running by Delegates.notNull<Int>()
    var failed by Delegates.notNull<Int>()
    var stopping by Delegates.notNull<Int>()
    var stopped by Delegates.notNull<Int>()
    var included by Delegates.notNull<Int>()
    var excluded by Delegates.notNull<Int>()
}

typealias ExcludedModuleListType = List<String>

interface IModuleManager {
    val moduleLoader: IModuleLoader
    fun startModule(module: ILoadableModule, exceptionList: MutableExceptionListType): Boolean
    fun stopModule(module: ILoadableModule, exceptionList: MutableExceptionListType): Boolean
    fun getModuleByUuid(uuid: UUID): Result<ILoadableModule>
    fun getCoreModule(exceptionList: MutableExceptionListType): ILoadableModule
    fun getModuleStatistic(): ModuleStatistic
    fun excludeModuleByUuid(uuid: UUID, exceptionList: MutableExceptionListType): Boolean
    fun includeModuleByUuid(uuid: UUID, exceptionList: MutableExceptionListType): Boolean
    fun sendStopSignalDependentModules(uuid: UUID, exceptionList: MutableExceptionListType): Boolean
    fun startCoreModule(applicationStartPath: String, args: Array<String>): Boolean
    fun getExcludedModuleUuidList(): ExcludedModuleListType
}

interface IModuleManagerFactory {

    fun createModuleManager(
        applicationStartPath: Path,
        excludeModuleIds: ExcludedModuleListType
    ): IModuleManager
}

object ModuleLoaderFactoryBuilder {
    fun createFactory(className: String): IModuleManagerFactory =
        Class.forName(className).getConstructor().newInstance()
                as IModuleManagerFactory
}