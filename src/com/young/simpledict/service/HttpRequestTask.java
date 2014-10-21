package com.young.simpledict.service;

import com.young.common.YLog;
import com.young.simpledict.dict.DictAdapter;
import com.young.simpledict.dict.YoudaoDictAdapter;
import com.young.simpledict.service.event.SearchWordResponse;
import com.young.simpledict.service.model.DictDetail;
import de.greenrobot.event.EventBus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.concurrent.Callable;

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   17:10
 * Life with passion. Code with creativity!
 */
public class HttpRequestTask implements Callable<DictDetail> {
    private static final String TAG = "HttpRequestTask";
    public static final int DICT_YOUDAO = 0;

    private String mWordToSearch;
    private DictAdapter mDictAdapter;

    public HttpRequestTask(String wordToSearch) {
        this(wordToSearch, DICT_YOUDAO);
    }

    public HttpRequestTask(String wordToSearch, int useDict) {
        if (wordToSearch == null) {
            throw new IllegalArgumentException("word can't be null");
        }
        mWordToSearch = wordToSearch;
        switch (useDict) {
            default:
                mDictAdapter = new YoudaoDictAdapter();
        }

    }

    @Override
    public DictDetail call() throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(mDictAdapter.getSearchUri(mWordToSearch));
            DictDetail responseBody = httpclient.execute(httpget, new HttpResponseHandler(mDictAdapter));
            SearchWordResponse r = new SearchWordResponse();
            r.dictDetail = responseBody;
            EventBus.getDefault().post(r);
            return responseBody;
        } catch (Exception e) {
            YLog.i(TAG, "request failed", e);
            EventBus.getDefault().post((SearchWordResponse) null);
        }
        return null;
    }
}
