package com.leonpatmore.faas.common

import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.Message
import com.leonpatmore.fass.common.Response
import io.mockk.every
import io.mockk.mockk
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

data class TestDto(val firstName: String, val secondName: String)

class TestDtoHandler : Handler<TestDto> {
    val mock = mockk<Consumer<TestDto>>()

    init {
        every { mock.accept(any()) } returns Unit
    }

    override fun handle(msg: Message<TestDto>): Response {
        mock.accept(msg.body)
        val helloString = "Hello ${msg.body.firstName} ${msg.body.secondName}"
        LOGGER.info("Processing message, returning [ $helloString ]")
        return Response(helloString)
    }

    override fun getMessageType(): Class<TestDto> = TestDto::class.java

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TestDtoHandler::class.java)
    }
}
