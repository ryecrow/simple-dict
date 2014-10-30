package com.young.simpledict.service;

import com.young.simpledict.service.event.DownloadBlobRequest;
import com.young.simpledict.service.event.OnApplicationTerminateEvent;
import com.young.simpledict.service.event.SearchWordRequest;
import com.young.simpledict.service.task.BaseTask;
import com.young.simpledict.service.task.DownloadBlobTask;
import com.young.simpledict.service.task.SearchWordTask;
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
    public static final int MAX_TASK_RETRY_TIMES = 2;
    private ExecutorService mThreadPool;

    public HttpRequestService() {
        mThreadPool = Executors.newFixedThreadPool(2);
        EventBus.getDefault().register(this);
    }

    public boolean retryTask(BaseTask<?> task) {
        if (task.increaseRetryCount() <= MAX_TASK_RETRY_TIMES) {
            submitTask(task);
            return true;
        } else {
            task.onTaskFail();
            return false;
        }
    }

    public boolean submitTask(BaseTask<?> task) {
        //TODO add some future related stuff
        mThreadPool.submit(task);
        return true;
    }

    private boolean submitTask(Class<? extends BaseTask<?>> taskClazz, Object... params) {
        BaseTask<?> task = null;
        try {
            Class<?>[] paramType = new Class<?>[params.length];
            for (int i = 0; i < params.length; i++) {
                paramType[i] = params[i].getClass();
            }
            task = taskClazz.getConstructor(paramType).newInstance(params);
            return submitTask(task);
        } catch (Exception e) {
            return false;
        }
    }

    public void onEvent(SearchWordRequest request) {
        submitTask(SearchWordTask.class, request);
    }

    public void onEvent(DownloadBlobRequest request) {
        submitTask(DownloadBlobTask.class, request);
    }

    public void onEvent(OnApplicationTerminateEvent e) {
        EventBus.getDefault().unregister(this);
        mThreadPool.shutdownNow();
    }
}
