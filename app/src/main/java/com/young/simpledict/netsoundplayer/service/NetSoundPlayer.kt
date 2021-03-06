package com.young.simpledict.netsoundplayer.service

import android.media.MediaPlayer
import android.util.Log

import com.young.simpledict.filecache.request.GetFileRequest
import com.young.simpledict.filecache.request.GetFileResponse
import com.young.simpledict.filecache.util.FileNameDigester
import com.young.simpledict.netsoundplayer.request.NetSoundRequest
import com.young.simpledict.netsoundplayer.request.NetSoundResponse

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.io.IOException

/**
 * Author: landerlyang
 * Date:   2014-10-29
 * Time:   12:09
 * Life with passion. Code with creativity!
 */
class NetSoundPlayer {
    private var mMediaPlayer: MediaPlayer? = null

    private val idleMediaPlayer: MediaPlayer
        get() {
            return try {
                var mediaPlayer = mMediaPlayer
                if (mediaPlayer != null) {
                    mediaPlayer.reset()
                } else {
                    mediaPlayer = MediaPlayer()
                    mMediaPlayer = mediaPlayer
                }
                mediaPlayer
            } catch (e: IllegalStateException) {
                val mediaPlayer = MediaPlayer()
                mMediaPlayer = mediaPlayer
                mediaPlayer
            }
        }

    init {
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(request: NetSoundRequest) {


        when {
            request.releaseAllMediaPlayer -> releaseMediaPlayer()
            request.requestCode == NetSoundRequest.REQUEST_START -> {
                val getFileRequest = GetFileRequest(
                    request.eventCode,
                    FileNameDigester.digest(request.soundUrl),
                    request.soundUrl
                )
                EventBus.getDefault().post(getFileRequest)
            }
            else -> {
                var retState = NetSoundResponse.RESPONSE_FAILED
                if (mMediaPlayer != null) {
                    try {
                        when (request.requestCode) {
                            NetSoundRequest.REQUEST_PAUSE -> {
                                mMediaPlayer!!.pause()
                                retState = NetSoundResponse.RESPONSE_PAUSED
                            }
                            NetSoundRequest.REQUEST_RESUME -> {
                                mMediaPlayer!!.start()
                                retState = NetSoundResponse.RESPONSE_PLAYING
                            }
                            NetSoundRequest.REQUEST_STOP -> {
                                mMediaPlayer!!.stop()
                                retState = NetSoundResponse.RESPONSE_COMPLETED
                            }
                        }
                    } catch (e: IllegalStateException) {
                        try {
                            //try to reset mediaplayer
                            mMediaPlayer!!.reset()
                        } catch (ex: IllegalStateException) {

                        }

                        //already initialized
                        //retState = NetSoundResponse.RESPONSE_FAILED;
                    }

                }
                val response = NetSoundResponse()
                response.setEventCode(request.eventCode)
                response.responseState = retState
                EventBus.getDefault().post(response)
            }
        }
    }

    /**
     * a small time consuming operation, just do it in EventBus's background thread is fine.
     *
     * @param response
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEventBackgroundThread(response: GetFileResponse) {
        val mediaPlayer = idleMediaPlayer
        val res = NetSoundResponse()
        res.setEventCode(response.eventCode)
        try {
            mediaPlayer.setDataSource(response.resultFile!!.absolutePath)
            mediaPlayer.setOnPreparedListener { mp ->
                mp.start()
                res.responseState = NetSoundResponse.RESPONSE_PLAYING
                EventBus.getDefault().post(res)
            }
            mediaPlayer.setOnCompletionListener { mp ->
                try {
                    mp.reset()
                } catch (ex: IllegalStateException) {

                }

                res.responseState = NetSoundResponse.RESPONSE_COMPLETED
                EventBus.getDefault().post(res)
            }
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
            Log.i(TAG, "playsound failed")
        }

    }

    private fun releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    companion object {
        private val TAG: String? = NetSoundPlayer::class.simpleName
    }
}
