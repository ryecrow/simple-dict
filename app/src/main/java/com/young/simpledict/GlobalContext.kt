package com.young.simpledict

import android.app.Application
import android.content.Context
import com.young.common.Singleton
import com.young.simpledict.filecache.service.FileCache
import com.young.simpledict.netsoundplayer.service.NetSoundPlayer
import com.young.simpledict.service.HttpRequestService

import java.util.concurrent.atomic.AtomicReference

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   15:46
 * Life with passion. Code with creativity!
 */
object GlobalContext {
    private val sApplication = AtomicReference<Application>()

    var application: Application?
        get() = sApplication.get()
        set(a) {
            requireNotNull(a) { "parameter application can't be null" }
            check(sApplication.getAndSet(a) == null) { "Application can only be set once" }
        }

    val applicationContext: Context?
        get() {
            val app = application
            return app?.applicationContext
        }

    private val sHttpRequestService = object : Singleton<HttpRequestService>() {
        override fun init(): HttpRequestService {
            return HttpRequestService()
        }
    }

    val requestService: HttpRequestService
        get() = sHttpRequestService.get()

    private val sCacheDir = object : Singleton<String>() {
        override fun init(): String {
            val externalCachedir = application!!.externalCacheDir
            return if (externalCachedir == null) {
                application!!.cacheDir.absolutePath + '/'
            } else {
                externalCachedir.absolutePath + '/'
            }
        }
    }

    val cacheDir: String
        get() = sCacheDir.get()

    private val sFileCache = object : Singleton<FileCache>() {
        override fun init(): FileCache {
            return FileCache(cacheDir)
        }
    }

    val fileCache: FileCache
        get() = sFileCache.get()

    val sNetSoundPlayer: Singleton<NetSoundPlayer> = object : Singleton<NetSoundPlayer>() {
        override fun init(): NetSoundPlayer {
            return NetSoundPlayer()
        }
    }

    val netSoundPlayer: NetSoundPlayer
        get() = sNetSoundPlayer.get()

    fun init() {
        requestService
        fileCache
        netSoundPlayer
    }
}
