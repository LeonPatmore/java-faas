package com.leonpatmore.faas

import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.Message
import com.leonpatmore.fass.common.source.FunctionSourceData
import com.leonpatmore.fass.common.source.HandlerEventSourceFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.support.registerBean

@TestConfiguration
class TestEventSourceFactoryConfiguration {
    @Bean
    fun testEventSourceFactory() = TestEventSourceFactory()
}

class TestEventSource(private val handler: Handler<*>) {
    fun produce(): Any {
        val typedHandler = handler as Handler<String>
        LOGGER.info("Handler has type ${typedHandler.getMessageType()}")
        val res = typedHandler.handle(Message("test message"))
        return res.body
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TestEventSource::class.java)
    }
}

data class TestEventSourceProperties(val requiredProp: String, val optional: String = "hello")

class TestEventSourceFactory : HandlerEventSourceFactory<TestEventSourceProperties> {
    override fun wrapHandler(data: FunctionSourceData<TestEventSourceProperties>) {
        val testEventSource = TestEventSource(data.handler)
        data.context.registerBean {
            testEventSource
        }
    }

    override fun getPropertyClass() = TestEventSourceProperties::class.java
}
