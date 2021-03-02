package org.artembogomova.pf4k.api

import java.nio.file.Path
import java.util.UUID

interface IModuleDescriptorReader {
    fun readFromModuleJar(modulePath: Path): LoadableModuleDescriptor
}

interface IModuleLoader {
    val descriptorReader: IModuleDescriptorReader
    fun onResolve(event: OnResolvedEvent): Boolean
    fun onLoad(event: OnLoadEvent): Boolean
    fun onBeforeUnload(event: OnAfterAfterEvent): Boolean
    fun onAfterUnload(event: OnAfterBeforeEvent): Boolean
    fun onFailed(event: OnFailedEvent): Boolean
}

interface IModuleManager {
    fun loadModules(): Int
    fun loadModule(modulePath: Path): Boolean
    fun startModule(module: ILoadableModule): Boolean
    fun stopModule(module: ILoadableModule): Boolean
    fun unloadModule(module: ILoadableModule): Boolean
    fun unloadModules(): Int

    fun getModuleByUuid(uuid: UUID): ILoadableModule
    fun getCoreModule(): ILoadableModule
}