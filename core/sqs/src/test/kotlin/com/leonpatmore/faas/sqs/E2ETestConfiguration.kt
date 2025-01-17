package com.leonpatmore.faas.sqs

import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import software.amazon.awssdk.services.sqs.SqsAsyncClient

@TestConfiguration
class E2ETestConfiguration {
    @Bean
    fun sqsTemplate(sqsAsyncClient: SqsAsyncClient): SqsTemplate {
        return SqsTemplate.builder()
            .sqsAsyncClient(sqsAsyncClient)
            .configureDefaultConverter {
                it.doNotSendPayloadTypeHeader()
            }
            .build()
    }
}

@SpringBootApplication
class TestApp
