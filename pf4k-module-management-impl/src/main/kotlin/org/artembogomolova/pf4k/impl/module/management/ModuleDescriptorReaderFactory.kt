package org.artembogomolova.pf4k.impl.module.management

import java.nio.file.Path
import org.artembogomolova.pf4k.api.module.management.IModuleDescriptorReader
import org.artembogomolova.pf4k.api.module.types.LoadableModuleDescriptor

class ModuleDescriptorReaderFactory {
    companion object {
        fun getModuleDescriptorReader(): IModuleDescriptorReader = ModuleDescriptorReader()
    }
}

internal class ModuleDescriptorReader : IModuleDescriptorReader {

    override fun readFromModuleJar(modulePath: Path): LoadableModuleDescriptor {
        TODO("Not yet implemented")
    }
}
