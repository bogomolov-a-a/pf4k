package org.artembogomova.pf4k.impl.module.management

import java.nio.file.Path
import java.util.UUID
import org.artembogomova.pf4k.api.BasicModuleException
import org.artembogomova.pf4k.api.ILoadableModule
import org.artembogomova.pf4k.api.module.MutableExceptionListType
import org.artembogomova.pf4k.api.module.management.IModuleLoader
import org.artembogomova.pf4k.api.module.management.IModuleManager
import org.artembogomova.pf4k.api.module.management.ModuleStatistic

typealias ExcludedModuleListType = List<String>

class ModuleManagerFactory {
    companion object {
        fun getModuleManager(
            rootDir: Path,
            excludedModuleIdList: ExcludedModuleListType
        ): IModuleManager =
            ModuleManager(
                rootDir, excludedModuleIdList,
                ModuleLoaderFactory.getModuleLoader()
            )
    }
}

private class ModuleManager(
    private val rootDir: Path,
    private val excludedModuleIdList: ExcludedModuleListType,
    override val moduleLoader: IModuleLoader
) : IModuleManager {

    override fun startModule(module: ILoadableModule, exceptionList: MutableExceptionListType): Boolean = false

    override fun stopModule(module: ILoadableModule, exceptionList: MutableExceptionListType): Boolean = false

    override fun getModuleByUuid(uuid: UUID): Result<ILoadableModule> = Result.failure(BasicModuleException(""))

    override fun getCoreModule(exceptionList: MutableExceptionListType): ILoadableModule = throw BasicModuleException("")

    override fun getModuleStatistic(): ModuleStatistic = ModuleStatistic()

    override fun excludeModuleByUuid(uuid: UUID, exceptionList: MutableExceptionListType): Boolean = false

    override fun includeModuleByUuid(uuid: UUID, exceptionList: MutableExceptionListType): Boolean = false

    override fun sendStopSignalDependentModules(uuid: UUID, exceptionList: MutableExceptionListType): Boolean = false

    override fun startCoreModule(applicationStartPath: String, args: Array<String>) {
        //no op;
    }

    override fun getExcludedModuleUuidList(): List<String> = listOf()

}
