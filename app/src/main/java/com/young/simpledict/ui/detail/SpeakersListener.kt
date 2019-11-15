package com.young.simpledict.ui.detail

import android.graphics.drawable.AnimationDrawable
import android.text.TextUtils
import android.util.SparseArray
import android.view.View
import android.widget.ImageView

import com.young.simpledict.R
import com.young.simpledict.netsoundplayer.request.NetSoundRequest
import com.young.simpledict.netsoundplayer.request.NetSoundResponse
import com.young.simpledict.service.event.BaseEvent

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Author: landerlyang
 * Date:   2014-10-30
 * Time:   11:49
 * Life with passion. Code with creativity!
 */
class SpeakersListener : View.OnClickListener {
    private val mViewAndUrlMap = SparseArray<String>()
    private var mLastPlayedImageView: ImageView? = null

    override fun onClick(v: View) {
        val request = NetSoundRequest(v, mViewAndUrlMap.get(v.hashCode()))
        if (mLastPlayedImageView !== v) {
            changeUINormal(mLastPlayedImageView)
        }

        if (TextUtils.isEmpty(request.soundUrl)) {
            changeUINormal(v)
            return
        }

        when (v.getTag(VIEW_TAG_ID) as Int) {
            NetSoundResponse.RESPONSE_FAILED,
            NetSoundResponse.RESPONSE_COMPLETED -> {
                request.requestCode = NetSoundRequest.REQUEST_START
                changeUIPreparing(v)
            }
            NetSoundResponse.RESPONSE_PAUSED -> {
                request.requestCode = NetSoundRequest.REQUEST_RESUME
                changeUIPlaying(v)
            }
            NetSoundResponse.RESPONSE_PLAYING -> {
                request.requestCode = NetSoundRequest.REQUEST_PAUSE
                changeUINormal(v)
            }
        }
        EventBus.getDefault().post(request)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(response: NetSoundResponse) {
        if (response.eventCode is View && response.eventCode === mLastPlayedImageView) {
            val v = response.eventCode as View
            when (response.responseState) {
                NetSoundResponse.RESPONSE_PLAYING -> changeUIPlaying(response.eventCode as View)
                NetSoundResponse.RESPONSE_COMPLETED,
                NetSoundRequest.REQUEST_PAUSE,
                NetSoundResponse.RESPONSE_FAILED -> changeUINormal(response.eventCode as View)
            }
            v.setTag(VIEW_TAG_ID, response.responseState)
        }
    }

    private fun changeUINormal(v: View?) {
        if (v is ImageView) {
            v.setImageResource(R.drawable.speaker2)
            v.setTag(VIEW_TAG_ID, NetSoundResponse.RESPONSE_COMPLETED)
        }
    }

    private fun changeUIPreparing(v: View) {
        changeUIPlaying(v)
    }

    private fun changeUIPlaying(v: View) {
        if (v is ImageView) {
            changeUINormal(v)
            val ad =
                v.getContext().getDrawable(R.drawable.speaker_animation_drawable) as AnimationDrawable
            v.setImageDrawable(ad)
            ad.start()
            v.setTag(VIEW_TAG_ID, NetSoundResponse.RESPONSE_PLAYING)
            mLastPlayedImageView = v
        }
    }

    fun addListenerToView(v: View, soundUrl: String) {
        mViewAndUrlMap.put(v.hashCode(), soundUrl)
        v.setTag(VIEW_TAG_ID, NetSoundResponse.RESPONSE_COMPLETED)
        v.setOnClickListener(this)
    }

    fun stopPlaying() {
        val req = NetSoundRequest(
            BaseEvent.EMPTY_EVENT_CODE, "",
            NetSoundRequest.REQUEST_STOP,
            true
        )
        EventBus.getDefault().post(req)
        changeUINormal(mLastPlayedImageView)
    }

    fun register() {
        EventBus.getDefault().register(this)
    }

    fun unRegister() {
        EventBus.getDefault().unregister(this)
    }

    companion object {
        private const val VIEW_TAG_ID = R.id.speaker_imageview_state_id
    }
}
