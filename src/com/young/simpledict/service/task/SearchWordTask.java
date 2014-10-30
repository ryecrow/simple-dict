package com.young.simpledict.service.task;

import com.young.common.YLog;
import com.young.simpledict.dict.DictAdapter;
import com.young.simpledict.dict.YoudaoBriefDictAdapter;
import com.young.simpledict.dict.YoudaoDetailDictAdapter;
import com.young.simpledict.dict.model.DictDetail;
import com.young.simpledict.service.event.SearchWordRequest;
import com.young.simpledict.service.event.SearchWordResponse;
import com.young.simpledict.service.httphandler.SearchWordResponseHandler;
import de.greenrobot.event.EventBus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(mDictAdapter.getSearchUri(mWordToSearch));
            DictDetail responseBody = httpclient.execute(httpget, new SearchWordResponseHandler(mDictAdapter));
            SearchWordResponse r = new SearchWordResponse();
            r.dictDetail = responseBody;
            r.setEventCode(requestEventCode);
            EventBus.getDefault().post(r);
            return responseBody;
        } catch (Exception e) {
            YLog.i(TAG, "request failed", e);
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
