package com.young.simpledict.dict

import android.net.Uri
import android.util.Log
import com.young.common.NameValuePair

import com.young.simpledict.dict.model.DictDetail
import com.young.simpledict.dict.model.DictExplain

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Author: landerlyang
 * Date:   2014-10-21
 * Time:   19:02
 * Life with passion. Code with creativity!
 */
open class YoudaoBriefDictAdapter : DictAdapter {

    override fun getSearchUri(mWordToSearch: String): String {
        return "http://dict.youdao.com/jsonapi?client=mobile&q=${Uri.encode(mWordToSearch)}&dicts=%7B%22dicts%22%3A%5B%5B%22ec%22%2C%22ce%22%2C%22cj%22%2C%22jc%22%2C%22kc%22%2C%22ck%22%2C%22fc%22%2C%22cf%22%2C%22media_sents_part%22%2C%22pic_dict%22%2C%22auth_sents_part%22%2C%22rel_word%22%2C%22syno%22%2C%22ec21%22%2C%22ee%22%2C%22special%22%5D%2C%5B%22hh%22%5D%2C%5B%22typos%22%5D%2C%5B%22wordform%22%5D%2C%5B%22ce_new%22%5D%2C%5B%22fanyi%22%5D%5D%2C%22count%22%3A99%7D&keyfrom=mdict.5.3.1.android&vendor=youdaoweb&abtest=1&xmlVersion=5.1"
    }

    override fun parseDict(response: String): DictDetail {
        val d = DictDetail()
        try {
            val json = JSONObject(response)
            parseHeader(json.getJSONObject("simple"), d)
            if (json.has("ec")) {
                parseDict(json.getJSONObject("ec"), d)
            }
            parseDict(json, d)
        } catch (e: JSONException) {
            Log.v(TAG, "parse response json failed", e)
        }

        return d
    }

    protected fun parseHeader(simple: JSONObject?, d: DictDetail) {
        if (simple != null) {
            try {
                d.word = simple.getString("query")
                val word = simple.getJSONArray("word")
                for (i in 0 until word.length()) {
                    val o = word.getJSONObject(i)
                    if (o.has("phone")) d.pinyin = o.getString("phone")
                    if (o.has("usphone")) d.usphonetic = o.getString("usphone")
                    if (o.has("ukphone")) d.ukphonetic = o.getString("ukphone")
                    if (o.has("usspeech"))
                        d.usspeech =
                            "http://dict.youdao.com/dictvoice?audio=${o.getString("usspeech")}"
                    if (o.has("ukspeech"))
                        d.ukspeech =
                            "http://dict.youdao.com/dictvoice?audio=${o.getString("ukspeech")}"
                }
            } catch (e: Exception) {
                Log.v(TAG, "parse header failed", e)
            }

        }
    }

    private fun parseDict(dict: JSONObject?, d: DictDetail) {
        if (dict == null) {
            return
        }
        try {
            var dictName: String? = null
            //try to get dictName
            if (dict.has("source")) {
                val jo = dict.getJSONObject("source")
                if (jo.has("name")) dictName = jo.getString("name")
            }

            val de = DictExplain(dictName ?: "")
            d.explains.add(de)

            var arr: JSONArray
            try {
                arr = dict.getJSONArray("word")
            } catch (e: JSONException) {
                arr = JSONArray()
                arr.put(dict.getJSONObject("word"))
            }

            for (j in 0 until arr.length()) {
                val o = arr.getJSONObject(0)
                val trs = o.getJSONArray("trs")
                for (i in 0 until trs.length()) {
                    try {
                        de.trs.add(
                            trs.getJSONObject(i)
                                .getJSONArray("tr").getJSONObject(0)
                                .getJSONObject("l")
                                .getJSONArray("i").getString(0)
                        )
                    } catch (e: JSONException) {

                    }

                }

                val wfs = o.getJSONArray("wfs")
                for (i in 0 until wfs.length()) {
                    try {
                        val wf = wfs.getJSONObject(i)
                            .getJSONObject("wf")
                        de.wfs.add(
                            NameValuePair(
                                wf.getString("name"),
                                wf.getString("value")
                            )
                        )

                    } catch (e: JSONException) {

                    }

                }
            }
        } catch (e: Exception) {
            Log.v(TAG, "Parse dictionary failed", e)
        }
    }

    protected fun parseDicts(json: JSONObject, d: DictDetail, dicts: Array<String>) {
        for (s in dicts) {
            try {
                if (json.has(s)) {
                    parseDict(json.getJSONObject(s), d)
                }
            } catch (e: JSONException) {

            }

        }
    }

    companion object {
        private val TAG: String? = YoudaoBriefDictAdapter::class.simpleName
    }
}
