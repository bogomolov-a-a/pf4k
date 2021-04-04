package org.artembogomolova.pf4k

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlin.reflect.KClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun logger(clazz: KClass<*>): Logger =
    LoggerFactory.getLogger(clazz.qualifiedName)

const val THREAD_INTERRUPT_INTERVAL = 1000L

val kotlinObjectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())