package com.young.simpledict.service.task

import android.util.Log
import com.young.simpledict.service.event.DownloadBlobRequest
import com.young.simpledict.service.event.DownloadBlobResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.buffer
import okio.sink
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.IOException
import javax.net.ssl.HostnameVerifier

/**
 * Author: taylorcyang
 * Date:   2014-10-28
 * Time:   20:49
 * Life with passion. Code with creativity!
 */
class DownloadBlobTask(private val mRequest: DownloadBlobRequest) : BaseTask<File>() {

    override fun doTheJob(): File? {
        val client = OkHttpClient.Builder()
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .build()
        val getFileRequest = Request.Builder()
            .get()
            .url(mRequest.requestUrlAndFileToStore.first)
            .build()
        val response: Response
        try {
            response = client.newCall(getFileRequest).execute()
        } catch (e: IOException) {
            Log.i(TAG, "Failed to execute request.", e)
            return null
        }

        val body = response.body
        if (!response.isSuccessful || body == null) {
            Log.i(TAG, "Unexpected response status: " + response.code)
            return null
        }
        val fileToWrite = mRequest.requestUrlAndFileToStore.second
        try {
            fileToWrite.sink()
                .buffer()
                .use { sink -> sink.writeAll(body.source()) }
        } catch (e: IOException) {
            Log.i(TAG, "request failed", e)
            return null
        }

        val r = DownloadBlobResponse(mRequest.eventCode, fileToWrite)
        EventBus.getDefault().post(r)
        return fileToWrite
    }

    override fun onTaskFail() {
        val rep = DownloadBlobResponse(mRequest.eventCode, null)
        EventBus.getDefault().post(rep)
    }

    companion object {
        private val TAG: String? = DownloadBlobTask::class.simpleName
    }
}
