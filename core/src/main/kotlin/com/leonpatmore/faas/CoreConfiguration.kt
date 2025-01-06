package com.leonpatmore.faas

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(CoreProperties::class)
class CoreConfiguration
