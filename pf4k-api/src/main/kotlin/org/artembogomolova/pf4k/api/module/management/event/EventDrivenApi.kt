package org.artembogomolova.pf4k.api.module.management.event

import org.artembogomolova.pf4k.api.module.management.types.MutableExceptionListType

typealias SubscriberEventTypeList = List<Class<IOnEventContext>>

/***
 *  Basic interface for subscribable
 */
interface ISubscriber {
    fun getAvailableEventContextTypes(): SubscriberEventTypeList
    suspend fun handleEvent(eventContext: IOnEventContext): Boolean
}

interface IOnEventContext {
    val exceptionList: MutableExceptionListType
}

data class OnEvent(
    val context: IOnEventContext
)

interface IEventQueue {
    suspend fun pushEvent(event: OnEvent): Boolean
    fun subscribe(subscriber: ISubscriber): Result<Boolean>
}

interface IEventQueueFactory {
    fun createEventQueue(): IEventQueue
}

object EventQueueFactoryBuilder {
    fun createEventQueue(className: String): IEventQueueFactory =
        Class.forName(className).getConstructor().newInstance() as IEventQueueFactory
}