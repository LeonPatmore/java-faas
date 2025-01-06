package com.leonpatmore.faas.web

import com.leonpatmore.faas.common.TestHandler
import com.leonpatmore.faas.common.TestHandler.Companion.RESPONSE_STRING
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(properties = ["web.path=/path"], classes = [TestConfig::class])
@AutoConfigureMockMvc
class E2ETests {
    @Autowired
    private lateinit var webEventSourceFactory: WebEventSourceFactory

    @Autowired
    private lateinit var testHandler: TestHandler

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun name() {
        webEventSourceFactory.wrapHandler(testHandler, applicationContext as GenericApplicationContext)

        mockMvc.perform(post("/path").content("hello"))
            .andExpect(status().isOk)
            .andExpect(content().string(RESPONSE_STRING))
    }
}

@TestConfiguration
class TestConfig {
    @Bean
    fun testHandler() = TestHandler()
}

@SpringBootApplication
class TestApp
