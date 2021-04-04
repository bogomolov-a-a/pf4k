package org.artembogomolova.pf4k.api.module.management.types

import java.nio.file.Path
import org.artembogomolova.pf4k.api.module.management.IModuleManager
import org.artembogomolova.pf4k.api.module.management.event.IOnEventContext
import org.artembogomolova.pf4k.api.module.types.LoadableModuleDescriptor

typealias  MutableExceptionListType = MutableList<Exception>

abstract class BaseEventContext(
    override val exceptionList: MutableExceptionListType
) : IOnEventContext

typealias  DependencyPathListType = List<Path>

/*********************
 **Management events**
 ********************/

/**
 * Event triggered, when new module found in classpath.
 *
 * @property [resolvedPath] path with module jar.
 * @property [dependencyPathList] list of path dependencies of module in [resolvedPath].
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnResolvedEventContext(
    val resolvedPath: Path,
    val dependencyPathList: DependencyPathListType,
    override val exceptionList: MutableExceptionListType
) : BaseEventContext(exceptionList)

/**
 *  Event triggered, when new module loaded in memory.
 *
 *  @property [descriptor] loaded module descriptor.
 *  @property [exceptionList] list of exceptions thrown in event method handler.
 *  @author bogomolov-a-a
 */
data class OnLoadedEventContext(
    val descriptor: LoadableModuleDescriptor,
    override val exceptionList: MutableExceptionListType
) : BaseEventContext(exceptionList)

/**
 *  Event triggered, when module preparing unloaded from memory.
 *
 *  @property [descriptor] loaded module descriptor.
 *  @property [exceptionList] list of exceptions thrown in event method handler.
 *  @author bogomolov-a-a
 */
data class OnBeforeUnloadEventContext(
    val descriptor: LoadableModuleDescriptor,
    override val exceptionList: MutableExceptionListType
) : BaseEventContext(exceptionList)

/**
 *  Event triggered, when module successful unloaded from memory.
 *
 *  @property [descriptor] loaded module descriptor.
 *  @property [exceptionList] list of exceptions thrown in event method handler.
 *  @author bogomolov-a-a
 */
data class OnAfterUnloadEventContext(
    val descriptor: LoadableModuleDescriptor,
    override val exceptionList: MutableExceptionListType
) : BaseEventContext(exceptionList)

/**
 * Event triggered, when module successful unloaded from memory.
 *
 *  @property [descriptor] loaded module descriptor.
 *  @property [moduleManager] current module manager.
 *  @property [exceptionList] list of exceptions thrown in event method handler.
 *  @author bogomolov-a-a

 */
data class OnFailedEventContext(
    val descriptor: LoadableModuleDescriptor,
    val moduleManager: IModuleManager,
    override val exceptionList: MutableExceptionListType
) : BaseEventContext(exceptionList)

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
data class OnBeforeStartEventContext(
    val moduleManager: IModuleManager,
    override val exceptionList: MutableExceptionListType
) : BaseEventContext(exceptionList)

/**
 * Event triggered, when module try to validate start ability.
 *
 * @property [descriptor] loaded module descriptor.
 * @property [moduleManager] current module manager.
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnPreconditionsValidateEventContext(
    val descriptor: LoadableModuleDescriptor,
    val moduleManager: IModuleManager,
    override val exceptionList: MutableExceptionListType
) : BaseEventContext(exceptionList)

/**
 * Event triggered, when module try to get resources from another module for self initialization.
 *
 * @property [moduleManager] current module manager.
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnInitializedDependenciesWaitEventContext(
    val moduleManager: IModuleManager,
    override val exceptionList: MutableExceptionListType
) : BaseEventContext(exceptionList)

/**
 * Event triggered, when module try to initialize self resources.
 *
 * @property [descriptor] loaded module descriptor.
 * @property [moduleManager] current module manager.
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnInitializeResourcesEventContext(
    val descriptor: LoadableModuleDescriptor,
    val moduleManager: IModuleManager,
    override val exceptionList: MutableExceptionListType
) : BaseEventContext(exceptionList)

/**
 * Event triggered, when module successful started.
 *
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnAfterStartEventContext(
    override val exceptionList: MutableExceptionListType
) : BaseEventContext(exceptionList)

/**
 * Event triggered, when module preparing to stop.
 *
 * @property [descriptor] loaded module descriptor.
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnBeforeStopEventContext(
    val descriptor: LoadableModuleDescriptor,
    override val exceptionList: MutableExceptionListType
) : BaseEventContext(exceptionList)

/**
 * Event triggered, when module preparing resources release.
 *
 * @property [moduleManager] current module manager.
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnResourcesReleaseEventContext(
    val moduleManager: IModuleManager,
    override val exceptionList: MutableExceptionListType
) : BaseEventContext(exceptionList)

/**
 * Event triggered, when module successful stopped.
 *
 * @property [moduleManager] current module manager.
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnAfterStopEventContext(
    val moduleManager: IModuleManager,
    override val exceptionList: MutableExceptionListType
) : BaseEventContext(exceptionList)
