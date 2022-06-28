package org.artembogomolova.pf4k.api.app

const val EXCLUDED_MODULE_ID_LIST = "excludedModuleIdList"

const val MODULE_ID_SEPARATOR = ","

const val APPLICATION_PATH_PROPERTY = "user.dir"

interface IModularizedApplication {

    suspend fun run(applicationName: String, args: Array<String>)
}

object ModularizedApplicationBuilder {

    fun createModularizedApplication(className: String): IModularizedApplication =
        Class.forName(className).getConstructor().newInstance() as IModularizedApplication
}