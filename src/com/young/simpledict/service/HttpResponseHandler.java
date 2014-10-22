package com.young.simpledict.service;

import com.young.common.YLog;
import com.young.simpledict.dict.DictAdapter;
import com.young.simpledict.dict.model.DictDetail;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   17:26
 * Life with passion. Code with creativity!
 */
public class HttpResponseHandler implements ResponseHandler<DictDetail> {
    private static final String TAG = "HttpResponseHandler";

    private DictAdapter mAdapter;

    public HttpResponseHandler(DictAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public DictDetail handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
        DictDetail d;
        int status = httpResponse.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                d = mAdapter.parseDict(EntityUtils.toString(entity));
                return d;
            }
        } else {
            YLog.i(TAG, "Unexpected response status: " + status);
        }
        return null;
    }
}
