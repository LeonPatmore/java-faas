package com.leonpatmore.fass.example

import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.Message
import com.leonpatmore.fass.common.Response
import org.springframework.stereotype.Service

data class MyMessage(val firstName: String, val lastName: String)

@Service
class TestHandler : Handler<MyMessage> {
    override fun handle(msg: Message<MyMessage>): Response {
        return Response("Hello ${msg.body.firstName} ${msg.body.lastName}!")
    }

    override fun getMessageType(): Class<MyMessage> = MyMessage::class.java
}
