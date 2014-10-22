package com.young.simpledict.service;

import com.young.simpledict.service.event.OnApplicationTerminateEvent;
import com.young.simpledict.service.event.SearchWordRequest;
import de.greenrobot.event.EventBus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   15:46
 * Life with passion. Code with creativity!
 */
public class HttpRequestService {
    private ExecutorService mThreadPool;

    public HttpRequestService() {
        mThreadPool = Executors.newFixedThreadPool(2);
        EventBus.getDefault().register(this);
    }

    public void onEvent(SearchWordRequest request) {
        mThreadPool.submit(new HttpRequestTask(request.word, request.useDict));
    }

    public void onEvent(OnApplicationTerminateEvent e) {
        EventBus.getDefault().unregister(this);
        mThreadPool.shutdownNow();
    }
}
