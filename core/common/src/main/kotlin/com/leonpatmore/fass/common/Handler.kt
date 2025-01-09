package com.leonpatmore.fass.common

data class Message<T>(val body: T)

data class Response(val body: Any)

interface Handler<T> {
    fun handle(msg: Message<T>): Response

    fun getMessageType(): Class<T>
}

abstract class NamedHandler<T>(val name: String) : Handler<T>
