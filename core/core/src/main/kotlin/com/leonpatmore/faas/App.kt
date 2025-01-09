package com.leonpatmore.faas

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class App

@Configuration
@ComponentScan(
    basePackages = [
        "com.leonpatmore.faas",
    ],
)
class ExternalConfiguration

fun main(args: Array<String>) {
    runApplication<App>(*args)
}
