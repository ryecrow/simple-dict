package com.young.simpledict.filecache.service

import android.util.Pair

import com.young.simpledict.filecache.request.GetFileRequest
import com.young.simpledict.filecache.request.GetFileResponse
import com.young.simpledict.service.event.DownloadBlobRequest
import com.young.simpledict.service.event.DownloadBlobResponse

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.io.File

/**
 * Author: taylorcyang
 * Date:   2014-10-28
 * Time:   20:43
 * Life with passion. Code with creativity!
 */
class FileCache(private val cacheDir: String) {

    init {
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(req: GetFileRequest) {
        val filename = cacheDir + req.fileName
        val file = File(filename)
        if (file.exists()) {
            val resp = GetFileResponse(req.eventCode, file)
            resp.eventCode = req.eventCode
            EventBus.getDefault().post(resp)
        } else {
            val downloadRequest = DownloadBlobRequest(req.eventCode, Pair(req.fileUrl, file))
            EventBus.getDefault().post(downloadRequest)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(resp: DownloadBlobResponse) {
        val getFileResponse = GetFileResponse(resp.eventCode, resp.resultFile)
        EventBus.getDefault().post(getFileResponse)
    }
}
