package com.leonpatmore.faas.sqs.target

import com.leonpatmore.fass.common.target.EventTarget
import com.leonpatmore.fass.common.target.HandlerEventTargetFactory
import org.springframework.stereotype.Component

@Component
class SqsEventTargetFactory : HandlerEventTargetFactory<SqsTargetProperties> {
    override fun generateTarget(props: SqsTargetProperties?): EventTarget {
        TODO("Not yet implemented")
    }

    override fun getPropertyClass(): Class<SqsTargetProperties> {
        TODO("Not yet implemented")
    }
}
