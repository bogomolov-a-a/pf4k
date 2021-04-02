package org.artembogomolova.pf4k.impl.module

import org.artembogomolova.pf4k.api.module.OnAfterStartEventContext
import org.artembogomolova.pf4k.api.module.OnAfterStopEventContext
import org.artembogomolova.pf4k.api.module.OnBeforeStartEventContext
import org.artembogomolova.pf4k.api.module.OnBeforeStopEventContext
import org.artembogomolova.pf4k.api.module.OnInitializeResourcesEventContext
import org.artembogomolova.pf4k.api.module.OnInitializedDependenciesWaitEventContext
import org.artembogomolova.pf4k.api.module.OnPreconditionsValidateEventContext
import org.artembogomolova.pf4k.api.module.OnResourcesReleaseEventContext

import org.artembogomolova.pf4k.api.module.management.event.IOnEventContext

internal object ModuleEventHandler {
    suspend fun handleEvent(loadableModule: AbstractLoadableModule, eventContext: IOnEventContext): Boolean {
        if (isStartEvent(eventContext)) {
            return handleStartEvent(loadableModule, eventContext)
        }
        if (isStopEvent(eventContext)) {
            return handleStopEvent(loadableModule, eventContext)
        }
        return false
    }

    private fun handleStopEvent(loadableModule: AbstractLoadableModule, eventContext: IOnEventContext): Boolean {
        if (eventContext is OnBeforeStopEventContext) {
            return loadableModule.onBeforeStop(eventContext)
        }
        if (eventContext is OnAfterStopEventContext) {
            return loadableModule.onAfterStop(eventContext)
        }
        /*Event can't successful handled by this module*/
        return handleReleaseEvent(loadableModule, eventContext)
    }

    private fun handleReleaseEvent(loadableModule: AbstractLoadableModule, eventContext: IOnEventContext): Boolean {
        if (eventContext is OnResourcesReleaseEventContext) {
            return loadableModule.onResourcesRelease(eventContext)
        }
        return false
    }

    private fun isStopEvent(eventContext: IOnEventContext): Boolean =
        (eventContext is OnBeforeStopEventContext) or
                (eventContext is OnAfterStopEventContext) or
                (isReleaseResourcesEvent(eventContext))

    private fun isReleaseResourcesEvent(eventContext: IOnEventContext): Boolean =
        eventContext is OnResourcesReleaseEventContext

    private suspend fun handleStartEvent(loadableModule: AbstractLoadableModule, eventContext: IOnEventContext): Boolean {
        if (eventContext is OnBeforeStartEventContext) {
            return loadableModule.onBeforeStart(eventContext)
        }
        if (eventContext is OnAfterStartEventContext) {
            return loadableModule.onAfterStart(eventContext)
        }
        return handleInitializerEvent(loadableModule, eventContext)
    }

    private suspend fun handleInitializerEvent(loadableModule: AbstractLoadableModule, eventContext: IOnEventContext): Boolean {
        if (eventContext is OnPreconditionsValidateEventContext) {
            return loadableModule.onPreconditionsValidate(eventContext)
        }
        if (eventContext is OnInitializeResourcesEventContext) {
            return loadableModule.onInitializeResources(eventContext)
        }
        return handleIntercommunicationEvent(loadableModule, eventContext)

    }

    private suspend fun handleIntercommunicationEvent(loadableModule: AbstractLoadableModule, eventContext: IOnEventContext): Boolean {
        if (eventContext is OnInitializedDependenciesWaitEventContext) {
            return loadableModule.onInitializedDependenciesWait(eventContext)
        }
        return false
    }

    private fun isStartEvent(eventContext: IOnEventContext): Boolean =
        (eventContext is OnBeforeStartEventContext) or
                (eventContext is OnAfterStartEventContext) or
                (isInitializerEvent(eventContext))

    private fun isInitializerEvent(eventContext: IOnEventContext): Boolean =
        (eventContext is OnPreconditionsValidateEventContext) or
                (eventContext is OnInitializedDependenciesWaitEventContext) or
                (eventContext is OnInitializeResourcesEventContext)
}