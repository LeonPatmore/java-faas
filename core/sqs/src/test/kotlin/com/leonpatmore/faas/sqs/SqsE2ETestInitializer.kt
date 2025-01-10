package com.leonpatmore.faas.sqs

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName
import java.time.Duration

class SqsE2ETestInitializer :
    ApplicationContextInitializer<ConfigurableApplicationContext> {
    companion object {
        @JvmStatic
        @Container
        var localstack: LocalStackContainer =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:4.0.3"))
                .withServices(LocalStackContainer.Service.SQS)
                .withReuse(true)
                .withStartupTimeout(Duration.ofSeconds(10))
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        localstack.start()
        TestPropertyValues
            .of(
                mapOf(
                    "spring.cloud.aws.region.static" to localstack.region,
                    "spring.cloud.aws.credentials.access-key" to
                        localstack.accessKey,
                    "spring.cloud.aws.credentials.secret-key" to
                        localstack.secretKey,
                    "spring.cloud.aws.sqs.endpoint" to
                        localstack.getEndpointOverride(LocalStackContainer.Service.SQS).toString(),
                ),
            ).applyTo(applicationContext.environment)
    }
}
