package org.artembogomolova.pf4k.api.module.management.types

import org.artembogomolova.pf4k.api.module.management.event.ISubscriber
import org.artembogomolova.pf4k.api.module.management.event.SubscriberEventTypeList

const val APPLICATION_DESCRIPTOR_PATH = "application-descriptor"

data class ApplicationDescriptor(
    val name: String,
    val applicationClassName: String
)

data class ModuleStatistic(
    var total: Int = 0,
    var loaded: Int = 0,
    var unloaded: Int = 0,
    var starting: Int = 0,
    var running: Int = 0,
    var failed: Int = 0,
    var stopping: Int = 0,
    var stopped: Int = 0,
    var included: Int = 0,
    var excluded: Int = 0
)

data class SubscriberInfo(
    val subscriber: ISubscriber,
    val availableEvents: SubscriberEventTypeList
)
