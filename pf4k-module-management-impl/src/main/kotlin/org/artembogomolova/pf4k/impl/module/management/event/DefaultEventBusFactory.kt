package org.artembogomolova.pf4k.impl.module.management.event

import java.util.concurrent.ConcurrentHashMap
import org.artembogomolova.pf4k.api.SubscribeRegisterException
import org.artembogomolova.pf4k.api.module.management.event.IEventBus
import org.artembogomolova.pf4k.api.module.management.event.IEventBusFactory
import org.artembogomolova.pf4k.api.module.management.event.IOnEventContext
import org.artembogomolova.pf4k.api.module.management.event.ISubscriber
import org.artembogomolova.pf4k.api.module.management.event.OnEvent
import org.artembogomolova.pf4k.api.module.management.types.SubscriberInfo
import org.artembogomolova.pf4k.logger

class DefaultEventBusFactory : IEventBusFactory {
    override fun createEventBus(): IEventBus = DefaultEventBus()
}

private class DefaultEventBus : IEventBus {

    private val log = logger(this::class)
    private val subscribers: MutableMap<Class<ISubscriber>, SubscriberInfo> = ConcurrentHashMap()

    override suspend fun pushEvent(event: OnEvent): Boolean {
        for (subscriberInfo in subscribers.values) {
            val result = tryToHandleEvent(subscriberInfo, event)
            if (result) {
                log.info("event with context ${event.context} handled by ${subscriberInfo.subscriber}")
            }
        }
        log.warn("event with context ${event.context} can't successful handled")
        return false
    }

    private suspend fun tryToHandleEvent(subscriberInfo: SubscriberInfo, event: OnEvent): Boolean {
        val context = event.context
        if (supportPushEvent(subscriberInfo, context.javaClass).not()) {
            return false
        }
        return subscriberInfo.subscriber.handleEvent(event.context)
    }

    private fun supportPushEvent(subscriberInfo: SubscriberInfo, eventClass: Class<IOnEventContext>): Boolean =
        subscriberInfo.availableEvents.contains(eventClass)


    override fun subscribe(subscriber: ISubscriber): Result<Boolean> {
        val subscriberClass = subscriber.javaClass
        if (subscribers.containsKey(subscriberClass)) {
            return Result.failure(SubscribeRegisterException("$subscriber already subscribed for events!"))
        }
        subscribers[subscriberClass] = SubscriberInfo(subscriber, subscriber.getAvailableEventContextTypes())
        return Result.success(true)
    }

}

