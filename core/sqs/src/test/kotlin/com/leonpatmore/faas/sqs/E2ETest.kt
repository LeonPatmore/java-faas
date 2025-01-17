package com.leonpatmore.faas.sqs

import com.leonpatmore.faas.common.TestDto
import com.leonpatmore.faas.common.TestDtoHandler
import com.leonpatmore.faas.common.TestHandlerConfiguration
import com.leonpatmore.faas.sqs.source.SqsEventSourceFactory
import com.leonpatmore.faas.sqs.source.SqsSourceProperties
import com.leonpatmore.faas.sqs.target.SqsEventTargetFactory
import com.leonpatmore.faas.sqs.target.SqsTargetProperties
import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.Response
import com.leonpatmore.fass.common.source.FunctionSourceData
import io.awspring.cloud.sqs.operations.SqsTemplate
import io.kotest.matchers.shouldBe
import io.mockk.verify
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.test.context.ContextConfiguration
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@SpringBootTest(
    properties = [
        "logging.level.io.awspring.cloud.sqs=DEBUG",
        "event.source.sqs.enabled=true",
        "event.target.sqs.enabled=true",
    ],
)
@ContextConfiguration(
    classes = [TestHandlerConfiguration::class, E2ETestConfiguration::class],
    initializers = [SqsE2ETestInitializer::class],
)
class E2ETest {
    @Autowired
    private lateinit var sqsTemplate: SqsTemplate

    @Autowired
    private lateinit var testDtoHandler: TestDtoHandler

    @Autowired
    private lateinit var stringTestHandler: Handler<String>

    @Autowired
    private lateinit var sqsEventSourceFactory: SqsEventSourceFactory

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var sqsEventTargetFactory: SqsEventTargetFactory

    @Autowired
    private lateinit var sqsAsyncClient: SqsAsyncClient

    @Test
    fun `test string`() {
        sqsEventSourceFactory.wrapHandler(
            FunctionSourceData(
                "stringFunction",
                stringTestHandler,
                applicationContext as GenericApplicationContext,
                SqsSourceProperties("test-queue-string"),
            ),
        )

        sqsTemplate.send { it.queue("test-queue-string").payload("some-payload") }

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

    @Test
    fun `test event target`() {
        val target = sqsEventTargetFactory.generateTarget(SqsTargetProperties("targetQueueName"))

        target.handle(Response(TestDto("leon", "patmore")))

        val queueUrl = sqsAsyncClient.getQueueUrl { it.queueName("targetQueueName") }
        val messages =
            sqsAsyncClient.receiveMessage {
                it.queueUrl(queueUrl.join().queueUrl())
                it.maxNumberOfMessages(1)
                it.waitTimeSeconds(3)
            }.join().messages()
        messages.size shouldBe 1
        messages[0].body() shouldBe "{\"firstName\":\"leon\",\"secondName\":\"patmore\"}"
    }
}
