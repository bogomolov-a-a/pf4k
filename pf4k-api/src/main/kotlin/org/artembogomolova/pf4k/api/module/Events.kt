package org.artembogomolova.pf4k.api.module

import java.nio.file.Path
import org.artembogomolova.pf4k.api.module.management.IModuleManager
import org.artembogomolova.pf4k.api.module.types.LoadableModuleDescriptor

typealias  MutableExceptionListType = MutableList<Exception>
typealias  ExceptionListType = List<Exception>
typealias  DependencyPathListType = List<Path>

/*********************
 ****Manager events***
 ********************/

/**
 * Event triggered, when new module found in classpath.
 *
 * @property [resolvedPath] path with module jar.
 * @property [dependencyPathList] list of path dependencies of module in [resolvedPath].
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnResolvedEvent(
    val resolvedPath: Path,
    val dependencyPathList: DependencyPathListType,
    val exceptionList: MutableExceptionListType
)

/**
 *  Event triggered, when new module loaded in memory.
 *
 *  @property [descriptor] loaded module descriptor.
 *  @property [exceptionList] list of exceptions thrown in event method handler.
 *  @author bogomolov-a-a
 */
data class OnLoadEvent(
    val descriptor: LoadableModuleDescriptor,
    val exceptionList: MutableExceptionListType
)

/**
 *  Event triggered, when module preparing unloaded from memory.
 *
 *  @property [descriptor] loaded module descriptor.
 *  @property [exceptionList] list of exceptions thrown in event method handler.
 *  @author bogomolov-a-a
 */
data class OnBeforeUnloadEvent(
    val descriptor: LoadableModuleDescriptor,
    val exceptionList: MutableExceptionListType
)

/**
 *  Event triggered, when module successful unloaded from memory.
 *
 *  @property [descriptor] loaded module descriptor.
 *  @property [exceptionList] list of exceptions thrown in event method handler.
 *  @author bogomolov-a-a
 */
data class OnAfterUnloadEvent(
    val descriptor: LoadableModuleDescriptor,
    val exceptionList: MutableExceptionListType
)

/**
 * Event triggered, when module successful unloaded from memory.
 *
 *  @property [descriptor] loaded module descriptor.
 *  @property [moduleManager] current module manager.
 *  @property [exceptionList] list of exceptions thrown in event method handler.
 *  @author bogomolov-a-a

 */
data class OnFailedEvent(
    val descriptor: LoadableModuleDescriptor,
    val moduleManager: IModuleManager,
    val exceptionList: ExceptionListType
)
/*********************
 ****Module events***
 ********************/
/**
 * Event triggered, when module try to start.
 *
 * @property [moduleManager] current module manager.
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnBeforeStartEvent(
    val moduleManager: IModuleManager,
    val exceptionList: MutableExceptionListType
)

/**
 * Event triggered, when module try to validate start ability.
 *
 * @property [descriptor] loaded module descriptor.
 * @property [moduleManager] current module manager.
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnPreconditionsValidateEvent(
    val descriptor: LoadableModuleDescriptor,
    val moduleManager: IModuleManager,
    val exceptionList: MutableExceptionListType
)

/**
 * Event triggered, when module try to get resources from another module for self initialization.
 *
 * @property [moduleManager] current module manager.
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnInitializedDependenciesWaitEvent(
    val moduleManager: IModuleManager,
    val exceptionList: MutableExceptionListType
)

/**
 * Event triggered, when module try to initialize self resources.
 *
 * @property [descriptor] loaded module descriptor.
 * @property [moduleManager] current module manager.
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnInitializeResourcesEvent(
    val descriptor: LoadableModuleDescriptor,
    val moduleManager: IModuleManager,
    val exceptionList: MutableExceptionListType
)

/**
 * Event triggered, when module successful started.
 *
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnAfterStartEvent(
    val exceptionList: MutableExceptionListType
)

/**
 * Event triggered, when module preparing to stop.
 *
 * @property [descriptor] loaded module descriptor.
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnBeforeStopEvent(
    val descriptor: LoadableModuleDescriptor,
    val exceptionList: MutableExceptionListType
)

/**
 * Event triggered, when module preparing resources release.
 *
 * @property [moduleManager] current module manager.
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnResourcesReleaseEvent(
    val moduleManager: IModuleManager,
    val exceptionList: MutableExceptionListType
)

/**
 * Event triggered, when module successful stopped.
 *
 * @property [moduleManager] current module manager.
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnAfterStopEvent(
    val moduleManager: IModuleManager,
    val exceptionList: MutableExceptionListType
)