package com.leonpatmore.faas.common.target

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestEventTargetConfiguration {
    @Bean
    fun testEventTargetFactory() = TestEventTargetFactory()
}
