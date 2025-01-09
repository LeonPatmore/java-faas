package com.leonpatmore.faas.source

import com.fasterxml.jackson.databind.ObjectMapper
import com.leonpatmore.faas.CoreProperties
import com.leonpatmore.faas.FunctionSourceProperties
import com.leonpatmore.faas.RootFunctionProperties
import com.leonpatmore.faas.SpringUtils.Companion.exit
import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.HandlerEventSourceFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component

@Component
class FunctionEventSourceListener(
    val properties: CoreProperties,
    val validators: List<HandlersPropertiesValidator>,
    val objectMapper: ObjectMapper,
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
                Pair(handlers[handler], it.value as RootFunctionProperties)
            } + if (properties.functions.isEmpty()) mapOf("root" to Pair(handlers.values.single(), properties.root)) else emptyMap()

        functionConfigs.forEach { (functionName, props) ->
            LOGGER.info("Setting up event source for function {}", functionName)
            val factory = factories.getEventSourceFactory(props.second?.source, context)
            doSomething(factory, props.first!!, props.second, context)
        }
    }

    fun <T> doSomething(
        factory: HandlerEventSourceFactory<T>,
        handler: Handler<*>,
        functionProps: RootFunctionProperties?,
        context: GenericApplicationContext,
    ) {
        val sourceProps = objectMapper.convertValue(functionProps?.source?.props ?: emptyMap<String, Any>(), factory.getPropertyClass())
        LOGGER.info("Using source properties [ $sourceProps ]")
        factory.wrapHandler(handler, context, sourceProps)
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(FunctionEventSourceListener::class.java)
    }
}

fun Map<String, HandlerEventSourceFactory<*>>.getEventSourceFactory(
    props: FunctionSourceProperties?,
    context: ApplicationContext,
): HandlerEventSourceFactory<*> {
    if (props?.factory == null) {
        if (size != 1) {
            exit("More than one event source factory detected, you need to specify which one to use. The factories are [ $keys ]", context)
        } else {
            return values.single()
        }
    }
    if (containsKey(props.factory)) {
        return get(props.factory)!!
    } else {
        exit("Event source factory [ ${props.factory} ] does not exist", context)
    }
}

abstract class HandlersPropertiesValidator(val reason: String) {
    abstract fun validate(
        handlers: Map<String, Handler<*>>,
        properties: CoreProperties,
        factories: Map<String, HandlerEventSourceFactory<*>>,
    ): Boolean
}
