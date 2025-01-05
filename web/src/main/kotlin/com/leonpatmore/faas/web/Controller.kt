package com.leonpatmore.faas.web

import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.Message
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(val handler: Handler<*>) {
    @GetMapping("/test")
    fun test(): String {
        val typedHandler = handler as Handler<String>
        return typedHandler.handle(Message("some body")).body as String
    }
}
