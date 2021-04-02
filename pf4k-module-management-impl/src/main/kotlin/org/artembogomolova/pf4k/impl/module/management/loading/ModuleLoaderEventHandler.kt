package org.artembogomolova.pf4k.impl.module.management.loading

import org.artembogomolova.pf4k.api.module.management.IModuleLoader
import org.artembogomolova.pf4k.api.module.management.event.IOnEventContext

object ModuleLoaderEventHandler {
    fun handleEvent(moduleLoader: IModuleLoader, eventContext: IOnEventContext): Boolean = false
}