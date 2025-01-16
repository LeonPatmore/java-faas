package com.leonpatmore.faas.target

import com.leonpatmore.faas.FunctionInitialiser
import com.leonpatmore.faas.RootFunctionProperties
import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.target.EventTarget
import com.leonpatmore.fass.common.target.HandlerEventTargetFactory
import org.apache.logging.log4j.util.Supplier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component
import java.lang.reflect.Proxy

@Component
class FunctionEventTargetInitialiser : FunctionInitialiser {
    override fun initialseFunction(
        functionName: String,
        handler: Handler<*>,
        props: RootFunctionProperties?,
        context: GenericApplicationContext,
    ) {
        if (props?.target?.factory == null) {
            return
        }
        val targetFactory = props.target.factory
        LOGGER.info("Adding target [ $targetFactory ] to function [ $functionName ]")
        val factory = context.getBean(targetFactory, HandlerEventTargetFactory::class.java)
        val target = factory.generateTarget()
        (Proxy.getInvocationHandler(handler as Proxy) as EventTargetProxy<*>).targets.add(target)
        context.registerBean("${functionName}EventTarget", EventTarget::class.java, Supplier { target })
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(FunctionEventTargetInitialiser::class.java)
    }
}
