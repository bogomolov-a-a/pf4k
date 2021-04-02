package org.artembogomolova.pf4k.impl.module.management.loading

import java.nio.file.Path
import java.security.AccessController
import java.security.PrivilegedAction
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.artembogomolova.pf4k.api.BasicIntercomException
import org.artembogomolova.pf4k.api.module.ILoadableModule
import org.artembogomolova.pf4k.api.module.MutableExceptionListType
import org.artembogomolova.pf4k.api.module.OnLoadEventContext
import org.artembogomolova.pf4k.api.module.OnResolvedEventContext
import org.artembogomolova.pf4k.api.module.management.*
import org.artembogomolova.pf4k.api.module.management.event.EventQueueFactoryBuilder
import org.artembogomolova.pf4k.api.module.management.event.IEventQueue
import org.artembogomolova.pf4k.api.module.management.event.IOnEventContext
import org.artembogomolova.pf4k.api.module.management.event.OnEvent
import org.artembogomolova.pf4k.api.module.management.event.SubscriberEventTypeList
import org.artembogomolova.pf4k.api.module.types.LoadableModuleDescriptor
import org.artembogomolova.pf4k.api.module.types.LoadableModuleState
import org.artembogomolova.pf4k.impl.module.management.descriptor.DefaultModuleDescriptorReaderFactory
import org.artembogomolova.pf4k.impl.module.management.event.DefaultEventQueueFactory


class DefaultModuleLoaderFactory : IModuleLoaderFactory {

    override fun createModuleLoader(descriptorReader: IModuleDescriptorReader): IModuleLoader =
        DefaultModuleLoader(
            ModuleDescriptorReaderFactoryBuilder
                .createFactory(DefaultModuleDescriptorReaderFactory::class.java.name)
                .createModuleDescriptorReader()
        )
}

internal class DefaultModuleLoader(override val descriptorReader: IModuleDescriptorReader) : IModuleLoader {

    companion object {
        val SUPPORTED_EVENT_CONTEXT_LIST = listOf(
            OnResolvedEventContext::class.java,

            )
    }

    private val loadableModuleDescriptorMap: MutableMap<UUID, LoadableModuleDescriptor> = ConcurrentHashMap()
    private val eventQueue: IEventQueue = EventQueueFactoryBuilder
        .createEventQueue(DefaultEventQueueFactory::class.java.name).createEventQueue()

    init {
        eventQueue.subscribe(this)
    }

    override fun loadModules(modulePaths: PathList): List<Result<LoadableModuleDescriptor>> {
        val result: MutableList<Result<LoadableModuleDescriptor>> = mutableListOf()
        modulePaths.forEach(this::loadModule)
        return result.toList()
    }

    override fun loadModule(modulePath: Path): Result<LoadableModuleDescriptor> {
        val moduleClassLoader = createClassLoaderForModule(modulePath)
        val result = descriptorReader.readFromModuleJar(moduleClassLoader)
        if (result.isFailure) {
            return result
        }
        val descriptor = result.getOrNull()!!
        /**/
        val resolvedEvent = OnResolvedEventContext(
            modulePath,
            descriptor.dependencyDescriptors.map { it.moduleDependency?.path!! }.toList(),
            mutableListOf()
        )
        if (eventQueue.pushEvent(OnEvent(resolvedEvent)).not()) {
            return Result.failure(createIntercomException(resolvedEvent))
        }

        return createClassInstanceByDescriptor(moduleClassLoader, descriptor)
    }

    private fun createIntercomException(resolvedEvent: IOnEventContext): BasicIntercomException =
        BasicIntercomException("event '${resolvedEvent}' can't handled. Cause:${resolvedEvent.exceptionList}")

    private fun createClassInstanceByDescriptor(
        moduleClassLoader: LoadableModuleClassLoader,
        descriptor: LoadableModuleDescriptor
    ): Result<LoadableModuleDescriptor> {
        lateinit var moduleClass: Class<*>
        try {
            moduleClass = moduleClassLoader.loadClass(descriptor.moduleClassName)
        } catch (e: ClassNotFoundException) {
            return Result.failure(e)
        }
        val moduleRef = moduleClass
            .getConstructor(LoadableModuleState::class.java)
            .newInstance(LoadableModuleState.from(descriptor)) as ILoadableModule
        descriptor.moduleRef = moduleRef
        descriptor.moduleClassLoader = moduleClassLoader
        loadableModuleDescriptorMap[moduleRef.loadableModuleState.uuid] = descriptor
        val loadedEvent = OnLoadEventContext(
            descriptor,
            mutableListOf()
        )
        if (eventQueue.pushEvent(OnEvent(loadedEvent)).not()) {
            return Result.failure(createIntercomException(loadedEvent))
        }
        return Result.success(descriptor)
    }

    private fun createClassLoaderForModule(modulePath: Path): LoadableModuleClassLoader = AccessController.doPrivileged(PrivilegedAction {
        return@PrivilegedAction LoadableModuleClassLoader(arrayOf(modulePath.toUri().toURL()), ClassLoader.getSystemClassLoader())
    })

    override fun unloadModule(module: ILoadableModule, exceptionList: MutableExceptionListType) = false

    override fun unloadModules(exceptionList: MutableExceptionListType): Int = 0

    override fun isAllDependenciesLoaded(uuid: UUID, exceptionList: MutableExceptionListType): Result<Boolean> = Result.success(false)

    @Suppress("UNCHECKED_CAST")
    override fun getAvailableEventContextTypes(): SubscriberEventTypeList = SUPPORTED_EVENT_CONTEXT_LIST as SubscriberEventTypeList

    override fun handleEvent(eventContext: IOnEventContext): Boolean = ModuleLoaderEventHandler.handleEvent(this, eventContext)

}