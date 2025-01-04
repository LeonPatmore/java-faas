package com.leonpatmore.faas

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JavaFaasApplication

fun main(args: Array<String>) {
	runApplication<JavaFaasApplication>(*args)
}
