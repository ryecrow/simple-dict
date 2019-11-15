package com.young.simpledict.netsoundplayer.request

import com.young.simpledict.service.event.BaseEvent

/**
 * Author: landerlyang
 * Date:   2014-10-29
 * Time:   12:12
 * Life with passion. Code with creativity!
 */
class NetSoundResponse(eventCode: Any, var responseState: Int) : BaseEvent(eventCode) {

    constructor() : this(BaseEvent.EMPTY_EVENT_CODE, 0)

    companion object {
        const val RESPONSE_PLAYING = 1
        const val RESPONSE_PAUSED = 2
        const val RESPONSE_COMPLETED = 3
        const val RESPONSE_FAILED = -1
    }
}
