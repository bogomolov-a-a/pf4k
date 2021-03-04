package org.artembogomova.pf4k.impl.module.management

import java.nio.file.Path
import org.artembogomova.pf4k.api.module.management.IModuleDescriptorReader
import org.artembogomova.pf4k.api.types.LoadableModuleDescriptor

class ModuleDescriptorReaderFactory {
    companion object {
        fun getModuleDescriptorReader(): IModuleDescriptorReader = ModuleDescriptorReader()
    }
}

private class ModuleDescriptorReader : IModuleDescriptorReader {

    override fun readFromModuleJar(modulePath: Path): LoadableModuleDescriptor {
        TODO("Not yet implemented")
    }
}
