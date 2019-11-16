package com.young.simpledict.service

import com.young.simpledict.service.event.DownloadBlobRequest
import com.young.simpledict.service.event.OnApplicationTerminateEvent
import com.young.simpledict.service.event.SearchWordRequest
import com.young.simpledict.service.task.BaseTask
import com.young.simpledict.service.task.DownloadBlobTask
import com.young.simpledict.service.task.SearchWordTask

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * Author: landerlyang
 * Date:   2014-10-21
 * Time:   15:46
 * Life with passion. Code with creativity!
 */
class HttpRequestService {
    private val mThreadPool: ExecutorService

    init {
        mThreadPool = ThreadPoolExecutor(
            0, 2, 30,
            TimeUnit.SECONDS, LinkedBlockingQueue()
        )
        EventBus.getDefault().register(this)
    }

    fun retryTask(task: BaseTask<*>): Boolean {
        return if (task.increaseRetryCount() <= MAX_TASK_RETRY_TIMES) {
            submitTask(task)
        } else {
            task.onTaskFail()
            false
        }
    }

    private fun submitTask(task: BaseTask<*>): Boolean {
        //TODO add some future related stuff
        mThreadPool.submit(task)
        return true
    }

    private fun submitTask(taskClazz: KClass<out BaseTask<*>>, vararg params: Any): Boolean {
        val task: BaseTask<*>
        return try {
            val paramType = arrayOfNulls<Class<*>>(params.size)
            for (i in params.indices) {
                paramType[i] = params[i].javaClass
            }
            task = taskClazz.java.getConstructor(*paramType).newInstance(*params)
            submitTask(task)
        } catch (e: Exception) {
            false
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(request: SearchWordRequest) {
        submitTask(SearchWordTask::class, request)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(request: DownloadBlobRequest) {
        submitTask(DownloadBlobTask::class, request)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: OnApplicationTerminateEvent) {
        EventBus.getDefault().unregister(this)
        mThreadPool.shutdownNow()
    }

    companion object {
        const val MAX_TASK_RETRY_TIMES = 2
    }
}
