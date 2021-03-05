package org.artembogomova.pf4k.impl

import kotlin.reflect.KClass
import org.slf4j.LoggerFactory

fun logger(clazz: KClass<*>) =
    LoggerFactory.getLogger(clazz.qualifiedName)
