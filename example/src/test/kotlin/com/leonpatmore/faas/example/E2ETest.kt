package com.leonpatmore.faas.example

import com.leonpatmore.fass.common.Message
import com.leonpatmore.fass.example.Configuration
import com.leonpatmore.fass.example.MyMessage
import com.leonpatmore.fass.example.TestHandler
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [Configuration::class])
class E2ETest {
    @Autowired
    private lateinit var testHandler: TestHandler

    @Test
    fun name() {
        testHandler.handle(Message(MyMessage("leon", "patmore")))
    }
}
