package org.artembogomolova.pf4k.api.module.management.event

import kotlin.reflect.KClass
import org.artembogomolova.pf4k.api.module.MutableExceptionListType

typealias SubscriberEventTypeList = List<KClass<IOnEventContext>>

interface ISubscriber {
    fun getAvailableEventContextTypes(): SubscriberEventTypeList
    fun handleEvent(eventContext: IOnEventContext): Boolean
}

interface IOnEventContext {
    val exceptionList: MutableExceptionListType
}

data class OnEvent(
    val context: IOnEventContext
)

interface IEventQueue {
    fun pushEvent(event: OnEvent): Boolean
    fun subscribe(subscriber: ISubscriber): Result<Boolean>
}

interface IEventQueueFactory {
    fun createEventQueue(): IEventQueue
}

object EventQueueFactoryBuilder {
    fun createEventQueue(className: String): IEventQueueFactory =
        Class.forName(className).getConstructor().newInstance() as IEventQueueFactory
}