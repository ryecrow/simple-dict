package com.young.simpledict.service.task;

import android.util.Log;

import com.young.simpledict.dict.DictAdapter;
import com.young.simpledict.dict.YoudaoBriefDictAdapter;
import com.young.simpledict.dict.YoudaoDetailDictAdapter;
import com.young.simpledict.dict.model.DictDetail;
import com.young.simpledict.service.event.SearchWordRequest;
import com.young.simpledict.service.event.SearchWordResponse;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   17:10
 * Life with passion. Code with creativity!
 */
public class SearchWordTask extends BaseTask<DictDetail> {
    private static final String TAG = "HttpRequestTask";

    private Object requestEventCode;
    private String mWordToSearch;
    private DictAdapter mDictAdapter;

    public SearchWordTask(SearchWordRequest req) {
        this(req, req.useDict);
    }

    public SearchWordTask(SearchWordRequest req, int useDict) {
        if (req.word == null) {
            throw new IllegalArgumentException("word can't be null");
        }
        mWordToSearch = req.word;
        requestEventCode = req.getEventCode();
        switch (useDict) {
            case DictAdapter.DICT_YOUDAO_DETAIL:
                mDictAdapter = new YoudaoDetailDictAdapter();
                break;
            default:
                mDictAdapter = new YoudaoBriefDictAdapter();
        }
    }

    @Override
    public DictDetail doTheJob() {

        OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier((hostname, session) -> true)
                .build();
        Request getFileRequest = new Request.Builder()
                .get()
                .url(mDictAdapter.getSearchUri(mWordToSearch))
                .build();
        Response response;
        try {
            response = client.newCall(getFileRequest).execute();
        } catch (IOException e) {
            Log.i(TAG, "Failed to execute request.", e);
            return null;
        }
        ResponseBody body = response.body();
        if (!response.isSuccessful() || (body == null)) {
            Log.i(TAG, "Unexpected response status: " + response.code());
            return null;
        }
        try {
            DictDetail detail = mDictAdapter.parseDict(body.string());
            SearchWordResponse r = new SearchWordResponse();
            r.dictDetail = detail;
            r.setEventCode(requestEventCode);
            EventBus.getDefault().post(r);
            return detail;
        } catch (IOException e) {
            Log.i(TAG, "Failed to parse detail message from response.", e);
        }
        return null;
    }

    @Override
    public void onTaskFail() {
        SearchWordResponse r = new SearchWordResponse();
        r.setEventCode(requestEventCode);
        r.dictDetail = null;
        EventBus.getDefault().post(r);
    }
}
