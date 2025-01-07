package com.leonpatmore.fass.common

import org.springframework.context.support.GenericApplicationContext

interface HandlerEventSourceFactory<PROPS> {
    fun wrapHandler(
        handler: Handler<*>,
        context: GenericApplicationContext,
        properties: PROPS,
    )

    fun getPropertyClass(): Class<PROPS>
}

const val EVENT_SOURCE_ENABLED_PROPERTY_PREFIX = "event.source."
