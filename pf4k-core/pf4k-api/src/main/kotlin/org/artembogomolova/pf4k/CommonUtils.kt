package org.artembogomolova.pf4k

import kotlin.reflect.KClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun logger(clazz: KClass<*>): Logger =
    LoggerFactory.getLogger(clazz.qualifiedName)
