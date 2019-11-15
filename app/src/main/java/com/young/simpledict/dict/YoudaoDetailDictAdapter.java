package com.young.simpledict.dict;

import android.util.Log;

import com.young.simpledict.dict.model.AudioSentence;
import com.young.simpledict.dict.model.DictDetail;
import com.young.simpledict.dict.model.TranslateSentence;
import com.young.simpledict.dict.model.Wiki;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author: taylorcyang
 * Date:   2014-10-22
 * Time:   15:41
 * Life with passion. Code with creativity!
 */
public class YoudaoDetailDictAdapter extends YoudaoBriefDictAdapter {
    private static final String TAG = "YoudaoDetailDictAdapter";

    @Override
    public String getSearchUri(String mWordToSearch) {
        return "http://dict.youdao.com/jsonapi?client=mobile&q="
                + mWordToSearch +
                "&dicts=%7B%22dicts%22%3A%5B%5B%22ec%22%2C%22ce%22%2C%22cj%22%2C%22jc%22%2C%22kc%22%2C%22ck%22%2C%22fc%22%2C%22cf%22%5D%2C%5B%22baike%22%5D%2C%5B%22media_sents_part%22%5D%2C%5B%22pic_dict%22%5D%2C%5B%22hh%22%5D%2C%5B%22phrs%22%5D%2C%5B%22typos%22%5D%2C%5B%22wordform%22%5D%2C%5B%22auth_sents_part%22%5D%2C%5B%22rel_word%22%5D%2C%5B%22ce_new%22%5D%2C%5B%22blng_sents_part%22%5D%2C%5B%22syno%22%5D%2C%5B%22web_trans%22%5D%2C%5B%22fanyi%22%5D%2C%5B%22ec21%22%5D%2C%5B%22ee%22%5D%2C%5B%22special%22%5D%2C%5B%22collins%22%5D%5D%2C%22count%22%3A99%7D&keyfrom=mdict.5.3.1.androidvendor=youdaoweb&abtest=1&xmlVersion=5.1";
    }

    @Override
    public DictDetail parseDict(String response) {
        DictDetail d = new DictDetail();
        String[] dictsToParse = {"ec", "ec21", "ee", "ce_new"};
        try {
            JSONObject json = new JSONObject(response);
            parseHeader(json.getJSONObject("simple"), d);
            parseDicts(json, d, dictsToParse);
            try {
                parseAudioSentences(json.getJSONObject("media_sents_part"), d);
            } catch (JSONException e) {

            }

            try {
                parseTranslateSentences(json.getJSONObject("blng_sents_part"), d);
            } catch (JSONException e) {

            }
            parseBaike(json, d);
        } catch (JSONException e) {
            Log.i(TAG, "parse response json failed", e);
        }
        return d;
    }

    private void parseAudioSentences(JSONObject anode, DictDetail d) throws JSONException {
        JSONArray sent = anode.getJSONArray("sent");
        for (int i = 0; i < sent.length(); i++) {
            try {
                AudioSentence as = new AudioSentence();
                JSONObject n = sent.getJSONObject(i);
                as.sentence = n.getString("eng");
                JSONObject snippets = n.getJSONObject("snippets");
                JSONObject snippet = snippets.getJSONArray("snippet").getJSONObject(0);
                as.sentenceAudioUrl = snippet.getString("streamUrl");
                as.audioLength = n.getString("speech-size");
                d.audioSentences.add(as);
            } catch (Exception e) {
            }
        }
    }

    private void parseTranslateSentences(JSONObject tsnode, DictDetail d) throws JSONException {
        JSONArray sentence_pair = tsnode.getJSONArray("sentence-pair");
        for (int i = 0; i < sentence_pair.length(); i++) {
            try {
                TranslateSentence ts = new TranslateSentence();
                JSONObject pair = sentence_pair.getJSONObject(i);
                ts.sentence = pair.getString("sentence");
                ts.translate = pair.getString("sentence-translation");
                if (pair.has("source")) ts.source = pair.getString("source");
                d.translateSentences.add(ts);
            } catch (Exception e) {

            }
        }
    }


    protected void parseBaike(JSONObject json, DictDetail d) {
        if (json == null || d == null) {
            return;
        }
        try {
            JSONObject obj = json.getJSONObject("baike");
            JSONObject source = obj.getJSONObject("source");
            Wiki b = new Wiki();
            b.sourceName = source.getString("name");
            b.sourceURL = source.getString("url");
            JSONArray summarys = obj.getJSONArray("summarys");
            for (int i = 0; i < summarys.length(); i++) {
                JSONObject sum = summarys.getJSONObject(i);
                b.summary.add(sum.getString("summary"));
            }
            d.wiki = b;
        } catch (JSONException e) {

        }

    }
}
