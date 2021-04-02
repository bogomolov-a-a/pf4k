package org.artembogomolova.pf4k.impl.module.management.descriptor

import org.artembogomolova.pf4k.api.module.management.ApplicationDescriptorReaderFactoryBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class DefaultApplicationDescriptorReaderFactoryTest {
    @Test
    fun createApplicationDescriptorReader() {
        Assertions.assertNotNull(
            ApplicationDescriptorReaderFactoryBuilder
                .createFactory(DefaultApplicationDescriptorReaderFactory::class.java.name)
                .createApplicationDescriptorReader(),
            "Can't create IApplicationDescriptorReader instance for"
        )
    }
}