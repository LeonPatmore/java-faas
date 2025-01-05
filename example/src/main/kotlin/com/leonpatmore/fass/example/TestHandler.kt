package com.leonpatmore.fass.example

import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.Message
import com.leonpatmore.fass.common.Response
import org.springframework.stereotype.Service

@Service
class TestHandler : Handler<String> {
    override fun handle(msg: Message<String>): Response {
        return Response("Hello there!")
    }
}
