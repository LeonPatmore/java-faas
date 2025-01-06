package com.leonpatmore.faas

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext

class SpringUtils {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(SpringUtils::class.java)

        fun exit(
            msg: String,
            context: ApplicationContext,
        ): Nothing {
            LOGGER.error(msg)
            SpringApplication.exit(context, { 30 })
            throw RuntimeException(msg)
        }
    }
}
