package com.leonpatmore.fass.common

import org.springframework.context.support.GenericApplicationContext

interface HandlerEventSourceFactory<PROPS> {
    fun wrapHandler(data: FunctionSourceData<PROPS>)

    fun getPropertyClass(): Class<PROPS>
}

data class FunctionSourceData<PROPS>(
    val functionName: String,
    val handler: Handler<*>,
    val context: GenericApplicationContext,
    val properties: PROPS,
)

const val EVENT_SOURCE_ENABLED_PROPERTY_PREFIX = "event.source."
