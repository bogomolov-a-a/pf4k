package org.artembogomolova.pf4k.impl.core

import org.artembogomolova.pf4k.api.module.ICoreModule
import org.artembogomolova.pf4k.api.module.types.LoadableModuleState
import org.artembogomolova.pf4k.impl.module.AbstractLoadableModule

data class Parameter(val value: String)
typealias  ParameterList = List<Parameter>

abstract class AbstractCoreModule(
    override val loadableModuleState: LoadableModuleState
) : AbstractLoadableModule(
    loadableModuleState
), ICoreModule {

    companion object {
        const val PARAMETERS_RESOURCE_NAME = "cli-arguments"
    }

    override suspend fun run(args: Array<String>) {
        addResourceToMap(PARAMETERS_RESOURCE_NAME, args.map(::Parameter).toList())
        runModule()
    }


}

