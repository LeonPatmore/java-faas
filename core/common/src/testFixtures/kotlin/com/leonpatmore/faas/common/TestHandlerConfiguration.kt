package com.leonpatmore.faas.common

import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.Response
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestHandlerConfiguration {
    @Bean
    fun stringTestHandler(): Handler<String> {
        val mock = mockk<Handler<String>>()
        every { mock.getMessageType() } returns String::class.java
        every { mock.handle(any()) } answers {
            Response("res")
        }
        return mock
    }

    @Bean
    fun testDtoHandler() = TestDtoHandler()
}
