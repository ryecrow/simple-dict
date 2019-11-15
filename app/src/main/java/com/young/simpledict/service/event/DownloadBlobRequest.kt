package com.young.simpledict.service.event

import android.util.Pair

import java.io.File

/**
 * Author: landerlyang
 * Date:   2014-10-28
 * Time:   20:47
 * Life with passion. Code with creativity!
 */
class DownloadBlobRequest(eventCode: Any, var requestUrlAndFileToStore: Pair<String, File>) :
    BaseEvent(eventCode)