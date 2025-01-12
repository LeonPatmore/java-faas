package com.leonpatmore.faas.common.target

import com.leonpatmore.fass.common.Response
import com.leonpatmore.fass.common.target.EventTarget
import com.leonpatmore.fass.common.target.HandlerEventTargetFactory
import io.mockk.every
import io.mockk.mockk
import java.util.function.Consumer

class TestEventTarget : EventTarget {
    val mock = mockk<Consumer<Response>>()

    init {
        every { mock.accept(any()) } returns Unit
    }

    override fun handle(res: Response) {
        mock.accept(res)
        println("Event target for message ${res.body} reached")
    }
}

class TestEventTargetFactory : HandlerEventTargetFactory {
    override fun generateTarget(): EventTarget {
        return TestEventTarget()
    }
}
