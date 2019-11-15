package com.young.simpledict.dict

import android.util.Log
import com.young.simpledict.dict.model.AudioSentence
import com.young.simpledict.dict.model.DictDetail
import com.young.simpledict.dict.model.TranslateSentence
import com.young.simpledict.dict.model.Wiki
import org.json.JSONException
import org.json.JSONObject

/**
 * Author: landerlyang
 * Date:   2014-10-22
 * Time:   15:41
 * Life with passion. Code with creativity!
 */
class YoudaoDetailDictAdapter : YoudaoBriefDictAdapter() {

    override fun getSearchUri(mWordToSearch: String): String {
        return "http://dict.youdao.com/jsonapi?client=mobile&q=$mWordToSearch&dicts=%7B%22dicts%22%3A%5B%5B%22ec%22%2C%22ce%22%2C%22cj%22%2C%22jc%22%2C%22kc%22%2C%22ck%22%2C%22fc%22%2C%22cf%22%5D%2C%5B%22baike%22%5D%2C%5B%22media_sents_part%22%5D%2C%5B%22pic_dict%22%5D%2C%5B%22hh%22%5D%2C%5B%22phrs%22%5D%2C%5B%22typos%22%5D%2C%5B%22wordform%22%5D%2C%5B%22auth_sents_part%22%5D%2C%5B%22rel_word%22%5D%2C%5B%22ce_new%22%5D%2C%5B%22blng_sents_part%22%5D%2C%5B%22syno%22%5D%2C%5B%22web_trans%22%5D%2C%5B%22fanyi%22%5D%2C%5B%22ec21%22%5D%2C%5B%22ee%22%5D%2C%5B%22special%22%5D%2C%5B%22collins%22%5D%5D%2C%22count%22%3A99%7D&keyfrom=mdict.5.3.1.androidvendor=youdaoweb&abtest=1&xmlVersion=5.1"
    }

    override fun parseDict(response: String): DictDetail {
        val d = DictDetail()
        val dictsToParse = arrayOf("ec", "ec21", "ee", "ce_new")
        try {
            val json = JSONObject(response)
            parseHeader(json.getJSONObject("simple"), d)
            parseDicts(json, d, dictsToParse)
            try {
                parseAudioSentences(json.getJSONObject("media_sents_part"), d)
            } catch (e: JSONException) {

            }

            try {
                parseTranslateSentences(json.getJSONObject("blng_sents_part"), d)
            } catch (e: JSONException) {

            }

            parseBaike(json, d)
        } catch (e: JSONException) {
            Log.i(TAG, "parse response json failed", e)
        }

        return d
    }

    private fun parseAudioSentences(anode: JSONObject, d: DictDetail) {
        val sent = anode.getJSONArray("sent")
        val asList: ArrayList<AudioSentence> = ArrayList(sent.length())
        for (i in 0 until sent.length()) {
            val n = sent.getJSONObject(i)
            val snippets = n.getJSONObject("snippets")
            val snippet = snippets.getJSONArray("snippet").getJSONObject(0)
            val sentence = AudioSentence(
                n.getString("eng"),
                snippet.getString("streamUrl"),
                n.getString("speech-size")
            )
            asList.add(sentence)
        }
        d.audioSentences = asList
    }

    @Throws(JSONException::class)
    private fun parseTranslateSentences(tsnode: JSONObject, d: DictDetail) {
        val sentencePair = tsnode.getJSONArray("sentence-pair")
        val tsList: ArrayList<TranslateSentence> = ArrayList(sentencePair.length())
        for (i in 0 until sentencePair.length()) {
            val pair = sentencePair.getJSONObject(i)
            val ts = TranslateSentence(
                pair.getString("sentence"),
                pair.getString("sentence-translation"), ""
            )
            if (pair.has("source")) ts.source = pair.getString("source")
            tsList.add(ts)
        }
        d.translateSentences = tsList
    }

    private fun parseBaike(json: JSONObject?, d: DictDetail?) {
        val obj = json!!.getJSONObject("baike")
        val source = obj.getJSONObject("source")
        val summarys = obj.getJSONArray("summarys")
        val summaries: ArrayList<String> = ArrayList(summarys.length())
        for (i in 0 until summarys.length()) {
            summaries.add(summarys.getJSONObject(i).getString("summary"))
        }
        val b = Wiki(source.getString("name"), source.getString("url"), summaries)
        d?.wiki = b
    }

    companion object {
        private val TAG: String? = YoudaoDetailDictAdapter::class.simpleName
    }
}
