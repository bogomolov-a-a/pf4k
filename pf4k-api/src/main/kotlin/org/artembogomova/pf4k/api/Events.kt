package org.artembogomova.pf4k.api

import java.nio.file.Path

typealias  ExceptionListType = MutableList<Exception>
typealias  DependencyPathListType = List<Path>

/**
 * Event triggered, when new module found in classpath.
 *
 * @property [resolvedPath] path with module jar
 * @property [dependencyPathList] list of path dependencies of module in [resolvedPath]
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnResolvedEvent(
    val resolvedPath: Path,
    val dependencyPathList: DependencyPathListType,
    val exceptionList: ExceptionListType
)

/**
 *  Event triggered, when new module loaded in memory.
 *
 *  @property [descriptor] loaded module descriptor
 *  @property [exceptionList] list of exceptions thrown in event method handler.
 *  @author bogomolov-a-a
 */
data class OnLoadEvent(
    val descriptor: LoadableModuleDescriptor,
    val exceptionList: ExceptionListType
)

/**
 * Event triggered, when module try to start.
 *
 * @property [descriptor] loaded module descriptor.
 * @property [moduleManager] current module manager.
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnBeforeStartEvent(
    val descriptor: LoadableModuleDescriptor,
    val moduleManager: IModuleManager,
    val exceptionList: ExceptionListType
)

/**
 * Event triggered, when module successful started.
 *
 * @property [exceptionList] list of exceptions thrown in event method handler.
 * @author bogomolov-a-a
 */
data class OnAfterStartEvent(
    val exceptionList: ExceptionListType
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
    val exceptionList: ExceptionListType
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
    val exceptionList: ExceptionListType
)