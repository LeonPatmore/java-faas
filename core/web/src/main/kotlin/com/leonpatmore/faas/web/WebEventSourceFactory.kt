package com.leonpatmore.faas.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.leonpatmore.fass.common.EVENT_SOURCE_ENABLED_PROPERTY_PREFIX
import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.HandlerEventSourceFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method

@ConditionalOnProperty(EVENT_SOURCE_ENABLED_PROPERTY_PREFIX + "web.enabled")
@Component
class WebEventSourceFactory(
    private val handlerMapping: RequestMappingHandlerMapping,
    private val objectMapper: ObjectMapper,
) : HandlerEventSourceFactory<WebProperties> {
    override fun wrapHandler(
        handler: Handler<*>,
        context: GenericApplicationContext,
        properties: WebProperties,
    ) {
        val mappingInfo = RequestMappingInfo.paths(properties.path).methods(RequestMethod.POST).build()
        val method: Method = SimpleWebHandler::class.java.methods.first { it.name == "handle" }
        val simpleWebHandler = SimpleWebHandler(handler, objectMapper)
        handlerMapping.registerMapping(mappingInfo, simpleWebHandler, method)
    }

    override fun getPropertyClass(): Class<WebProperties> = WebProperties::class.java
}
