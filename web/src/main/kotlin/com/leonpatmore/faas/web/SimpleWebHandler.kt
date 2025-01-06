package com.leonpatmore.faas.web

import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.Message
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

class SimpleWebHandler(private val handler: Handler<*>) {
    fun handle(
        @RequestBody body: String,
    ): ResponseEntity<String> {
        println("Received $body")
        val typedHandler = handler as Handler<String>
        val res = typedHandler.handle(Message(body))
        return ResponseEntity.ok(res.body as String)
    }
}
