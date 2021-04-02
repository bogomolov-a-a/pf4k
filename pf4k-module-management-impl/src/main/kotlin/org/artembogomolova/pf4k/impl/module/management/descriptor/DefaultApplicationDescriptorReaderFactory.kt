package org.artembogomolova.pf4k.impl.module.management.descriptor

import com.fasterxml.jackson.databind.ObjectReader
import java.io.IOException
import java.net.URLClassLoader
import org.artembogomolova.pf4k.KOTLIN_OBJECT_MAPPER
import org.artembogomolova.pf4k.api.BasicModuleException
import org.artembogomolova.pf4k.api.module.management.IApplicationDescriptorReader
import org.artembogomolova.pf4k.api.module.management.IApplicationDescriptorReaderFactory
import org.artembogomolova.pf4k.api.module.management.types.APPLICATION_DESCRIPTOR_PATH
import org.artembogomolova.pf4k.api.module.management.types.ApplicationDescriptor

class DefaultApplicationDescriptorReaderFactory : IApplicationDescriptorReaderFactory {
    override fun createApplicationDescriptorReader(): IApplicationDescriptorReader = DefaultApplicationDescriptorReader()
}

internal class DefaultApplicationDescriptorReader : IApplicationDescriptorReader {

    companion object {
        val APPLICATION_DESCRIPTOR_TYPE_READER: ObjectReader = KOTLIN_OBJECT_MAPPER.reader().forType(ApplicationDescriptor::class.java)
    }

    override fun readFromModuleJar(moduleClassLoader: URLClassLoader): Result<ApplicationDescriptor> {
        val moduleDescriptorResource = moduleClassLoader.getResource(APPLICATION_DESCRIPTOR_PATH)
            ?: return Result.failure(BasicModuleException("Can't locate $APPLICATION_DESCRIPTOR_PATH into '${moduleClassLoader.urLs[0]}'"))
        return try {
            Result.success(APPLICATION_DESCRIPTOR_TYPE_READER.readValue(moduleDescriptorResource))
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}