package com.young.simpledict.service.task

import android.util.Log

import com.young.simpledict.GlobalContext

import java.util.concurrent.Callable
import java.util.concurrent.atomic.AtomicInteger

/**
 * Author: taylorcyang
 * Date:   2014-10-28
 * Time:   21:02
 * Life with passion. Code with creativity!
 */
abstract class BaseTask<T> : Callable<T> {
    var retryCount: AtomicInteger = AtomicInteger(0)
        private set

    fun increaseRetryCount(): Int {
        return retryCount.incrementAndGet()
    }

    override fun call(): T? {
        try {
            return doTheJob()
        } catch (e: Exception) {
            Log.i(TAG, "execute failed", e)
            GlobalContext.requestService.retryTask(this)
        }
        return null
    }

    /**
     * do the real operation in this method, note the call method is `final`,
     * so you can not override it.
     *
     * @return
     */
    abstract fun doTheJob(): T?

    /**
     * callback called when task failed after times of retry!
     */
    abstract fun onTaskFail()

    companion object {
        private val TAG: String? = BaseTask::class.simpleName
    }
}
