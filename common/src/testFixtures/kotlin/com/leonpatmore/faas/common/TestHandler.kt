package com.leonpatmore.faas.common

import com.leonpatmore.fass.common.Message
import com.leonpatmore.fass.common.NamedHandler
import com.leonpatmore.fass.common.Response

class TestHandler : NamedHandler<String>("Test handler") {
    override fun handle(msg: Message<String>): Response {
        println("Handling " + msg.body)
        return Response(RESPONSE_STRING)
    }

    companion object {
        const val RESPONSE_STRING = "res"
    }

    override fun getMessageType(): Class<String> = String::class.java
}
