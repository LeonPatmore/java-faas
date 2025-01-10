package com.leonpatmore.faas.sqs

import com.fasterxml.jackson.databind.ObjectMapper
import com.leonpatmore.fass.common.EVENT_SOURCE_ENABLED_PROPERTY_PREFIX
import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.HandlerEventSourceFactory
import com.leonpatmore.fass.common.Message
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory
import io.awspring.cloud.sqs.listener.DefaultListenerContainerRegistry
import io.awspring.cloud.sqs.listener.MessageListenerContainerRegistry
import io.awspring.cloud.sqs.listener.SqsMessageListenerContainer
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsAsyncClient

@Component
@ConditionalOnProperty(EVENT_SOURCE_ENABLED_PROPERTY_PREFIX + "sqs.enabled")
class SqsEventSourceFactory(
    private val sqsAsyncClient: SqsAsyncClient,
    private val objectMapper: ObjectMapper,
    private val registry: MessageListenerContainerRegistry
) : HandlerEventSourceFactory<SqsProperties> {
    override fun wrapHandler(
        handler: Handler<*>,
        context: GenericApplicationContext,
        properties: SqsProperties,
    ) {
        val container = createContainer(handler, properties)
        container.start()
        registry.registerListenerContainer(container)
        // TODO : Fix me
        context.registerBean(properties.queueName + "container", SqsMessageListenerContainer::class.java, container)
    }

    private fun <T> createContainer(
        handler: Handler<T>,
        properties: SqsProperties,
    ) = SqsMessageListenerContainerFactory<String>()
        .apply { this.setSqsAsyncClient(sqsAsyncClient) }
        .createContainer(properties.queueName).apply {
            // TODO : fix me
            this.id = properties.queueName
        }
        .withHandler(handler)

    private fun <T> SqsMessageListenerContainer<String>.withHandler(handler: Handler<T>): SqsMessageListenerContainer<String> {
        this.setMessageListener {
            val messageBody: T =
                if (handler.getMessageType() == String::class.java) {
                    it.payload as T
                } else {
                    objectMapper.readValue(it.payload, handler.getMessageType())
                }
            handler.handle(Message(messageBody))
        }
        return this
    }

    override fun getPropertyClass(): Class<SqsProperties> = SqsProperties::class.java
}
