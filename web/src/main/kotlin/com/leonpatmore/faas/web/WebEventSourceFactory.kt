package com.leonpatmore.faas.web

import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.HandlerEventSourceFactory
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method

@Component
class WebEventSourceFactory(
    private val handlerMapping: RequestMappingHandlerMapping,
    private val properties: Properties,
) : HandlerEventSourceFactory {
    override fun wrapHandler(
        handler: Handler<*>,
        context: GenericApplicationContext,
    ) {
        val mappingInfo = RequestMappingInfo.paths(properties.path).methods(RequestMethod.POST).build()
        val method: Method = SimpleWebHandler::class.java.getMethod("handle", String::class.java)
        val simpleWebHandler = SimpleWebHandler(handler)
        handlerMapping.registerMapping(mappingInfo, simpleWebHandler, method)
    }
}
