package org.artembogomova.pf4k.impl

import java.nio.file.Path
import java.util.UUID
import org.artembogomova.pf4k.api.ILoadableModule
import org.artembogomova.pf4k.api.IModuleManager

typealias ExcludedModuleListType = List<String>

class ModuleManager(
    private val rootDir: Path,
    private val excludedModuleIdList: ExcludedModuleListType
) : IModuleManager {

    init{
        LoadableModuleResolverFactory
    }
    override fun loadModules(): Int {

    }

    override fun loadModule(modulePath: Path): Boolean {
        TODO("Not yet implemented")
    }

    override fun startModule(module: ILoadableModule): Boolean {
        TODO("Not yet implemented")
    }

    override fun stopModule(module: ILoadableModule): Boolean {
        TODO("Not yet implemented")
    }

    override fun unloadModule(module: ILoadableModule): Boolean {
        TODO("Not yet implemented")
    }

    override fun unloadModules(): Int {
        TODO("Not yet implemented")
    }

    override fun getModuleByUuid(uuid: UUID): ILoadableModule {
        TODO("Not yet implemented")
    }

    override fun getCoreModule(): ILoadableModule {
        TODO("Not yet implemented")
    }
}
