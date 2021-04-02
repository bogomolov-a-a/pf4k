package org.artembogomolova.pf4k.impl.launcher

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import org.artembogomolova.pf4k.api.app.ModularizedApplicationBuilder
import org.artembogomolova.pf4k.api.module.management.types.APPLICATION_DESCRIPTOR_PATH
import org.artembogomolova.pf4k.api.module.management.types.ApplicationDescriptor

object DefaultApplicationLauncher {

    const val APPLICATION_IMPL_CLASS_NAME_PROJECT_PROPERTY_NAME = "application.impl.class.name"
    const val APPLICATION_LAUNCHER_CLASS_NAME_PROJECT_PROPERTY_NAME = "application.launcher.class.name"
    private val APPLICATION_DESCRIPTOR_READER: ObjectReader = ObjectMapper().reader().forType(ApplicationDescriptor::class.java)

    @JvmStatic
    suspend fun main(args: Array<String>) {
        val applicationDescriptor = getApplicationDescriptor()
        val applicationClassName = applicationDescriptor.applicationClassName
        ModularizedApplicationBuilder
            .createModularizedApplication(applicationClassName)
            .run(applicationDescriptor.name, args)
    }

    private fun getApplicationDescriptor(): ApplicationDescriptor {
        val result: ApplicationDescriptor
        javaClass.classLoader.getResource(APPLICATION_DESCRIPTOR_PATH).openStream().use {
            result = APPLICATION_DESCRIPTOR_READER.readValue(it)
        }
        return result
    }
}