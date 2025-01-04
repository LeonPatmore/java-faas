package com.leonpatmore.faas.web

import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.Message
import jakarta.annotation.PostConstruct
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(val handler: Handler<*>) {
//
//    @PostConstruct
//    fun hello() {
//        System.exit(1)
//    }

    @GetMapping("/test")
    fun test(): String {
        val typedHandler = handler as Handler<String>
        return typedHandler.handle(Message("some body")).body as String
    }
}
