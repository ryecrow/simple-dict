package com.young.simpledict.service.event

import com.young.simpledict.dict.model.DictDetail

/**
 * Author: landerlyang
 * Date:   2014-10-21
 * Time:   16:35
 * Life with passion. Code with creativity!
 */
class SearchWordResponse(eventCode: Any, var dictDetail: DictDetail?) : BaseEvent(eventCode)