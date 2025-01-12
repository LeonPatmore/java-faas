package com.leonpatmore.faas.target

import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.Message
import com.leonpatmore.fass.common.Response
import com.leonpatmore.fass.common.target.EventTarget
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

@Component
class HandlerBeanFactory : BeanPostProcessor, ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext

    override fun postProcessBeforeInitialization(
        bean: Any,
        beanName: String,
    ): Any {
        if (bean is Handler<*>) {
            println("Post processing $beanName")
            return Proxy.newProxyInstance(
                applicationContext.classLoader,
                arrayOf(Handler::class.java),
                EventTargetProxy(bean),
            )
        }
        return bean
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}

class EventTargetProxy<T>(
    private val handler: Handler<T>,
) : InvocationHandler {
    val targets: MutableList<EventTarget> = mutableListOf()

    @Throws(Throwable::class)
    override fun invoke(
        proxy: Any?,
        method: Method,
        args: Array<Any?>,
    ): Any {
        println("Before method: " + method.getName())
        val message = args[0] as Message<T>
        val res = method.invoke(handler, message) as Response
        println("After method: " + method.getName())
        targets.forEach { it.handle(res) }
        return res
    }
}
