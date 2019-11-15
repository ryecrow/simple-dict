package com.young.simpledict.service.event

import java.io.File

/**
 * Author: taylorcyang
 * Date:   2014-10-28
 * Time:   20:53
 * Life with passion. Code with creativity!
 */
class DownloadBlobResponse(eventCode: Any, var resultFile: File?) : BaseEvent(eventCode)