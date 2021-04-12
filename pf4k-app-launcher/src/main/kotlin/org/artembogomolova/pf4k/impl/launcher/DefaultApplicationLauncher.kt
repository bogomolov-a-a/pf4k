package org.artembogomolova.pf4k.impl.launcher

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.artembogomolova.pf4k.api.app.IModularizedApplication
import org.artembogomolova.pf4k.api.app.ModularizedApplicationBuilder
import org.artembogomolova.pf4k.api.module.management.types.APPLICATION_DESCRIPTOR_PATH
import org.artembogomolova.pf4k.api.module.management.types.ApplicationDescriptor
import org.artembogomolova.pf4k.logger

/**
 * Default launcher implementation.
 *
 * Launcher create application class instance from application descriptor from jar file
 * and invoke method [IModularizedApplication.run] in [async] coroutine.
 *
 * @author bogomolov-a-a
 *
 * @since 0.2.0
 */
open class DefaultApplicationLauncher {

    private val log = logger(this::class)

    companion object {
        const val APPLICATION_IMPL_CLASS_NAME_PROJECT_PROPERTY_NAME = "application.impl.class.name"
        private val APPLICATION_DESCRIPTOR_READER: ObjectReader = ObjectMapper().reader().forType(ApplicationDescriptor::class.java)

        @JvmStatic
        fun main(args: Array<String>) = DefaultApplicationLauncher().mainMethod(args)
    }

    fun mainMethod(args: Array<String>) = launchApplication(args)

    private fun launchApplication(args: Array<String>) {
        if (log.isDebugEnabled) {
            log.debug("starting application")
        }
        if (log.isDebugEnabled) {
            log.debug("starting arguments ${args.contentToString()}")
        }
        val applicationDescriptor = getApplicationDescriptor()
        val applicationClassName = applicationDescriptor.applicationClassName
        val application = createApplication(applicationClassName)
        runApplicationWithArgs(application, applicationDescriptor.name, args)

    }

    internal open fun runApplicationWithArgs(
        application: IModularizedApplication,
        name: String,
        args: Array<String>
    ) = runBlocking {
        val asyncRun = GlobalScope.async {
            application.run(name, args)
        }
        asyncRun.await()
    }

    private fun createApplication(applicationClassName: String) =
        ModularizedApplicationBuilder
            .createModularizedApplication(applicationClassName)

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    internal open fun getApplicationDescriptor(): ApplicationDescriptor =
        javaClass.classLoader.getResource(APPLICATION_DESCRIPTOR_PATH).openStream().use {
            APPLICATION_DESCRIPTOR_READER.readValue(it)
        }
}