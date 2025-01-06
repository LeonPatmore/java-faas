package com.leonpatmore.faas.source

import com.leonpatmore.faas.CoreProperties
import com.leonpatmore.fass.common.Handler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Validators {
    @Bean
    fun atLeastOneHandler() =
        object : HandlersPropertiesValidator("at-least one handler must be present ") {
            override fun validate(
                handlers: Map<String, Handler<*>>,
                properties: CoreProperties,
            ): Boolean {
                return handlers.isNotEmpty()
            }
        }

    @Bean
    fun rootRequiresOneHandler() =
        object : HandlersPropertiesValidator("if using root, there must be exactly one handler") {
            override fun validate(
                handlers: Map<String, Handler<*>>,
                properties: CoreProperties,
            ): Boolean {
                return (properties.functions.isEmpty() && handlers.size == 1) || properties.functions.isNotEmpty()
            }
        }

    @Bean
    fun rootAndExplicitFunctionsCannotBeUsedTogether() =
        object : HandlersPropertiesValidator("root and explicit functions config can not be used together") {
            override fun validate(
                handlers: Map<String, Handler<*>>,
                properties: CoreProperties,
            ): Boolean {
                return !(properties.functions.isNotEmpty() && properties.root != null)
            }
        }
}
