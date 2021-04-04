package org.artembogomolova.pf4k.api.module.management

import java.net.URLClassLoader
import java.nio.file.Path
import java.util.UUID
import org.artembogomolova.pf4k.api.module.ICoreModule
import org.artembogomolova.pf4k.api.module.ILoadableModule
import org.artembogomolova.pf4k.api.module.management.event.ISubscriber
import org.artembogomolova.pf4k.api.module.management.types.ApplicationDescriptor
import org.artembogomolova.pf4k.api.module.management.types.ModuleStatistic
import org.artembogomolova.pf4k.api.module.management.types.MutableExceptionListType
import org.artembogomolova.pf4k.api.module.types.LoadableModuleDescriptor

const val MODULE_DESCRIPTOR_PATH = "module-descriptor"

interface IModuleDescriptorReader {

    fun readFromModuleJar(moduleClassLoader: URLClassLoader): Result<LoadableModuleDescriptor>
}

interface IModuleDescriptorReaderFactory {
    fun createModuleDescriptorReader(): IModuleDescriptorReader
}

object ModuleDescriptorReaderFactoryBuilder {
    fun createFactory(className: String): IModuleDescriptorReaderFactory =
        Class.forName(className).getConstructor().newInstance() as IModuleDescriptorReaderFactory
}

typealias  PathList = List<Path>

interface IModuleLoader : ISubscriber {
    val descriptorReader: IModuleDescriptorReader
    suspend fun loadModules(modulePaths: PathList): List<Result<LoadableModuleDescriptor>>
    suspend fun loadModule(modulePath: Path): Result<LoadableModuleDescriptor>
    fun unloadModule(module: ILoadableModule, exceptionList: MutableExceptionListType): Boolean
    fun unloadModules(exceptionList: MutableExceptionListType): Int
    fun isAllDependenciesLoaded(uuid: UUID, exceptionList: MutableExceptionListType): Result<Boolean>
}

interface IModuleLoaderFactory {
    fun createModuleLoader(descriptorReader: IModuleDescriptorReader): IModuleLoader
}

object ModuleLoaderFactoryBuilder {
    fun createFactory(className: String): IModuleLoaderFactory =
        Class.forName(className).getConstructor().newInstance() as IModuleLoaderFactory
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
    suspend fun startCoreModule(applicationStartPath: String, args: Array<String>): Result<ICoreModule>
    fun getExcludedModuleUuidList(): ExcludedModuleListType
}

interface IModuleManagerFactory {

    fun createModuleManager(
        applicationStartPath: Path,
        excludeModuleIds: ExcludedModuleListType
    ): IModuleManager
}

object ModuleManagerFactoryBuilder {
    fun createFactory(className: String): IModuleManagerFactory =
        Class.forName(className).getConstructor().newInstance()
                as IModuleManagerFactory
}

interface IApplicationDescriptorReader {

    fun readFromModuleJar(moduleClassLoader: URLClassLoader): Result<ApplicationDescriptor>
}

interface IApplicationDescriptorReaderFactory {
    fun createApplicationDescriptorReader(): IApplicationDescriptorReader
}

object ApplicationDescriptorReaderFactoryBuilder {
    fun createFactory(className: String): IApplicationDescriptorReaderFactory =
        Class.forName(className).getConstructor().newInstance() as IApplicationDescriptorReaderFactory
}