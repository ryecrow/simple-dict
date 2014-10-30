package com.young.simpledict.service.task;

import com.young.common.YLog;
import com.young.simpledict.GlobalContext;

import java.util.concurrent.Callable;

/**
 * Author: taylorcyang
 * Date:   2014-10-28
 * Time:   21:02
 * Life with passion. Code with creativity!
 */
public abstract class BaseTask<T> implements Callable<T> {
    private static final String TAG = "BaseTask";
    private int mRetryCount;

    public final int getRetryCount() {
        return mRetryCount;
    }

    public final int increaseRetryCount() {
        return ++mRetryCount;
    }

    public final T call() {
        T ret = null;
        try {
            ret = doTheJob();
        } catch (Exception e) {
            YLog.i(TAG, "execute failed", e);
            GlobalContext.getRequestService().retryTask(this);
        }
        return ret;
    }

    /**
     * do the real operation in this method, note the call method is {@code final},
     * so you can not override it.
     *
     * @return
     */
    public abstract T doTheJob();

    /**
     * callback called when task failed after times of retry!
     */
    public abstract void onTaskFail();

}
