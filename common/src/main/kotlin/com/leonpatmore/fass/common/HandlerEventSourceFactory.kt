package com.leonpatmore.fass.common

import org.springframework.context.support.GenericApplicationContext

interface HandlerEventSourceFactory {
    fun wrapHandler(
        handler: Handler<*>,
        context: GenericApplicationContext,
    )
}
