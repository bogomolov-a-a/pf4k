package org.artembogomolova.pf4k.impl.module

import java.nio.file.Path
import java.util.UUID
import java.util.stream.Collectors
import org.artembogomolova.pf4k.api.ApiDescriptorNotFoundException
import org.artembogomolova.pf4k.api.BasicModuleException
import org.artembogomolova.pf4k.api.PreconditionCheckedException
import org.artembogomolova.pf4k.api.module.DependencyType
import org.artembogomolova.pf4k.api.module.ILoadableModule
import org.artembogomolova.pf4k.api.module.MutableExceptionListType
import org.artembogomolova.pf4k.api.module.OnAfterStartEvent
import org.artembogomolova.pf4k.api.module.OnAfterStopEvent
import org.artembogomolova.pf4k.api.module.OnBeforeStartEvent
import org.artembogomolova.pf4k.api.module.OnBeforeStopEvent
import org.artembogomolova.pf4k.api.module.OnInitializeResourcesEvent
import org.artembogomolova.pf4k.api.module.OnInitializedDependenciesWaitEvent
import org.artembogomolova.pf4k.api.module.OnPreconditionsValidateEvent
import org.artembogomolova.pf4k.api.module.OnResourcesReleaseEvent
import org.artembogomolova.pf4k.api.module.management.IModuleManager
import org.artembogomolova.pf4k.api.module.types.ApiPointDescriptor
import org.artembogomolova.pf4k.api.module.types.ApiPointDescriptorListType
import org.artembogomolova.pf4k.api.module.types.ApiVersion
import org.artembogomolova.pf4k.api.module.types.LoadableModuleAvailableStatus
import org.artembogomolova.pf4k.api.module.types.LoadableModuleDependencyDescriptorListType
import org.artembogomolova.pf4k.api.module.types.LoadableModuleDescriptor
import org.artembogomolova.pf4k.api.module.types.LoadableModuleRuntimeStatus
import org.artembogomolova.pf4k.api.module.types.ModuleType
import org.artembogomolova.pf4k.api.module.types.UUIDListType

abstract class AbstractLoadableModule(
    override val uuid: UUID,
    override val version: ApiVersion,
    override val name: String,
    override val moduleType: ModuleType,
    override val modulePath: Path,
    override var availableStatus: LoadableModuleAvailableStatus,
    override var runtimeStatus: LoadableModuleRuntimeStatus,
    private val apiPointDescriptors: ApiPointDescriptorListType,
    private val dependencyDescriptors: LoadableModuleDependencyDescriptorListType
) : ILoadableModule {

    companion object {
        const val DEPENDENCY_RESPONSE_TIMEOUT_MILS = 1000L
    }

    override fun listPublicApiDescriptorUuids(): UUIDListType =
        apiPointDescriptors.stream().map {
            it.uuid
        }.collect(Collectors.toList())

    override fun getPublicApiDescriptorByUuid(uuid: UUID): Result<ApiPointDescriptor> {
        val result = apiPointDescriptors.stream().filter {
            it.uuid == uuid
        }.collect(Collectors.toList())
        if (result.isEmpty()) {
            return Result.failure(ApiDescriptorNotFoundException("api point with $uuid not found in module with ${this.uuid}"))
        }
        return Result.success(result[0])
    }

    override fun listDependencies(dependencyType: DependencyType?): LoadableModuleDependencyDescriptorListType =
        if (dependencyType == null) {
            dependencyDescriptors
        } else {
            dependencyDescriptors.stream().filter {
                it.dependencyType == dependencyType
            }.collect(Collectors.toList())
        }

    /**************
     ****EVENTS****
     **************/

    override fun onBeforeStart(event: OnBeforeStartEvent): Boolean {
        runtimeStatus = LoadableModuleRuntimeStatus.STARTING
        return true
    }

    final override fun onPreconditionsValidate(event: OnPreconditionsValidateEvent): Boolean {
        val moduleDescriptor = event.descriptor
        val moduleManager = event.moduleManager
        val exceptionList = event.exceptionList
        if (validatePreconditions(moduleDescriptor, moduleManager, exceptionList).not()) {
            runtimeStatus = LoadableModuleRuntimeStatus.FAILED
            return false
        }
        runtimeStatus = LoadableModuleRuntimeStatus.PRECONDITION_VALIDATED
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
            runtimeStatus = LoadableModuleRuntimeStatus.FAILED
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

    final override fun onInitializedDependenciesWait(event: OnInitializedDependenciesWaitEvent): Boolean {
        val moduleManager = event.moduleManager
        val exceptionList = event.exceptionList
        if (tryToRegisterNonLoadedDependencies(moduleManager, exceptionList).not()) {
            runtimeStatus = LoadableModuleRuntimeStatus.FAILED
            return false
        }
        runtimeStatus = LoadableModuleRuntimeStatus.INITIALIZED_DEPENDENCIES_RESOURCES_GET
        return true
    }

    private fun tryToRegisterNonLoadedDependencies(moduleManager: IModuleManager, exceptionList: MutableExceptionListType): Boolean {
        val moduleDependencies = listDependencies(DependencyType.MODULE)
        moduleDependencies.forEach {
            val loadableModuleResult = moduleManager.getModuleByUuid(it.moduleDependency!!.uuid)
            if (loadableModuleResult.isFailure) {
                moduleManager.moduleLoader.loadModule(it.moduleDependency!!.path, exceptionList)
            }
        }
        return waitAllModuleDependenciesAreLoaded(moduleManager, exceptionList)

    }

    private fun waitAllModuleDependenciesAreLoaded(moduleManager: IModuleManager, exceptionList: MutableExceptionListType): Boolean {
        var loadingResult: Result<Boolean>
        do {
            Thread.sleep(DEPENDENCY_RESPONSE_TIMEOUT_MILS)
            loadingResult = moduleManager.moduleLoader.isAllDependenciesLoaded(uuid, exceptionList)
        } while (loadProcessFinished(loadingResult).not())
        if (loadingResult.isFailure) {
            return false
        }
        return true
    }

    private fun loadProcessFinished(loadingResult: Result<Boolean>): Boolean =
        loadingResult.isSuccess && loadingResult.getOrNull()!!.not() || loadingResult.isFailure


    final override fun onInitializeResources(event: OnInitializeResourcesEvent): Boolean {
        val moduleDescriptor = event.descriptor
        val moduleManager = event.moduleManager
        val exceptionList = event.exceptionList
        if (initializeResources(moduleDescriptor, moduleManager, exceptionList).not()) {
            runtimeStatus = LoadableModuleRuntimeStatus.FAILED
            return false
        }
        runtimeStatus = LoadableModuleRuntimeStatus.RESOURCE_INITIALIZED
        return true
    }

    protected abstract fun initializeResources(
        moduleDescriptor: LoadableModuleDescriptor,
        moduleManager: IModuleManager,
        exceptionList: MutableExceptionListType
    ): Boolean

    override fun onAfterStart(event: OnAfterStartEvent): Boolean = true

    override fun onBeforeStop(event: OnBeforeStopEvent): Boolean {
        runtimeStatus = LoadableModuleRuntimeStatus.STOPPING
        return true
    }

    final override fun onResourcesRelease(event: OnResourcesReleaseEvent): Boolean {
        if (releaseResources(event.moduleManager, event.exceptionList).not()) {
            runtimeStatus = LoadableModuleRuntimeStatus.FAILED
            return false
        }
        runtimeStatus = LoadableModuleRuntimeStatus.RESOURCE_RELEASED
        return true
    }

    abstract fun releaseResources(
        moduleManager: IModuleManager,
        exceptionList: MutableList<Exception>
    ): Boolean

    final override fun onAfterStop(event: OnAfterStopEvent): Boolean {
        runtimeStatus = LoadableModuleRuntimeStatus.STOPPED
        if (event.moduleManager.sendStopSignalDependentModules(uuid, event.exceptionList).not()) {
            runtimeStatus = LoadableModuleRuntimeStatus.FAILED
            return false
        }
        return true
    }

}