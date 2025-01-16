package com.leonpatmore.faas

import com.leonpatmore.faas.SpringUtils.Companion.exit
import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.source.HandlerEventSourceFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component

@Component
class FunctionInitialiserListener(
    val properties: CoreProperties,
    val validators: List<HandlersPropertiesValidator>,
    val initialisers: List<FunctionInitialiser>,
) : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val context = event.applicationContext as GenericApplicationContext
        val handlers: Map<String, Handler<*>> = context.getBeansOfType(Handler::class.java)
        val factories = context.getBeansOfType(HandlerEventSourceFactory::class.java)

        validators.forEach {
            val valid = it.validate(handlers, properties, factories)
            if (!valid) {
                exit("Failed to validate functions: ${it.reason}", context)
            }
        }

        val functionConfigs =
            properties.functions.mapValues {
                val handler = it.value.handler
                if (!handlers.containsKey(handler)) {
                    exit("Handler [ $handler ] not found for function [ ${it.key} ]", context)
                }
                Pair(handlers[handler], it.value as RootFunctionProperties)
            } + if (properties.functions.isEmpty()) mapOf("root" to Pair(handlers.values.single(), properties.root)) else emptyMap()

        functionConfigs.forEach { (functionName, props) ->
            initialisers.forEach { it.initialseFunction(functionName, props.first!!, props.second, context) }
        }
    }
}

interface FunctionInitialiser {
    fun initialseFunction(
        functionName: String,
        handler: Handler<*>,
        props: RootFunctionProperties?,
        context: GenericApplicationContext,
    )
}

abstract class HandlersPropertiesValidator(val reason: String) {
    abstract fun validate(
        handlers: Map<String, Handler<*>>,
        properties: CoreProperties,
        factories: Map<String, HandlerEventSourceFactory<*>>,
    ): Boolean
}
