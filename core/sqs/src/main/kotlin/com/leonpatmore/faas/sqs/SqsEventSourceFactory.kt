package com.leonpatmore.faas.sqs

import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.HandlerEventSourceFactory
import com.leonpatmore.fass.common.Message
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory
import io.awspring.cloud.sqs.listener.SqsMessageListenerContainer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsAsyncClient

@Component
class SqsEventSourceFactory(private val sqsAsyncClient: SqsAsyncClient) : HandlerEventSourceFactory<SqsProperties> {
    override fun wrapHandler(
        handler: Handler<*>,
        context: GenericApplicationContext,
        properties: SqsProperties,
    ) {
        val container = createContainer(handler, properties)
        container.start()
        context.registerBean(SqsMessageListenerContainer::class.java, container)
    }

    private fun <T> createContainer(
        handler: Handler<T>,
        properties: SqsProperties,
    ) = SqsMessageListenerContainerFactory<T>()
        .apply { this.setSqsAsyncClient(sqsAsyncClient) }
        .createContainer(properties.queueName)
        .withHandler(handler)

    private fun <T> SqsMessageListenerContainer<T>.withHandler(handler: Handler<T>): SqsMessageListenerContainer<T> {
        this.setMessageListener {
            handler.handle(Message(it.payload))
        }
        return this
    }

    override fun getPropertyClass(): Class<SqsProperties> = SqsProperties::class.java
}
