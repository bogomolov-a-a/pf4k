package org.artembogomolova.pf4k.impl.module

import java.nio.file.Path
import java.util.UUID
import java.util.stream.Collectors
import kotlinx.coroutines.delay
import org.artembogomolova.pf4k.THREAD_INTERRUPT_INTERVAL
import org.artembogomolova.pf4k.api.ApiDescriptorNotFoundException
import org.artembogomolova.pf4k.api.BasicModuleException
import org.artembogomolova.pf4k.api.PreconditionCheckedException
import org.artembogomolova.pf4k.api.ResourceAlreadyRegisteredException
import org.artembogomolova.pf4k.api.ResourceNotFoundException
import org.artembogomolova.pf4k.api.module.DependencyType
import org.artembogomolova.pf4k.api.module.ILoadableModule
import org.artembogomolova.pf4k.api.module.InitializedResourceResult
import org.artembogomolova.pf4k.api.module.MutableExceptionListType
import org.artembogomolova.pf4k.api.module.OnAfterStartEventContext
import org.artembogomolova.pf4k.api.module.OnAfterStopEventContext
import org.artembogomolova.pf4k.api.module.OnBeforeStartEventContext
import org.artembogomolova.pf4k.api.module.OnBeforeStopEventContext
import org.artembogomolova.pf4k.api.module.OnInitializeResourcesEventContext
import org.artembogomolova.pf4k.api.module.OnInitializedDependenciesWaitEventContext
import org.artembogomolova.pf4k.api.module.OnPreconditionsValidateEventContext
import org.artembogomolova.pf4k.api.module.OnResourcesReleaseEventContext
import org.artembogomolova.pf4k.api.module.management.IModuleManager
import org.artembogomolova.pf4k.api.module.management.event.IOnEventContext
import org.artembogomolova.pf4k.api.module.management.event.SubscriberEventTypeList
import org.artembogomolova.pf4k.api.module.types.ApiPointDescriptor
import org.artembogomolova.pf4k.api.module.types.LoadableModuleDependencyDescriptorListType
import org.artembogomolova.pf4k.api.module.types.LoadableModuleDescriptor
import org.artembogomolova.pf4k.api.module.types.LoadableModuleRuntimeStatus
import org.artembogomolova.pf4k.api.module.types.LoadableModuleState
import org.artembogomolova.pf4k.api.module.types.UUIDListType
import org.artembogomolova.pf4k.logger

abstract class AbstractLoadableModule(
    override val loadableModuleState: LoadableModuleState
) : ILoadableModule {

    companion object {
        val SUPPORTED_EVENT_CONTEXT_LIST = listOf(
            OnBeforeStartEventContext::class.java,
            OnPreconditionsValidateEventContext::class.java,
            OnInitializedDependenciesWaitEventContext::class.java,
            OnInitializeResourcesEventContext::class.java,
            OnAfterStartEventContext::class.java,
            OnBeforeStopEventContext::class.java,
            OnResourcesReleaseEventContext::class.java,
            OnAfterStopEventContext::class.java
        )
    }

    protected val log = logger(this::class)
    private val initializeResourcesMap: MutableMap<String, Any> = mutableMapOf()

    override fun listPublicApiDescriptorUuids(): UUIDListType =
        loadableModuleState.apiPointDescriptors.stream().map {
            it.uuid
        }.collect(Collectors.toList())

    override fun getPublicApiDescriptorByUuid(uuid: UUID): Result<ApiPointDescriptor> {
        val result = loadableModuleState.apiPointDescriptors.stream().filter {
            it.uuid == uuid
        }.collect(Collectors.toList())
        if (result.isEmpty()) {
            return Result.failure(ApiDescriptorNotFoundException("api point with $uuid not found in module with ${loadableModuleState.uuid}"))
        }
        return Result.success(result[0])
    }

    override fun listDependencies(dependencyType: DependencyType?): LoadableModuleDependencyDescriptorListType =
        if (dependencyType == null) {
            loadableModuleState.dependencyDescriptors
        } else {
            loadableModuleState.dependencyDescriptors.stream().filter {
                it.dependencyType == dependencyType
            }.collect(Collectors.toList())
        }

    /**************
     ****EVENTS****
     **************/
    /**
     * Event triggered before module start.
     *
     *  @param [event] event context.
     *  @author bogomolov-a-a
     */
    internal open fun onBeforeStart(event: OnBeforeStartEventContext): Boolean {
        loadableModuleState.runtimeStatus = LoadableModuleRuntimeStatus.STARTING
        return true
    }

    /**
     * Typically:
     *  - check core version(for plugged module).
     *  - check environment variables,
     *  - check plugged module parameters.
     *  ..etc
     *
     *  @param [event] event context.
     *  @author bogomolov-a-a
     */
    internal fun onPreconditionsValidate(event: OnPreconditionsValidateEventContext): Boolean {
        val moduleDescriptor = event.descriptor
        val moduleManager = event.moduleManager
        val exceptionList = event.exceptionList
        if (validatePreconditions(moduleDescriptor, moduleManager, exceptionList).not()) {
            loadableModuleState.runtimeStatus = LoadableModuleRuntimeStatus.FAILED
            return false
        }
        loadableModuleState.runtimeStatus = LoadableModuleRuntimeStatus.PRECONDITION_VALIDATED
        return true
    }

    private fun validatePreconditions(
        moduleDescriptor: LoadableModuleDescriptor,
        moduleManager: IModuleManager,
        exceptionList: MutableExceptionListType
    ): Boolean {
        if (isCoreModule())
            return true
        val coreModule = moduleManager.getCoreModule(exceptionList)
        if (exceptionList.isEmpty().not()) {
            loadableModuleState.runtimeStatus = LoadableModuleRuntimeStatus.FAILED
            return false
        }
        val validateResult = validateSpecificModulePreconditions(coreModule, moduleDescriptor)
        buildExceptionListIfExists(validateResult, moduleDescriptor.modulePath, exceptionList)
        return validateResult.isSuccess
    }

    private fun buildExceptionListIfExists(
        validateResult: Result<Nothing?>,
        modulePath: Path,
        exceptionList: MutableList<Exception>
    ) {
        if (validateResult.isFailure) {
            exceptionList.add(
                PreconditionCheckedException(
                    "module from path $modulePath can't be loaded because +${validateResult.exceptionOrNull()!!.message}",
                    validateResult.exceptionOrNull() as BasicModuleException?
                )
            )
        }
    }

    protected open fun validateSpecificModulePreconditions(
        coreModule: ILoadableModule,
        moduleDescriptor: LoadableModuleDescriptor
    ): Result<Nothing?> = Result.success(null)

    /**
     * Typically:
     * - wait start dependencies(module).
     *
     * @param [event] event context.
     *  @author bogomolov-a-a
     */
    internal suspend fun onInitializedDependenciesWait(event: OnInitializedDependenciesWaitEventContext): Boolean {
        val moduleManager = event.moduleManager
        val exceptionList = event.exceptionList
        if (tryToRegisterNonLoadedDependencies(moduleManager, exceptionList).not()) {
            loadableModuleState.runtimeStatus = LoadableModuleRuntimeStatus.FAILED
            return false
        }
        loadableModuleState.runtimeStatus = LoadableModuleRuntimeStatus.INITIALIZED_DEPENDENCIES_RESOURCES_GET
        return true
    }

    private suspend fun tryToRegisterNonLoadedDependencies(moduleManager: IModuleManager, exceptionList: MutableExceptionListType): Boolean {
        val moduleDependencies = listDependencies(DependencyType.MODULE)
        moduleDependencies.forEach {
            val loadableModuleResult = moduleManager.getModuleByUuid(it.moduleDependency!!.uuid)
            if (loadableModuleResult.isFailure) {
                moduleManager.moduleLoader.loadModule(it.moduleDependency!!.path)
            }
        }
        return waitAllModuleDependenciesAreLoaded(moduleManager, exceptionList)
    }

    private suspend fun waitAllModuleDependenciesAreLoaded(moduleManager: IModuleManager, exceptionList: MutableExceptionListType): Boolean {
        var loadingResult: Result<Boolean>
        do {
            delay(THREAD_INTERRUPT_INTERVAL)
            loadingResult = moduleManager.moduleLoader.isAllDependenciesLoaded(loadableModuleState.uuid, exceptionList)
        } while (loadProcessFinished(loadingResult).not())
        if (loadingResult.isFailure) {
            return false
        }
        return true
    }

    private fun loadProcessFinished(loadingResult: Result<Boolean>): Boolean =
        loadingResult.isSuccess && loadingResult.getOrNull()!!.not() || loadingResult.isFailure

    /**
     * Typically:
     * - initialize self resources from resources another module and self state.
     *
     * @param [event] event context.
     *  @author bogomolov-a-a
     */
    internal fun onInitializeResources(event: OnInitializeResourcesEventContext): Boolean {
        val moduleDescriptor = event.descriptor
        val moduleManager = event.moduleManager
        val exceptionList = event.exceptionList
        if (initializeResources(moduleDescriptor, moduleManager, exceptionList).not()) {
            loadableModuleState.runtimeStatus = LoadableModuleRuntimeStatus.FAILED
            return false
        }
        loadableModuleState.runtimeStatus = LoadableModuleRuntimeStatus.RESOURCE_INITIALIZED
        return true
    }

    protected abstract fun initializeResources(
        moduleDescriptor: LoadableModuleDescriptor,
        moduleManager: IModuleManager,
        exceptionList: MutableExceptionListType
    ): Boolean

    /**
     * Event triggered after module start.
     * Typically:
     *  - print information about module
     *  - invoke some methods(?)
     *
     *  @param [event] event context.
     *  @author bogomolov-a-a
     */
    internal open fun onAfterStart(event: OnAfterStartEventContext): Boolean = true

    /**
     * Event triggered before module module.
     * Typically:
     *  - wait stop dependencies(module) if necessary
     *
     *  @param [event] event context.
     *  @author bogomolov-a-a
     */

    internal open fun onBeforeStop(event: OnBeforeStopEventContext): Boolean {
        loadableModuleState.runtimeStatus = LoadableModuleRuntimeStatus.STOPPING
        return true
    }

    /**
     * Event triggered before resources release.
     * Typically:
     * - finalize state,
     *  - clear all sensitive data.
     *
     * @param [event] event context.
     * @author bogomolov-a-a
     */

    internal fun onResourcesRelease(event: OnResourcesReleaseEventContext): Boolean {
        if (releaseResources(event.moduleManager, event.exceptionList).not()) {
            loadableModuleState.runtimeStatus = LoadableModuleRuntimeStatus.FAILED
            return false
        }
        loadableModuleState.runtimeStatus = LoadableModuleRuntimeStatus.RESOURCE_RELEASED
        return true
    }

    abstract fun releaseResources(
        moduleManager: IModuleManager,
        exceptionList: MutableList<Exception>
    ): Boolean

    /**
     * Event triggered after module start.
     * Typically:
     *  - report about successful stop*
     *  - send signal to availability do restart this or unload.
     *  - send stop signal to dependsOn module
     *
     *  @param [event] event context.
     *  @author bogomolov-a-a
     */

    internal fun onAfterStop(event: OnAfterStopEventContext): Boolean {
        loadableModuleState.runtimeStatus = LoadableModuleRuntimeStatus.STOPPED
        if (event.moduleManager.sendStopSignalDependentModules(loadableModuleState.uuid, event.exceptionList).not()) {
            loadableModuleState.runtimeStatus = LoadableModuleRuntimeStatus.FAILED
            return false
        }
        return true
    }

    final override suspend fun handleEvent(eventContext: IOnEventContext): Boolean =
        ModuleEventHandler.handleEvent(this, eventContext)

    @Suppress("UNCHECKED_CAST")
    override fun getAvailableEventContextTypes(): SubscriberEventTypeList =
        SUPPORTED_EVENT_CONTEXT_LIST as SubscriberEventTypeList

    final override fun getInitializedResourcesForAnotherModuleByUuid(uuid: UUID, resourceName: String): InitializedResourceResult {
        if (uuid != loadableModuleState.uuid) {
            log.warn("can't get resources for module $uuid. Current module has uuid ${loadableModuleState.uuid}")
            return InitializedResourceResult.failure(BasicModuleException("uuid $uuid is incorrect for this module"))
        }
        if (!initializeResourcesMap.containsKey(resourceName)) {
            return InitializedResourceResult.failure(ResourceNotFoundException("resource with name '$resourceName' for module with uuid '$uuid' not found"))
        }
        return InitializedResourceResult.success(initializeResourcesMap[resourceName] as Any)
    }

    protected fun addResourceToMap(key: String, resource: Any): Result<Nothing?> {
        if (initializeResourcesMap.containsKey(key)) {
            return Result.failure(ResourceAlreadyRegisteredException("for module '${loadableModuleState.uuid}' resource with name '$key' already registered"))
        }
        initializeResourcesMap[key] = resource
        return Result.success(null)
    }

    final override suspend fun runModule() {
        /*coroutine is alive before 'stopped' status set for module.*/
        do {
            delay(THREAD_INTERRUPT_INTERVAL)
        } while (loadableModuleState.runtimeStatus != LoadableModuleRuntimeStatus.STOPPED)
    }
}