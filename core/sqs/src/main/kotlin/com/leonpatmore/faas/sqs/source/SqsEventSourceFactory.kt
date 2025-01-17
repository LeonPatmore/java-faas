package com.leonpatmore.faas.sqs.source

import com.fasterxml.jackson.databind.ObjectMapper
import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.Message
import com.leonpatmore.fass.common.source.EVENT_SOURCE_ENABLED_PROPERTY_PREFIX
import com.leonpatmore.fass.common.source.FunctionSourceData
import com.leonpatmore.fass.common.source.HandlerEventSourceFactory
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory
import io.awspring.cloud.sqs.listener.DefaultListenerContainerRegistry
import io.awspring.cloud.sqs.listener.SqsMessageListenerContainer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsAsyncClient

@Component
@ConditionalOnProperty(EVENT_SOURCE_ENABLED_PROPERTY_PREFIX + "sqs.enabled")
class SqsEventSourceFactory(
    private val sqsAsyncClient: SqsAsyncClient,
    private val objectMapper: ObjectMapper,
) : HandlerEventSourceFactory<SqsSourceProperties> {
    override fun wrapHandler(data: FunctionSourceData<SqsSourceProperties>) {
        val container = createContainer(data)
        val registry = DefaultListenerContainerRegistry()
        registry.registerListenerContainer(container)
        registry.start()
        data.context.beanFactory.registerSingleton(data.functionName + "SqsListenerContainer", container)
        data.context.beanFactory.registerSingleton(data.functionName + "SqsContainerRegistry", registry)
    }

    override fun getPropertyClass(): Class<SqsSourceProperties> = SqsSourceProperties::class.java

    private fun createContainer(data: FunctionSourceData<SqsSourceProperties>) =
        SqsMessageListenerContainerFactory<String>()
            .apply { this.setSqsAsyncClient(sqsAsyncClient) }
            .createContainer(data.properties.queueName).apply {
                this.id = "${data.functionName}Container"
            }
            .withHandler(data.handler)

    private fun <T> SqsMessageListenerContainer<String>.withHandler(handler: Handler<T>): SqsMessageListenerContainer<String> {
        this.setMessageListener {
            LOGGER.info("Received raw sqs message [ ${it.payload} ]")
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

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(SqsEventSourceFactory::class.java)
    }
}
