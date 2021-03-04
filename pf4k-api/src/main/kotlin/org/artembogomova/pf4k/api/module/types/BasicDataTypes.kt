package org.artembogomova.pf4k.api.types

import java.nio.file.Path
import java.util.UUID
import org.artembogomova.pf4k.api.DependencyType
import org.artembogomova.pf4k.api.ILoadableModule
import org.artembogomova.pf4k.api.module.types.LoadableModuleAvailableStatus
import org.artembogomova.pf4k.api.module.types.LoadableModuleRuntimeStatus
import org.artembogomova.pf4k.api.module.types.ModuleType

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
    lateinit var path: Path
        internal set
}

/**
 *  Module loadable module dependency(core or plugin) with version.
 *
 *  @property [uuid] primary key of module.
 *  @property [name] unique name pattern(\[A-Za-z09\_-\])*
 *  @property [version] version of module
 *  @property [moduleType]  value of [ModuleType]
 *  @property [path] path to module, late init variable.
 */
class LoadableModuleModuleDependency(
    val uuid: UUID,
    val name: String,
    val version: ApiVersion,
    val moduleType: ModuleType
) {
    lateinit var path: Path
        internal set

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
typealias UUIDListType = List<UUID>
typealias InitializedResourceListType = List<Any>

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
    lateinit var moduleRef: ILoadableModule
        internal set
}