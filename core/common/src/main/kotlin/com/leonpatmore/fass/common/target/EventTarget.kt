package com.leonpatmore.fass.common.target

import com.leonpatmore.fass.common.Response

interface EventTarget {
    fun handle(res: Response)
}
