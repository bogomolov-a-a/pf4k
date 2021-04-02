package org.artembogomolova.pf4k.impl.module.management.descriptor

import com.fasterxml.jackson.databind.ObjectReader
import java.io.IOException
import java.net.URLClassLoader
import org.artembogomolova.pf4k.KOTLIN_OBJECT_MAPPER
import org.artembogomolova.pf4k.api.BasicModuleException
import org.artembogomolova.pf4k.api.module.management.IModuleDescriptorReader
import org.artembogomolova.pf4k.api.module.management.IModuleDescriptorReaderFactory
import org.artembogomolova.pf4k.api.module.management.MODULE_DESCRIPTOR_PATH
import org.artembogomolova.pf4k.api.module.types.LoadableModuleDescriptor

class DefaultModuleDescriptorReaderFactory : IModuleDescriptorReaderFactory {
    override fun createModuleDescriptorReader(): IModuleDescriptorReader = ModuleDescriptorReader()
}

internal class ModuleDescriptorReader : IModuleDescriptorReader {

    companion object {
        val MODULE_DESCRIPTOR_TYPE_READER: ObjectReader = KOTLIN_OBJECT_MAPPER.reader().forType(LoadableModuleDescriptor::class.java)
    }

    override fun readFromModuleJar(moduleClassLoader: URLClassLoader): Result<LoadableModuleDescriptor> {
        val moduleDescriptorResource = moduleClassLoader.getResource(MODULE_DESCRIPTOR_PATH)
            ?: return Result.failure(BasicModuleException("Can't locate $MODULE_DESCRIPTOR_PATH into '${moduleClassLoader.urLs[0]}'. May be jar is not loadable module?"))
        return try {
            Result.success(MODULE_DESCRIPTOR_TYPE_READER.readValue(moduleDescriptorResource))
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}

