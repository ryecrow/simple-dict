package com.young.simpledict

import android.app.Application

import com.young.simpledict.service.event.OnApplicationStartEvent
import com.young.simpledict.service.event.OnApplicationTerminateEvent

import org.greenrobot.eventbus.EventBus

/**
 * Author: landerlyang
 * Date:   2014-10-21
 * Time:   16:46
 * Life with passion. Code with creativity!
 */
class SimpleDictApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        GlobalContext.application = this
        initApplication()
        EventBus.getDefault().post(OnApplicationStartEvent(SimpleDictApplication::class))
    }

    private fun initApplication() {
        GlobalContext.init()
    }

    override fun onTerminate() {
        EventBus.getDefault().post(OnApplicationTerminateEvent(SimpleDictApplication::class))
        super.onTerminate()
    }
}
