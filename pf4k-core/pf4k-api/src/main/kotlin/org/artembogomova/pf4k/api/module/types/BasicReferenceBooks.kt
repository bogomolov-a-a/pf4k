package org.artembogomova.pf4k.api.module.types

import org.artembogomova.pf4k.api.module.management.IModuleManager

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
     * Loadable module validate conditions(contract) for starting.
     * If module is core, preconditions don't validate typically, else
     * plugged module must be decided "load or not load" and report to [IModuleManager] about it.
     *
     * @author bogomolov-a-a
     */
    PRECONDITION_VALIDATED,

    /**
     * All dependencies are loaded and their statuses are [RESOURCE_INITIALIZED]
     * Try to get information about resources from them.
     *
     * @author bogomolov-a-a
     */
    INITIALIZED_DEPENDENCIES_RESOURCES_GET,

    /**
     * Step for initialize state of loadable module, db connections, configurations and etc.
     * On this step module can interact with another module and report [IModuleManager] about
     * necessary to load another modules.
     *
     * @author bogomolov-a-a
     */
    RESOURCE_INITIALIZED,

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
     * Resource not available after this status,
     * ALL sensitive datas MUST BE CLEARED!!!
     */
    RESOURCE_RELEASED,

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