package com.leonpatmore.faas.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.Message
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

class SimpleWebHandler<T>(
    private val handler: Handler<T>,
    private val objectMapper: ObjectMapper,
) {
    fun handle(
        @RequestBody body: String,
    ): ResponseEntity<String> {
        val messageBody: T =
            if (handler.getMessageType() == String::class.java) {
                body as T
            } else {
                objectMapper.readValue(body, handler.getMessageType())
            }
        val res = handler.handle(Message(messageBody))
        return ResponseEntity.ok(res.body as String)
    }
}
