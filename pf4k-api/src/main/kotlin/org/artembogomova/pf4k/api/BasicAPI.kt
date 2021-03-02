/**
 * @author bogomolov-a-a
 */
package org.artembogomova.pf4k.api

import java.nio.file.Path
import java.util.UUID


/**
 * Semantic version of module.
 *
 * @property [major] major, for non backward compatibility changes.
 * @property [minor] minor, for major backward compatibility changes.
 * @property [revision] revision, for small backward compatibility changes.
 * @property [classifier] classifier, for another small changes in one revision(typo, any commits by task)
 * @author bogomolov-a-a
 */
data class ApiVersion internal constructor(
    val major: Long,
    val minor: Long,
    val revision: Long,
    val classifier: String
) {
    override fun toString(): String {
        return "${major}.${minor}.${revision}-${classifier}"
    }
}

/**
 * Status module, available status.
 *
 * @author bogomolov-a-a
 */
enum class LoadableModuleAvailableStatus {
    /**
     * Module available to loading
     * @author bogomolov-a-a
     */
    INCLUDED,

    /**
     * Module not available to loading(failure,example)
     * @author bogomolov-a-a
     */
    EXCLUDED
}

/**
 * Runtime statuses of loadable module.
 *
 * @author bogomolov-a-a
 */
enum class LoadableModuleRuntimeStatus {
    /**
     * Looking up "plugins" folder,"core" folder and try resolving module dependencies.
     *
     * @author bogomolov-a-a
     */
    RESOLVING,

    /**
     * found in plugins folder, prepare loading it and its dependencies in memory.
     *
     * @author bogomolov-a-a
     * */
    RESOLVED,

    /**
     * Start loading module in memory(with dependencies), loading event triggered.
     *
     * @author bogomolov-a-a
     */
    LOADING,

    /**
     * Successful loaded in memory(with dependencies).
     *
     * @author bogomolov-a-a
     */
    LOADED,

    /**
     * Loadable module start initializing, init event triggered.
     *
     * @author bogomolov-a-a
     */
    STARTING,

    /**
     * Loadable module running, central lifecycle status.
     *
     * @author bogomolov-a-a
     */
    RUNNING,

    /**
     * Finalize event triggered, loadable module start finalizing.
     *
     * @author bogomolov-a-a
     */
    STOPPING,

    /**
     * Loadable module stopped, but loaded in memory, can be restarted or unloaded.
     *
     * @author bogomolov-a-a
     * */
    STOPPED,

    /**
     * If in [STARTING], [RUNNING], [STOPPING], [UNLOADING] statuses
     * throw uncaught exception - module enters this state.
     * Work with module unavailable. Can try load or unload again.
     *
     * @author bogomolov-a-a
     */
    FAILED,

    /**
     * Loadable module started unloading from memory. Classloader removed(inaccessible, but
     * for some time classes stored in memory. All data must be cleared!!!
     *
     * @author bogomolov-a-a
     * */
    UNLOADING,

    /**
     * Loadable module unload and can be load again.
     *
     * @author bogomolov-a-a
     */
    UNLOADED
}

typealias ParameterNameTypeList = List<String>

/**
 * Api point method descriptor.
 *
 * interface ApiInterface<T>{
 *  fun apiMethod(p1:T1,p2:T2....pN:TN):Result<T>
 * }
 * Contract requirements:
 * - Any public MUST BE return kotlin.Result for invoking safety!
 * - Any public api method MUST NOT HAVE overloads!
 *
 * @property [name] unqualified method name ("apiMethod", for example).
 * @property [description] method description(method purpose).
 * @property [parameterTypeNames] parameter type names, for information.
 * @author bogomolov-a-a
 */
data class ApiPointMethodDescriptor internal constructor(
    val name: String,
    val description: String,
    val parameterTypeNames: ParameterNameTypeList
)

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
typealias  ApiPointMethodDescriptorListType = List<ApiPointMethodDescriptor>

/**
 * Api point(public api interface) descriptor.
 *
 * @property [publicApiInterfaceClassName] qualified implementation class name
 * @property [name]  point name(for example,"Hello world point")
 * @property [version] version of this point
 * @property [description] description for this point
 * @property [methodDescriptors] List of method descriptor, which can be invoking from
 * this point
 * @author bogomolov-a-a
 */
data class ApiPointDescriptor internal constructor(
    val uuid: UUID,
    val publicApiInterfaceClassName: String,
    val name: String,
    val version: ApiVersion,
    val description: String,
    val methodDescriptors: ApiPointMethodDescriptorListType
)

typealias ApiPointDescriptorListType = List<ApiPointDescriptor>

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
 * Dependency loaded from 'common' directory and from 'dependencies' module directory
 * Library names can be \[groupId\]:artifactId:version:\[classifier\].jar
 *
 * @property [groupId] maven artifact groupId
 * @property [artifactId] maven artifact artifactId
 * @property [version] maven artifact version
 * @property [classifier] maven artifact classifier
 * @property [path] path to module, late init variable.
 * @author bogomolov-a-a
 */
class LoadableModuleLibraryDependency internal constructor(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val classifier: String,
) {
    var path: Path
        get() = path
        internal set(value) {
            path = value
        }
}

/**
 * Application module type.
 *
 * @author bogomolov-a-a
 */
enum class ModuleType {
    /**
     * Extension.
     *
     * @author bogomolov-a-a
     */
    PLUGIN,

    /**
     * Main module(core).
     *
     * @author bogomolov-a-a
     */
    CORE
}

/**
 *  Module loadable module dependency(core or plugin) with version.
 *
 *  @property [name] unique name pattern(\[A-Za-z09\_-\])*
 *  @property [version] version of module
 *  @property [moduleType]  value of [ModuleType]
 *  @property [path] path to module, late init variable.
 */
class LoadableModuleModuleDependency(
    val name: String,
    val version: ApiVersion,
    val moduleType: ModuleType
) {
    var path: Path
        get() = path
        internal set(value) {
            path = value
        }

}

/**
 * Loadable module dependency descriptor
 *
 * @property [dependencyType] plain jar or module
 * @property [libraryDependency] info about plain jar or null, if [dependencyType] is module
 * @property [moduleDependency] info about module jar or null, if [dependencyType] is plain jar
 * @author bogomolov-a-a
 */
data class LoadableModuleDependencyDescriptor(
    val dependencyType: DependencyType,
    val libraryDependency: LoadableModuleLibraryDependency? = null,
    val moduleDependency: LoadableModuleModuleDependency? = null
)

typealias LoadableModuleDependencyDescriptorListType = List<LoadableModuleDependencyDescriptor>
typealias UUIDList = List<UUID>

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
    fun listPublicApiDescriptorUuids(): UUIDList

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

    /**************
     ****EVENTS****
     **************/

    /**
     * Event triggered before module start.
     * Typically:
     *  - check preconditions,
     *  - initialize state,
     *  - wait start dependencies(module).
     *
     *  @param [event] event context.
     *  @author bogomolov-a-a
     */
    fun onBeforeStart(event: OnBeforeStartEvent): Boolean

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
     *  - finalize state,
     *  - clear all sensitive data.
     *
     *  @param [event] event context.
     *  @author bogomolov-a-a
     */
    fun onBeforeStop(event: OnBeforeStopEvent): Boolean

    /**
     * Event triggered after module start.
     * Typically:
     *  - report about successful stop
     *  - send signal to availability do restart this or unload.
     *
     *  @param [event] event context.
     *  @author bogomolov-a-a
     */
    fun onAfterStop(event: OnAfterStopEvent): Boolean
}

/**
 * Loadable module descriptor contains all information about one application module
 * and transfer it from stream(file,url) to module manager.
 *
 * @property [uuid] unique module identifier
 * @property [name] human readable name
 * @property [version] version of this module.
 * @property [description] purpose of this module.
 * @property [modulePath] path from which module can be loaded in memory.
 * @property [availableStatus] switch "on/off" for module.
 * Core module CAN'T BE SET [LoadableModuleAvailableStatus.EXCLUDED]. If
 * module has [LoadableModuleAvailableStatus.EXCLUDED] as availability status
 * module DON'T LOAD in memory after resolve dependencies.
 * @property [runtimeStatus] module status, values [LoadableModuleRuntimeStatus]
 * @property [dependencyDescriptors] list of this module dependencies such as plain jar or module jar.
 * @property [moduleRef] after resolving and loading in memory - reference to module to communicate with another modules
 * @author bogomolov-a-a
 */
class LoadableModuleDescriptor(
    val uuid: UUID,
    val name: String,
    val version: ApiVersion,
    val description: String,
    val modulePath: Path,
    val availableStatus: LoadableModuleAvailableStatus,
    val runtimeStatus: LoadableModuleRuntimeStatus,
    val dependencyDescriptors: LoadableModuleDependencyDescriptorListType,
    val apiPointDescriptors: ApiPointDescriptorListType
) {
    var moduleRef: ILoadableModule
        get() = moduleRef
        internal set(value) {
            moduleRef = value
        }
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
