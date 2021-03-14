package org.artembogomolova.pf4k.api.app

import java.util.jar.Manifest

const val APPLICATION_NAME_ATTRIBUTE_NAME = "Application-Name"

const val EXCLUDED_MODULE_ID_LIST = "excludedModuleIdList"

const val MODULE_ID_SEPARATOR = ","

const val APPLICATION_PATH_PROPERTY = "user.dir"

interface IModularizedApplication {

    fun run(manifest: Manifest, args: Array<String>)
}

object ModularizedApplicationBuilder {

    fun createModularizedApplication(className: String): IModularizedApplication =
        Class.forName(className).getConstructor().newInstance() as IModularizedApplication
}