package com.leonpatmore.faas.common.target

import com.leonpatmore.fass.common.Response
import com.leonpatmore.fass.common.target.EventTarget
import com.leonpatmore.fass.common.target.HandlerEventTargetFactory
import io.mockk.every
import io.mockk.mockk
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

class TestEventTarget : EventTarget {
    val mock = mockk<Consumer<Response>>()

    init {
        every { mock.accept(any()) } returns Unit
    }

    override fun handle(res: Response) {
        mock.accept(res)
        LOGGER.info("Event target for message ${res.body} reached")
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TestEventTarget::class.java)
    }
}

class TestEventTargetFactory : HandlerEventTargetFactory<Void> {
    override fun generateTarget(props: Void?): EventTarget {
        return TestEventTarget()
    }

    override fun getPropertyClass(): Class<Void> = Void::class.java
}
