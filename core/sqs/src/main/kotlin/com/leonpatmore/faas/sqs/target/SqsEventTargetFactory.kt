package com.leonpatmore.faas.sqs.target

import com.leonpatmore.fass.common.target.EVENT_TARGET_ENABLED_PROPERTY_PREFIX
import com.leonpatmore.fass.common.target.EventTarget
import com.leonpatmore.fass.common.target.HandlerEventTargetFactory
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(EVENT_TARGET_ENABLED_PROPERTY_PREFIX + "sqs.enabled")
class SqsEventTargetFactory(private val sqsTemplate: SqsTemplate) : HandlerEventTargetFactory<SqsTargetProperties> {
    override fun generateTarget(props: SqsTargetProperties?): EventTarget {
        return SqsEventTarget(sqsTemplate, props!!.queueName)
    }

    override fun getPropertyClass(): Class<SqsTargetProperties> = SqsTargetProperties::class.java
}
