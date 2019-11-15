package com.young.simpledict.service.task

import android.util.Log
import com.young.simpledict.dict.DictAdapter
import com.young.simpledict.dict.YoudaoBriefDictAdapter
import com.young.simpledict.dict.YoudaoDetailDictAdapter
import com.young.simpledict.dict.model.DictDetail
import com.young.simpledict.service.event.SearchWordRequest
import com.young.simpledict.service.event.SearchWordResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import javax.net.ssl.HostnameVerifier

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   17:10
 * Life with passion. Code with creativity!
 */
class SearchWordTask constructor(req: SearchWordRequest, useDict: Int = req.dictToUse) :
    BaseTask<DictDetail>() {

    private val requestEventCode: Any
    private val mWordToSearch: String
    private var mDictAdapter: DictAdapter? = null

    init {
        requireNotNull(req.word) { "word can't be null" }
        mWordToSearch = req.word
        requestEventCode = req.eventCode
        mDictAdapter = when (useDict) {
            DictAdapter.DICT_YOUDAO_DETAIL -> YoudaoDetailDictAdapter()
            else -> YoudaoBriefDictAdapter()
        }
    }

    override fun doTheJob(): DictDetail? {

        val client = OkHttpClient.Builder()
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .build()
        val getFileRequest = Request.Builder()
            .get()
            .url(mDictAdapter!!.getSearchUri(mWordToSearch))
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
        try {
            val detail = mDictAdapter!!.parseDict(body.string())
            val r = SearchWordResponse(requestEventCode, detail)
            EventBus.getDefault().post(r)
            return detail
        } catch (e: IOException) {
            Log.i(TAG, "Failed to parse detail message from response.", e)
        }

        return null
    }

    override fun onTaskFail() {
        val r = SearchWordResponse(requestEventCode, null)
        EventBus.getDefault().post(r)
    }

    companion object {
        private val TAG: String? = SearchWordTask::class.simpleName
    }
}
