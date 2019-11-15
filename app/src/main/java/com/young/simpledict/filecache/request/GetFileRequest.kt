package com.young.simpledict.filecache.request

import com.young.simpledict.service.event.BaseEvent

/**
 * Author: landerlyang
 * Date:   2014-10-29
 * Time:   12:01
 * Life with passion. Code with creativity!
 */
class GetFileRequest(eventCode: Any, val fileName: String, val fileUrl: String) :
    BaseEvent(eventCode)