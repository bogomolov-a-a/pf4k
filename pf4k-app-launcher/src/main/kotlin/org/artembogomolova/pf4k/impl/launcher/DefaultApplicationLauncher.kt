package org.artembogomolova.pf4k.impl.launcher

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.artembogomolova.pf4k.api.app.ModularizedApplicationBuilder
import org.artembogomolova.pf4k.api.module.management.types.APPLICATION_DESCRIPTOR_PATH
import org.artembogomolova.pf4k.api.module.management.types.ApplicationDescriptor

open class DefaultApplicationLauncher {

    companion object {
        const val APPLICATION_IMPL_CLASS_NAME_PROJECT_PROPERTY_NAME = "application.impl.class.name"
        private val APPLICATION_DESCRIPTOR_READER: ObjectReader = ObjectMapper().reader().forType(ApplicationDescriptor::class.java)

        @JvmStatic
        fun main(args: Array<String>) = DefaultApplicationLauncher().mainMethod(args)
    }

    fun mainMethod(args: Array<String>) = runBlocking {
        val asyncRun = GlobalScope.async {
            launchApplication(args)
        }
        asyncRun.await()
    }

    private suspend fun launchApplication(args: Array<String>) {
        val applicationDescriptor = getApplicationDescriptor()
        val applicationClassName = applicationDescriptor.applicationClassName
        ModularizedApplicationBuilder
            .createModularizedApplication(applicationClassName)
            .run(applicationDescriptor.name, args)

    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    internal open fun getApplicationDescriptor(): ApplicationDescriptor =
        javaClass.classLoader.getResource(APPLICATION_DESCRIPTOR_PATH).openStream().use {
            APPLICATION_DESCRIPTOR_READER.readValue(it)
        }


}