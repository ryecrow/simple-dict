package com.young.simpledict.service.event

/**
 * Author: landerlyang
 * Date:   2014-10-21
 * Time:   15:20
 * Life with passion. Code with creativity!
 */
class SearchWordRequest(eventCode: Any, var word: String, var dictToUse: Int) : BaseEvent(eventCode)
