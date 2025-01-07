package com.leonpatmore.faas.web

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

@Component
@ConditionalOnProperty(EVENT_SOURCE_ENABLED_PROPERTY_PREFIX + "web.enabled")
class WebEventSourceFactory(
    private val handlerMapping: RequestMappingHandlerMapping,
) : HandlerEventSourceFactory<WebProperties> {
    override fun wrapHandler(
        handler: Handler<*>,
        context: GenericApplicationContext,
        properties: WebProperties,
    ) {
        val mappingInfo = RequestMappingInfo.paths(properties.path).methods(RequestMethod.POST).build()
        val method: Method = SimpleWebHandler::class.java.getMethod("handle", String::class.java)
        val simpleWebHandler = SimpleWebHandler(handler)
        handlerMapping.registerMapping(mappingInfo, simpleWebHandler, method)
    }

    override fun getPropertyClass(): Class<WebProperties> = WebProperties::class.java
}
