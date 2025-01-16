package com.leonpatmore.faas.sqs

import com.leonpatmore.faas.common.TestDto
import com.leonpatmore.faas.common.TestDtoHandler
import com.leonpatmore.faas.common.TestHandlerConfiguration
import com.leonpatmore.faas.sqs.source.SqsEventSourceFactory
import com.leonpatmore.faas.sqs.source.SqsSourceProperties
import com.leonpatmore.fass.common.source.FunctionSourceData
import io.awspring.cloud.sqs.operations.SqsTemplate
import io.kotest.matchers.shouldBe
import io.mockk.verify
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.GenericApplicationContext
import org.springframework.test.context.ContextConfiguration
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@SpringBootTest(properties = [
    "logging.level.io.awspring.cloud.sqs=DEBUG",
    "event.source.sqs.enabled=true",
    "root.target.factory=asd"
])
@ContextConfiguration(classes = [TestHandlerConfiguration::class], initializers = [SqsE2ETestInitializer::class])
class E2ETest {
    @Autowired
    private lateinit var sqsTemplate: SqsTemplate

    @Autowired
    private lateinit var testDtoHandler: TestDtoHandler

    @Autowired
    private lateinit var sqsEventSourceFactory: SqsEventSourceFactory

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun `test message`() {
        sqsEventSourceFactory.wrapHandler(
            FunctionSourceData(
                "objectFunction",
                testDtoHandler,
                applicationContext as GenericApplicationContext,
                SqsSourceProperties("test-queue"),
            ),
        )

        sqsTemplate.send { it.queue("test-queue").payload(TestDto("leon", "patmore")) }

        await().atMost(10.seconds.toJavaDuration()).untilAsserted {
            verify {
                testDtoHandler.mock.accept(
                    withArg {
                        it.firstName shouldBe "leon"
                        it.secondName shouldBe "patmore"
                    },
                )
            }
        }
    }
}

@Configuration
class TestConfig {
    @Bean
    fun sqsTemplate(sqsAsyncClient: SqsAsyncClient): SqsTemplate {
        return SqsTemplate.builder()
            .sqsAsyncClient(sqsAsyncClient)
            .configureDefaultConverter {
                it.doNotSendPayloadTypeHeader()
            }
            .build()
    }
}

@SpringBootApplication
class TestApp
