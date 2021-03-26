package org.artembogomolova.pf4k.impl.module.management

import java.net.URLClassLoader
import java.nio.file.Path
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.artembogomolova.pf4k.api.module.ILoadableModule
import org.artembogomolova.pf4k.api.module.MutableExceptionListType
import org.artembogomolova.pf4k.api.module.OnAfterUnloadEvent
import org.artembogomolova.pf4k.api.module.OnBeforeUnloadEvent
import org.artembogomolova.pf4k.api.module.OnFailedEvent
import org.artembogomolova.pf4k.api.module.OnLoadEvent
import org.artembogomolova.pf4k.api.module.OnResolvedEvent
import org.artembogomolova.pf4k.api.module.management.IModuleDescriptorReader
import org.artembogomolova.pf4k.api.module.management.IModuleLoader


class ModuleLoaderFactory {
    companion object {
        fun getModuleLoader(): IModuleLoader =
            ModuleLoader(ModuleDescriptorReaderFactory.getModuleDescriptorReader())

    }
}

internal class ModuleLoader(override val descriptorReader: IModuleDescriptorReader) : IModuleLoader {
    private val moduleClassLoaderMap: Map<UUID, URLClassLoader> = ConcurrentHashMap()
    override fun loadModules(exceptionList: MutableExceptionListType): Int = 0

    override fun loadModule(modulePath: Path, exceptionList: MutableExceptionListType): Boolean = false

    override fun onResolve(event: OnResolvedEvent): Boolean = false

    override fun onLoad(event: OnLoadEvent): Boolean = false

    override fun onBeforeUnload(event: OnBeforeUnloadEvent): Boolean = false

    override fun onAfterUnload(event: OnAfterUnloadEvent): Boolean = false

    override fun onFailed(event: OnFailedEvent): Boolean = false

    override fun unloadModule(module: ILoadableModule, exceptionList: MutableExceptionListType) = false

    override fun unloadModules(exceptionList: MutableExceptionListType): Int = 0

    override fun isAllDependenciesLoaded(uuid: UUID, exceptionList: MutableExceptionListType): Result<Boolean> = Result.success(false)

}