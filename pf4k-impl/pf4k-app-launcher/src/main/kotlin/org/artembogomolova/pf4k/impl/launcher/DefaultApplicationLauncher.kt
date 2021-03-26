package org.artembogomolova.pf4k.impl.launcher

import java.util.jar.Manifest
import org.artembogomolova.pf4k.api.app.ModularizedApplicationBuilder

object DefaultApplicationLauncher {

    const val APPLICATION_IMPL_CLASS_NAME_PROJECT_PROPERTY_NAME = "application.impl.class.name"
    const val APPLICATION_IMPL_CLASS_NAME_PROPERTY = "Application-Impl-Class-Name"
    private const val MANIFEST_URI = "META-INF/MANIFEST.MF"

    @JvmStatic
    fun main(args: Array<String>) {
        val manifest = getManifest()
        val applicationClassName = getApplicationImplClassName(manifest)
        ModularizedApplicationBuilder
            .createModularizedApplication(applicationClassName)
            .run(manifest, args)
    }

    private fun getApplicationImplClassName(manifest: Manifest): String =
        manifest.mainAttributes[APPLICATION_IMPL_CLASS_NAME_PROPERTY] as String

    private fun getManifest(): Manifest {
        val result: Manifest
        javaClass.classLoader.getResource(MANIFEST_URI).openStream().use {
            result = Manifest(it)
        }
        return result
    }
}