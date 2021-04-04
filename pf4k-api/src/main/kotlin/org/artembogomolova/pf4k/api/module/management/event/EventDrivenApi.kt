package org.artembogomolova.pf4k.api.module.management.event

import org.artembogomolova.pf4k.api.module.management.types.MutableExceptionListType

typealias SubscriberEventTypeList = List<Class<IOnEventContext>>

/***
 * Basic interface for component subscribed on event from event bus [IEventBus].
 */
interface ISubscriber {
    /**
     * @return List of descendants of [IOnEventContext] which this implementation can be successful handled.
     */
    fun getAvailableEventContextTypes(): SubscriberEventTypeList

    /**
     * @param eventContext triggered event context. Basically contains property with type [MutableExceptionListType],
     * to collect any exception in execution process occurred.
     * @return true, if event successful handled.
     */
    suspend fun handleEvent(eventContext: IOnEventContext): Boolean
}

/**
 * Basic interface for triggered event context\
 *
 * @property [exceptionList] wrapped exceptions which occurred in handling process.
 */
interface IOnEventContext {
    val exceptionList: MutableExceptionListType
}

/**
 * Event context wrapper
 * @property [context] triggered event context(with specified attached data and exception list).
 */
data class OnEvent(
    val context: IOnEventContext
)

/**
 * EventBus
 * Default implementation based on https://dzone.com/articles/design-patterns-event-bus
 */
interface IEventBus {
    /**
     * @param event triggered event
     * @return true, if successful handled.
     */
    suspend fun pushEvent(event: OnEvent): Boolean

    /**
     * @param subscriber instance of [ISubscriber], can be only one time registered.
     * @return [Result.isSuccess] if register successful.
     * [Result.isFailure] and [Result.exceptionOrNull] return exception, descendants of []
     */
    fun subscribe(subscriber: ISubscriber): Result<Boolean>
}

interface IEventBusFactory {
    fun createEventBus(): IEventBus
}

object EventQueueFactoryBuilder {
    fun createEventBusFactory(className: String): IEventBusFactory =
        Class.forName(className).getConstructor().newInstance() as IEventBusFactory
}