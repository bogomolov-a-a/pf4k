package org.artembogomova.pf4k.impl

import java.nio.file.Path
import java.util.UUID
import java.util.stream.Collectors
import org.artembogomova.pf4k.api.ApiPointDescriptor
import org.artembogomova.pf4k.api.ApiPointDescriptorListType
import org.artembogomova.pf4k.api.ApiVersion
import org.artembogomova.pf4k.api.BasicModuleException
import org.artembogomova.pf4k.api.DependencyType
import org.artembogomova.pf4k.api.ExceptionListType
import org.artembogomova.pf4k.api.ILoadableModule
import org.artembogomova.pf4k.api.IModuleManager
import org.artembogomova.pf4k.api.LoadableModuleAvailableStatus
import org.artembogomova.pf4k.api.LoadableModuleDependencyDescriptorListType
import org.artembogomova.pf4k.api.LoadableModuleDescriptor
import org.artembogomova.pf4k.api.LoadableModuleRuntimeStatus
import org.artembogomova.pf4k.api.ModuleType
import org.artembogomova.pf4k.api.OnAfterStartEvent
import org.artembogomova.pf4k.api.OnAfterStopEvent
import org.artembogomova.pf4k.api.OnBeforeStartEvent
import org.artembogomova.pf4k.api.OnBeforeStopEvent
import org.artembogomova.pf4k.api.PreconditionCheckedException
import org.artembogomova.pf4k.api.UUIDList

abstract class AbstractLoadableModule(
    override val uuid: UUID,
    override val version: ApiVersion,
    override val name: String,
    override val moduleType: ModuleType,
    override val modulePath: Path,
    override var availableStatus: LoadableModuleAvailableStatus,
    override var runtimeStatus: LoadableModuleRuntimeStatus,
    private val apiPointDescriptors: ApiPointDescriptorListType
    private val dependencyDescriptors: LoadableModuleDependencyDescriptorListType
) : ILoadableModule {

    override fun listPublicApiDescriptorUuids(): UUIDList =
        apiPointDescriptors.stream().map {
            it.uuid
        }.collect(Collectors.toList())

    override fun getPublicApiDescriptorByUuid(uuid: UUID): Result<ApiPointDescriptor> {
        val result = apiPointDescriptors.stream().filter {
            it.uuid == uuid
        }.collect(Collectors.toList())
        if (result.isEmpty()) {
            return Result.failure(IllegalArgumentException("api point with $uuid not found in module with ${this.uuid}"))
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


    override fun onBeforeStart(event: OnBeforeStartEvent): Boolean {
        val moduleDescriptor = event.descriptor
        val moduleManager = event.moduleManager
        val exceptionList = event.exceptionList
        if (validatePreconditions(moduleDescriptor, moduleManager, exceptionList).not()) {
            runtimeStatus = LoadableModuleRuntimeStatus.FAILED
            return false
        }
        if (initializeResources(moduleDescriptor, moduleManager, exceptionList).not()) {
            runtimeStatus = LoadableModuleRuntimeStatus.FAILED
            return false
        }
        if (waitForGetResourcesFromAnotherModules(moduleDescriptor, moduleManager, exceptionList).not()) {
            runtimeStatus = LoadableModuleRuntimeStatus.FAILED
            return false
        }
        runtimeStatus = LoadableModuleRuntimeStatus.STARTING
        return true
    }

    abstract fun waitForGetResourcesFromAnotherModules(
        moduleDescriptor: LoadableModuleDescriptor,
        moduleManager: IModuleManager,
        exceptionList: MutableList<Exception>
    ): Boolean

    protected abstract fun initializeResources(
        moduleDescriptor: LoadableModuleDescriptor,
        moduleManager: IModuleManager,
        exceptionList: ExceptionListType
    ): Boolean

    private fun validatePreconditions(
        moduleDescriptor: LoadableModuleDescriptor,
        moduleManager: IModuleManager,
        exceptionList: ExceptionListType
    ): Boolean {
        if (isCoreModule()) return true
        val coreModule = moduleManager.getCoreModule()
        val validateResult = validateSpecificModulePreconditions(coreModule, moduleDescriptor)
        if (validateResult.isFailure) {
            exceptionList.add(
                PreconditionCheckedException(
                    "module from path ${moduleDescriptor.modulePath} can't be loaded because +${validateResult.exceptionOrNull()!!.message}",
                    validateResult.exceptionOrNull() as BasicModuleException?
                )
            )
            return false
        }
        return true
    }

    protected open fun validateSpecificModulePreconditions(
        coreModule: ILoadableModule,
        moduleDescriptor: LoadableModuleDescriptor
    ): Result<Nothing?> = Result.success(null)

    override fun onAfterStart(event: OnAfterStartEvent): Boolean = true

    override fun onBeforeStop(event: OnBeforeStopEvent): Boolean {
        finalizeResources()
    }

    override fun onAfterStop(event: OnAfterStopEvent): Boolean {
        TODO("Not yet implemented")
    }


}