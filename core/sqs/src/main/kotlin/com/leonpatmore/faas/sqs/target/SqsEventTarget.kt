package com.leonpatmore.faas.sqs.target

import com.leonpatmore.fass.common.Response
import com.leonpatmore.fass.common.target.EventTarget
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SqsEventTarget(private val sqsTemplate: SqsTemplate, private val queueName: String) : EventTarget {
    override fun handle(res: Response) {
        sqsTemplate.send {
            LOGGER.info("Sending [ ${res.body} ] to queue [ $queueName ]")
            it.queue(queueName).payload(res.body)
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(SqsEventTarget::class.java)
    }
}
