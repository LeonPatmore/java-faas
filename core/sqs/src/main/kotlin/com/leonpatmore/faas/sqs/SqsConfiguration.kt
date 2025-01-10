package com.leonpatmore.faas.sqs

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:sqs.properties")
class SqsConfiguration
