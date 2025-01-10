package com.leonpatmore.faas.sqs

import com.fasterxml.jackson.databind.ObjectMapper
import com.leonpatmore.faas.common.TestHandlerConfiguration
import com.leonpatmore.fass.common.Handler
import io.awspring.cloud.sqs.operations.SqsTemplate
import io.kotest.matchers.shouldBe
import io.mockk.verify
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.test.context.ContextConfiguration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@SpringBootTest(
    classes = [TestConfig::class, TestHandlerConfiguration::class],
    properties = ["logging.level.io.awspring.cloud.sqs=DEBUG"],
)
@ContextConfiguration(initializers = [SqsE2ETestInitializer::class])
class E2ETest {
    @Autowired
    private lateinit var sqsTemplate: SqsTemplate

    @Autowired
    private lateinit var stringTestHandler: Handler<String>

    @Autowired
    private lateinit var sqsEventSourceFactory: SqsEventSourceFactory

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun name() {
        sqsEventSourceFactory.wrapHandler(stringTestHandler, applicationContext as GenericApplicationContext, SqsProperties("test-queue"))

        sqsTemplate.send { it.queue("test-queue").payload("some-payload") }

        await().atMost(10.seconds.toJavaDuration()).untilAsserted {
            verify {
                stringTestHandler.handle(
                    withArg {
                        it.body shouldBe "some-payload"
                    },
                )
            }
        }
    }
}

@TestConfiguration
class TestConfig {
    @Bean
    fun objectMapper() = ObjectMapper()
}

@SpringBootApplication
class TestApp
