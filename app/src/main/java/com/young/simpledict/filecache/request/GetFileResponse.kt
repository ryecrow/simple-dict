package com.young.simpledict.filecache.request

import com.young.simpledict.service.event.BaseEvent

import java.io.File

/**
 * Author: landerlyang
 * Date:   2014-10-29
 * Time:   12:02
 * Life with passion. Code with creativity!
 */
class GetFileResponse(eventCode: Any, val resultFile: File?) : BaseEvent(eventCode)
