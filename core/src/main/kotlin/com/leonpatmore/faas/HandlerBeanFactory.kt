package com.leonpatmore.faas

import com.leonpatmore.fass.common.Handler
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import java.lang.reflect.Proxy

@Component
class HandlerBeanFactory : BeanPostProcessor, ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (bean is Handler<*>) {
            println("Post processing $beanName")
            return Proxy.newProxyInstance(
                applicationContext.classLoader,
                arrayOf(Handler::class.java)
            ) { proxy, method, args ->
                println("Before method: " + method.getName())
                val result: Any = method.invoke(bean, args)
                println("After method: " + method.getName())
                result
            } as Handler<*>
        }
        return bean
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}
