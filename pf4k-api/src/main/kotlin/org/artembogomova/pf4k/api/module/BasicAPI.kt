/**
 * @author bogomolov-a-a
 */
package org.artembogomova.pf4k.api

import java.nio.file.Path
import java.util.UUID
import org.artembogomova.pf4k.api.module.OnAfterStartEvent
import org.artembogomova.pf4k.api.module.OnAfterStopEvent
import org.artembogomova.pf4k.api.module.OnBeforeStartEvent
import org.artembogomova.pf4k.api.module.OnBeforeStopEvent
import org.artembogomova.pf4k.api.module.OnInitializeResourcesEvent
import org.artembogomova.pf4k.api.module.OnInitializedDependenciesWaitEvent
import org.artembogomova.pf4k.api.module.OnPreconditionsValidateEvent
import org.artembogomova.pf4k.api.module.OnResourcesReleaseEvent
import org.artembogomova.pf4k.api.module.types.LoadableModuleAvailableStatus
import org.artembogomova.pf4k.api.module.types.LoadableModuleRuntimeStatus
import org.artembogomova.pf4k.api.module.types.ModuleType
import org.artembogomova.pf4k.api.types.ApiPointDescriptor
import org.artembogomova.pf4k.api.types.ApiPointMethodDescriptor
import org.artembogomova.pf4k.api.types.ApiVersion
import org.artembogomova.pf4k.api.types.InitializedResourceListType
import org.artembogomova.pf4k.api.types.LoadableModuleDependencyDescriptorListType
import org.artembogomova.pf4k.api.types.UUIDListType

/**
 * interface ApiInterface<T>:IPublicApiInterface{
 *  fun method1(p11...p1N):Result<R1>
 *  fun method2(p21...p2N):Result<R2>
 *      ......
 *  fun methodM(pM1...pMN):Result<RM>
 *
 * }
 * method1... methodM - public methods, but they not available directly, only
 * 'invokeMethod' function call.
 * R1...RN - returning types, can be Nothing, if value not used or not returning.
 * R1..RN, typeOf(p(i,j)) - String, primitive type, Nothing, all types in kotlin.*(or another
 * common types), types from directory with common files for core and all plugins.
 * Plugin specific types CAN'T BE TRANSFER TO ANOTHER PLUGIN OR CORE MODULE!!!
 *
 * @author bogomolov-a-a
 */
interface IPublicApiInterface {
    /**
     *
     * @return UUID api point.
     * @author bogomolov-a-a
     */
    fun getUuid(): UUID

    /**
     *  @return Api interface version
     *  @author bogomolov-a-a
     */
    fun getVersion(): ApiVersion

    /**
     * @param method method descriptor, get from [ApiPointDescriptor.methodDescriptors]
     * @param values list of values for method.
     * @return Result<*> instance can't be null, caller must be validate return value on
     * successful invocation. Success conditions defined in method contract.
     * You can't be invoke directly method of public api interface because type loaded in
     * separated classloader and interface type irreducible data type to load in main app classloader
     * @author bogomolov-a-a
     */
    fun invokeMethod(
        method: ApiPointMethodDescriptor,
        vararg values: Any?
    ): Result<*>
}

/**
 * Module dependency type.
 *
 * @author bogomolov-a-a
 */
enum class DependencyType {
    /**
     * Plain java library.
     *
     * @author bogomolov-a-a
     */
    LIBRARY,

    /**
     * Another module(plugin), or core with specified version.
     *
     * @author bogomolov-a-a
     */
    MODULE
}

/**
 * Basic loadable module.
 *
 * Each module must be a zip archive with structure directories:
 *   - dependencies, contains plain jar dependencies
 *   - manifest.xml (with meta information)
 *   - module.jar (module main jar)
 *
 */
interface ILoadableModule {
    /**
     * Each module has uuid as primary key.
     *
     */
    val uuid: UUID

    /**
     * Each module has version in semantic versioning presentation
     *
     */
    val version: ApiVersion

    /**
     * Each module has unique name(as coordinate)
     *
     */
    val name: String

    /**
     *[ModuleType.PLUGIN] for extension module, and [ModuleType.CORE] for main module application
     */
    val moduleType: ModuleType

    /**
     * Path, from which module loaded.
     */
    val modulePath: Path

    /**
     * Current available module status in module registry
     */
    val availableStatus: LoadableModuleAvailableStatus

    /**
     *  Current lifecycle status.
     */
    val runtimeStatus: LoadableModuleRuntimeStatus

    /**
     * For each module can be one or more public api interfaces for interaction with core module and another plugins.
     *
     * @return list public api interfaces uuids. If none, return [listOf] ()
     */
    fun listPublicApiDescriptorUuids(): UUIDListType

    /**
     *
     * @return retrieve specified api point descriptor by id.
     * if [uuid]!=null but api point not found in this module then return null as result value.
     *
     */
    fun getPublicApiDescriptorByUuid(uuid: UUID): Result<ApiPointDescriptor>

    /**
     * @return true if this module is main(core)
     */
    fun isCoreModule(): Boolean = ModuleType.CORE == moduleType

    /**
     * Filter module dependencies by type.
     *
     * @param [dependencyType] type of listed dependencies
     * @return list of dependency descriptor of this module with it paths.
     * If [type] == null then return all dependencies
     * If no dependencies return
     */
    fun listDependencies(dependencyType: DependencyType? = null): LoadableModuleDependencyDescriptorListType

    /**
     * Get any resources from module by [uuid] for initialization process invoking module.
     *
     * @param [uuid] module uuid
     * @return if uuid not found empty list
     * @author bogomolov-a-a
     */
    fun getInitializedResourcesForAnotherModuleByUuid(uuid: UUID): InitializedResourceListType

    /**
     * Main method of module.
     * In simple case contains code handle message or [Thread.sleep] function call
     */
    fun run(args: Array<String> = arrayOf())
    /**************
     ****EVENTS****
     **************/

    /**
     * Event triggered before module start.
     *
     *  @param [event] event context.
     *  @author bogomolov-a-a
     */
    fun onBeforeStart(event: OnBeforeStartEvent): Boolean

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
    fun onPreconditionsValidate(event: OnPreconditionsValidateEvent): Boolean

    /**
     * Typically:
     * - wait start dependencies(module).
     *
     * @param [event] event context.
     *  @author bogomolov-a-a
     */
    fun onInitializedDependenciesWait(event: OnInitializedDependenciesWaitEvent): Boolean

    /**
     * Typically:
     * - initialize self resources from resources another module and self state.
     *
     * @param [event] event context.
     *  @author bogomolov-a-a
     */
    fun onInitializeResources(event: OnInitializeResourcesEvent): Boolean

    /**
     * Event triggered after module start.
     * Typically:
     *  - print information about module
     *  - invoke some methods(?)
     *
     *  @param [event] event context.
     *  @author bogomolov-a-a
     */
    fun onAfterStart(event: OnAfterStartEvent): Boolean

    /**
     * Event triggered before module module.
     * Typically:
     *  - wait stop dependencies(module) if necessary
     *
     *  @param [event] event context.
     *  @author bogomolov-a-a
     */
    fun onBeforeStop(event: OnBeforeStopEvent): Boolean

    /**
     * Event triggered before resources release.
     * Typically:
     * - finalize state,
     *  - clear all sensitive data.
     *
     * @param [event] event context.
     * @author bogomolov-a-a
     */
    fun onResourcesRelease(event: OnResourcesReleaseEvent): Boolean

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
    fun onAfterStop(event: OnAfterStopEvent): Boolean
}

/**
 * Core module.
 *
 * @author bogomolov-a-a
 */
interface ICoreModule : ILoadableModule

/**
 * Plugged module.
 *
 * @author bogomolov-a-a
 */
interface IPluginModule : ILoadableModule {
    /**
     * Minimum version of core module supports this plugged module.
     */
    val requireCoreVersion: ApiVersion
}
