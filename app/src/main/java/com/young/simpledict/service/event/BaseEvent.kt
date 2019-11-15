package com.young.simpledict.service.event

/**
 * Author: landerlyang
 * Date:   2014-10-21
 * Time:   17:40
 * Life with passion. Code with creativity!
 */
open class BaseEvent(var eventCode: Any) {

    fun setEventCode(code: Any): BaseEvent {
        this.eventCode = code
        return this
    }

    companion object {
        const val EMPTY_EVENT_CODE = -1
    }
}
