package com.leonpatmore.faas

import com.leonpatmore.fass.common.Handler
import org.springframework.context.support.GenericApplicationContext

interface HandlerEventSourceFactory {
    fun wrapHandler(
        handler: Handler<*>,
        context: GenericApplicationContext,
    )
}
