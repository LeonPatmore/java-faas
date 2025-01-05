package com.leonpatmore.faas

import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.NamedHandler
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component

@Component
class DynamicBeanInitializer : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val context = event.applicationContext as GenericApplicationContext
        val handlers: Collection<Handler<*>> = context.getBeansOfType(Handler::class.java).values
        handlers.forEach {
            val factory = context.getBean("testEventSourceFactory", HandlerEventSourceFactory::class.java)
            factory.wrapHandler(it, context)
        }
    }
}
