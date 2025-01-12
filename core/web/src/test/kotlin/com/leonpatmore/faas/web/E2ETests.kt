package com.leonpatmore.faas.web

import com.leonpatmore.faas.common.TestHandlerConfiguration
import com.leonpatmore.fass.common.Handler
import com.leonpatmore.fass.common.source.FunctionSourceData
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(properties = ["event.source.web.enabled=true"], classes = [TestHandlerConfiguration::class])
@AutoConfigureMockMvc
class E2ETests {
    @Autowired
    private lateinit var webEventSourceFactory: WebEventSourceFactory

    @Autowired
    private lateinit var testHandler: Handler<String>

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun name() {
        webEventSourceFactory.wrapHandler(
            FunctionSourceData("function", testHandler, applicationContext as GenericApplicationContext, WebProperties()),
        )

        mockMvc.perform(post("/api").content("hello"))
            .andExpect(status().isOk)
            .andExpect(content().string("res"))
    }
}

@SpringBootApplication
class TestApp
