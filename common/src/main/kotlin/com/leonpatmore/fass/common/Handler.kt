package com.leonpatmore.fass.common

data class Message<T>(val body: T)

data class Response(val body: Any)

fun interface Handler <T> {
    fun handle(msg: Message<T>): Response
}

abstract class NamedHandler<T> (val name: String) : Handler<T>
