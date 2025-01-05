package com.leonpatmore.faas

import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.Message
import com.leonpatmore.fass.common.NamedHandler
import com.leonpatmore.fass.common.Response
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(classes = [TestConfig::class])
class E2ETests {
    @Autowired
    private lateinit var eventSource: TestEventSource
    @Test
    fun name() {
        eventSource.produce() shouldBe "res"
    }
}

class TestEventSource(private val handler: Handler<*>) {
    fun produce() : Any {
        val typedHandler = handler as Handler<String>
        val res = typedHandler.handle(Message("test message"))
        return res.body
    }
}

class TestEventSourceFactory : HandlerEventSourceFactory {
    override fun wrapHandler(handler: Handler<*>, context: GenericApplicationContext) {
        val testEventSource = TestEventSource(handler)
        context.registerBean {
            testEventSource
        }
    }
}

class TestHandler : NamedHandler<String>("Test handler") {
    override fun handle(msg: Message<String>): Response {
        println("Handling " + msg.body)
        return Response("res")
    }
}

@TestConfiguration
class TestConfig {
    @Bean
    fun testHandler() = TestHandler()
    @Bean
    fun testEventSourceFactory() = TestEventSourceFactory()
}
