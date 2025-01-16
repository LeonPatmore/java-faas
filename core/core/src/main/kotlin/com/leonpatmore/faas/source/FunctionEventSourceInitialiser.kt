package com.leonpatmore.faas.source

import com.fasterxml.jackson.databind.ObjectMapper
import com.leonpatmore.faas.FunctionInitialiser
import com.leonpatmore.faas.FunctionSourceProperties
import com.leonpatmore.faas.RootFunctionProperties
import com.leonpatmore.faas.SpringUtils.Companion.exit
import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.source.FunctionSourceData
import com.leonpatmore.fass.common.source.HandlerEventSourceFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component

@Component
class FunctionEventSourceInitialiser(
    val objectMapper: ObjectMapper,
    val factories: Map<String, HandlerEventSourceFactory<*>>,
) : FunctionInitialiser {
    override fun initialseFunction(
        functionName: String,
        handler: Handler<*>,
        props: RootFunctionProperties?,
        context: GenericApplicationContext,
    ) {
        val sourcePropsMap = props?.source?.props ?: emptyMap()
        LOGGER.info("Setting up event source for function {} with custom props {}", functionName, sourcePropsMap)
        val factory = factories.getEventSourceFactory(props?.source, context)
        wrapWithSource(factory, functionName, handler, sourcePropsMap, context)
    }

    fun <T> wrapWithSource(
        factory: HandlerEventSourceFactory<T>,
        functionName: String,
        handler: Handler<*>,
        sourcePropsMap: Map<String, Any>,
        context: GenericApplicationContext,
    ) {
        val sourceProps = objectMapper.convertValue(sourcePropsMap, factory.getPropertyClass())
        LOGGER.info("Using source properties [ $sourceProps ]")
        factory.wrapHandler(FunctionSourceData(functionName, handler, context, sourceProps))
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(FunctionEventSourceInitialiser::class.java)
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
