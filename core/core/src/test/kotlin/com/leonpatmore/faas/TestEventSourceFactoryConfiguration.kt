package com.leonpatmore.faas

import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.Message
import com.leonpatmore.fass.common.source.FunctionSourceData
import com.leonpatmore.fass.common.source.HandlerEventSourceFactory
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
        val res = typedHandler.handle(Message("test message"))
        return res.body
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
