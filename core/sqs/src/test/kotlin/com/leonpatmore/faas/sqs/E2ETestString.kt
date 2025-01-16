package com.leonpatmore.faas.sqs

import com.leonpatmore.faas.common.TestHandlerConfiguration
import com.leonpatmore.faas.sqs.source.SqsEventSourceFactory
import com.leonpatmore.faas.sqs.source.SqsSourceProperties
import com.leonpatmore.fass.common.Handler
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
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@SpringBootTest(properties = ["logging.level.io.awspring.cloud.sqs=DEBUG", "event.source.sqs.enabled=true"])
@ContextConfiguration(classes = [TestHandlerConfiguration::class], initializers = [SqsE2ETestInitializer::class])
class E2ETestString {
    @Autowired
    private lateinit var sqsTemplate: SqsTemplate

    @Autowired
    private lateinit var stringTestHandler: Handler<String>

    @Autowired
    private lateinit var sqsEventSourceFactory: SqsEventSourceFactory

    @Autowired
    private lateinit var applicationContext: ApplicationContext

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
}
