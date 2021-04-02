/**
 * @author bogomolov-a-a
 */
package org.artembogomolova.pf4k.api.module

import java.util.UUID
import org.artembogomolova.pf4k.api.module.management.event.ISubscriber
import org.artembogomolova.pf4k.api.module.types.ApiPointDescriptor
import org.artembogomolova.pf4k.api.module.types.ApiPointMethodDescriptor
import org.artembogomolova.pf4k.api.module.types.ApiVersion
import org.artembogomolova.pf4k.api.module.types.LoadableModuleDependencyDescriptorListType
import org.artembogomolova.pf4k.api.module.types.LoadableModuleState
import org.artembogomolova.pf4k.api.module.types.ModuleType
import org.artembogomolova.pf4k.api.module.types.UUIDListType

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

typealias InitializedResourceResult = Result<Any>

/**
 * Basic loadable module. Each module extends [Runnable], module is stopped,
 * when runnable method exit(from thread).
 *
 * Each module must be a zip archive with structure directories:
 *   - dependencies, contains plain jar dependencies
 *   - manifest.xml (with meta information)
 *   - module.jar (module main jar)
 *
 */
interface ILoadableModule : ISubscriber {

    val loadableModuleState: LoadableModuleState

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
    fun isCoreModule(): Boolean = ModuleType.CORE == loadableModuleState.moduleType

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
     * @return initialized resource or [ResourceNotFoundExceptio] if resource not found.
     * @author bogomolov-a-a
     */
    fun getInitializedResourcesForAnotherModuleByUuid(uuid: UUID, resourceName: String): InitializedResourceResult

    suspend fun runModule()
}

/**
 * Core module.
 *
 * @author bogomolov-a-a
 */
interface ICoreModule : ILoadableModule {
    /**
     * Main method of module.
     * In simple case contains code handle message or [Thread.sleep] function call
     */
    suspend fun run(args: Array<String>)
}

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

    fun isCompatibleWithCore(coreModule: ICoreModule): Boolean
}
