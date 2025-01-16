package com.leonpatmore.fass.common.target

interface HandlerEventTargetFactory<PROPS> {
    fun generateTarget(props: PROPS?): EventTarget

    fun getPropertyClass(): Class<PROPS>
}
