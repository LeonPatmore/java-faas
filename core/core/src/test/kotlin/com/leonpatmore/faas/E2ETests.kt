package com.leonpatmore.faas

import com.leonpatmore.faas.common.TestHandlerConfiguration
import com.leonpatmore.faas.common.target.TestEventTarget
import com.leonpatmore.faas.common.target.TestEventTargetConfiguration
import com.leonpatmore.fass.common.target.EventTarget
import io.kotest.matchers.shouldBe
import io.mockk.verify
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@SpringBootTest(
    properties = [
        "functions.test.source.props.requiredProp=abc",
        "functions.test.handler=stringTestHandler",
        "functions.test.target.factory=testEventTargetFactory",
    ],
)
@ContextConfiguration(
    classes = [
        TestEventSourceFactoryConfiguration::class,
        TestHandlerConfiguration::class,
        TestEventTargetConfiguration::class,
    ],
)
class E2ETests {
    @Autowired
    private lateinit var testEventSource: TestEventSource

    @Autowired
    private lateinit var testEventTarget: EventTarget

    @Test
    fun name() {
        testEventSource.produce() shouldBe "res"

        await().atMost(10.seconds.toJavaDuration()).untilAsserted {
            verify {
                (testEventTarget as TestEventTarget).mock.accept(
                    withArg {
                        it.body shouldBe "res"
                    },
                )
            }
        }
    }
}
