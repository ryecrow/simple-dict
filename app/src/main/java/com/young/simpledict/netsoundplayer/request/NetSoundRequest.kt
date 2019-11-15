package com.young.simpledict.netsoundplayer.request

import com.young.simpledict.service.event.BaseEvent

/**
 * Author: landerlyang
 * Date:   2014-10-29
 * Time:   12:11
 * Life with passion. Code with creativity!
 */
class NetSoundRequest(
    eventCode: Any, var soundUrl: String,
    var requestCode: Int,
    var releaseAllMediaPlayer: Boolean
) : BaseEvent(eventCode) {

    constructor(eventCode: Any, soundUrl: String) : this(eventCode, soundUrl, REQUEST_START, false)

    companion object {
        const val REQUEST_START = 1
        const val REQUEST_PAUSE = 2
        const val REQUEST_STOP = 3
        const val REQUEST_RESUME = 4
    }
}
