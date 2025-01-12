package com.leonpatmore.faas

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

open class RootFunctionProperties(
    val source: FunctionSourceProperties?,
    val target: FunctionTargetProperties?,
)

class FunctionProperties(
    val handler: String,
    source: FunctionSourceProperties = FunctionSourceProperties(),
    target: FunctionTargetProperties = FunctionTargetProperties(),
) : RootFunctionProperties(
        source,
        target,
    )

data class FunctionSourceProperties(val factory: String? = null, val props: Map<String, Any> = emptyMap())

data class FunctionTargetProperties(val factory: String? = null, val props: Map<String, Any> = emptyMap())

@ConfigurationProperties
@Validated
data class CoreProperties(
    val root: RootFunctionProperties? = null,
    val functions: Map<String, FunctionProperties> = emptyMap(),
)
