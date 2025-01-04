package com.leonpatmore.faas

import com.leonpatmore.faas.web.Controller
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

//@SpringBootApplication(scanBasePackages = ["com.leonpatmore.fass"])
@ComponentScan(basePackages = ["com.leonpatmore.fass", "com.leonpatmore.faas.example"],
	basePackageClasses = [Controller::class])
@SpringBootApplication
class App

fun main(args: Array<String>) {
	runApplication<App>(*args)
}
