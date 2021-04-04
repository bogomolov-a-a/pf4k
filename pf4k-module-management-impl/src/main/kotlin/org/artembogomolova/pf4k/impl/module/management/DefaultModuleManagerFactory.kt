package org.artembogomolova.pf4k.impl.module.management

import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.artembogomolova.pf4k.api.BasicModuleException
import org.artembogomolova.pf4k.api.module.ICoreModule
import org.artembogomolova.pf4k.api.module.ILoadableModule
import org.artembogomolova.pf4k.api.module.management.ExcludedModuleListType
import org.artembogomolova.pf4k.api.module.management.IModuleLoader
import org.artembogomolova.pf4k.api.module.management.IModuleManager
import org.artembogomolova.pf4k.api.module.management.IModuleManagerFactory
import org.artembogomolova.pf4k.api.module.management.ModuleDescriptorReaderFactoryBuilder
import org.artembogomolova.pf4k.api.module.management.ModuleLoaderFactoryBuilder
import org.artembogomolova.pf4k.api.module.management.types.ModuleStatistic
import org.artembogomolova.pf4k.api.module.management.types.MutableExceptionListType
import org.artembogomolova.pf4k.impl.module.management.descriptor.DefaultModuleDescriptorReaderFactory
import org.artembogomolova.pf4k.impl.module.management.loading.DefaultModuleLoaderFactory
import org.artembogomolova.pf4k.logger


class DefaultModuleManagerFactory : IModuleManagerFactory {

    override fun createModuleManager(applicationStartPath: Path, excludeModuleIds: ExcludedModuleListType): IModuleManager = ModuleManager(
        applicationStartPath,
        excludeModuleIds,
        ModuleLoaderFactoryBuilder
            .createFactory(DefaultModuleLoaderFactory::class.java.name)
            .createModuleLoader(
                ModuleDescriptorReaderFactoryBuilder
                    .createFactory(DefaultModuleDescriptorReaderFactory::class.java.name)
                    .createModuleDescriptorReader()
            )
    )

}

private class ModuleManager(
    private val rootDir: Path,
    private val excludedModuleIdList: ExcludedModuleListType,
    override val moduleLoader: IModuleLoader
) : IModuleManager {
    companion object {
        const val CORE_MODULE_PATH_PATTERN: String = "%s/core/module.jar"
    }

    private val log = logger(this::class)
    override fun startModule(module: ILoadableModule, exceptionList: MutableExceptionListType): Boolean = true

    override fun stopModule(module: ILoadableModule, exceptionList: MutableExceptionListType): Boolean = false

    override fun getModuleByUuid(uuid: UUID): Result<ILoadableModule> = Result.failure(BasicModuleException(""))

    override fun getCoreModule(exceptionList: MutableExceptionListType): ILoadableModule = throw BasicModuleException("")

    override fun getModuleStatistic(): ModuleStatistic = ModuleStatistic()

    override fun excludeModuleByUuid(uuid: UUID, exceptionList: MutableExceptionListType): Boolean = false

    override fun includeModuleByUuid(uuid: UUID, exceptionList: MutableExceptionListType): Boolean = false

    override fun sendStopSignalDependentModules(uuid: UUID, exceptionList: MutableExceptionListType): Boolean = false

    override suspend fun startCoreModule(applicationStartPath: String, args: Array<String>): Result<ICoreModule> {
        val coreModulePath = CORE_MODULE_PATH_PATTERN.format(applicationStartPath)
        log.info("try to load core module from '$coreModulePath'")
        val result = moduleLoader.loadModule(Paths.get(coreModulePath))
        if (!result.isFailure) {
            log.error("while loading module from '$coreModulePath' occurred exception with message: '${result.exceptionOrNull()?.message}'")
            return Result.failure(result.exceptionOrNull()!!)
        }
        val coreModuleDescriptor = result.getOrNull()!!
        val coreModule = coreModuleDescriptor.moduleRef as ICoreModule
        log.info("core module with id ${coreModule.loadableModuleState.uuid} from '$coreModulePath' loaded!")
        GlobalScope.launch {
            coreModule.run(args)
        }
        return Result.success(coreModule)
    }

    override fun getExcludedModuleUuidList(): ExcludedModuleListType = listOf()

}
