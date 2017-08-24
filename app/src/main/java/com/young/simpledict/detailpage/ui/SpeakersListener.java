package com.young.simpledict.detailpage.ui;

import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.young.simpledict.R;
import com.young.simpledict.netsoundplayer.request.NetSoundRequest;
import com.young.simpledict.netsoundplayer.request.NetSoundResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Author: landerlyoung
 * Date:   2014-10-30
 * Time:   11:49
 * Life with passion. Code with creativity!
 */
public class SpeakersListener implements View.OnClickListener {
    private static final int VIEW_TAG_ID = R.id.speaker_imageview_state_id;
    private SparseArray<String> mViewAndUrlMap = new SparseArray<String>();
    private ImageView mLastPlayedImageView;

    @Override
    public void onClick(View v) {
        NetSoundRequest request = new NetSoundRequest();
        request.setEventCode(v);
        request.soundUrl = mViewAndUrlMap.get(v.hashCode());
        if (mLastPlayedImageView != v) {
            changeUI_Normal(mLastPlayedImageView);
        }

        if (TextUtils.isEmpty(request.soundUrl)) {
            changeUI_Normal(v);
            return;
        }

        switch ((Integer) v.getTag(VIEW_TAG_ID)) {
            case NetSoundResponse.RESPONSE_FAILED: //fall through
            case NetSoundResponse.RESPONSE_COMPLETED:
                request.requestCode = NetSoundRequest.REQUEST_START;
                changeUI_Preparing(v);
                break;
            case NetSoundResponse.RESPONSE_PAUSED:
                request.requestCode = NetSoundRequest.REQUEST_RESUME;
                changeUI_Playing(v);
                break;
            case NetSoundResponse.RESPONSE_PLAYING:
                request.requestCode = NetSoundRequest.REQUEST_PAUSE;
                changeUI_Normal(v);
                break;
        }
        EventBus.getDefault().post(request);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NetSoundResponse response) {
        if (response.getEventCode() instanceof View
                && response.getEventCode() == mLastPlayedImageView) {
            View v = (View) response.getEventCode();
            switch (response.responseState) {
                case NetSoundResponse.RESPONSE_PLAYING:
                    changeUI_Playing((View) response.getEventCode());
                    break;
                case NetSoundResponse.RESPONSE_COMPLETED: //fall through
                case NetSoundRequest.REQUEST_PAUSE: //fall through
                case NetSoundResponse.RESPONSE_FAILED:
                    changeUI_Normal((View) response.getEventCode());
                    break;
            }
            v.setTag(VIEW_TAG_ID, response.responseState);
        }
    }

    private void changeUI_Normal(View v) {
        if (v instanceof ImageView) {
            ((ImageView) v).setImageResource(R.drawable.speaker2);
            v.setTag(VIEW_TAG_ID, NetSoundResponse.RESPONSE_COMPLETED);
        }
    }

    private void changeUI_Preparing(View v) {
        changeUI_Playing(v);
    }

    private void changeUI_Playing(View v) {
        if (v instanceof ImageView) {
            changeUI_Normal(v);
            ImageView iv = (ImageView) v;
            AnimationDrawable ad = (AnimationDrawable) v.getContext().getResources()
                    .getDrawable(R.drawable.speaker_animation_drawable);
            iv.setImageDrawable(ad);
            ad.start();
            iv.setTag(VIEW_TAG_ID, NetSoundResponse.RESPONSE_PLAYING);
            mLastPlayedImageView = iv;
        }
    }

    public void addListenerToView(View v, String soundUrl) {
        mViewAndUrlMap.put(v.hashCode(), soundUrl);
        v.setTag(VIEW_TAG_ID, NetSoundResponse.RESPONSE_COMPLETED);
        v.setOnClickListener(this);
    }

    public void stopPlaying() {
        NetSoundRequest req = new NetSoundRequest();
        req.releaseAllMediaPlayer = true;
        EventBus.getDefault().post(req);
        changeUI_Normal(mLastPlayedImageView);
    }

    public void register() {
        EventBus.getDefault().register(this);
    }

    public void unRegister() {
        EventBus.getDefault().unregister(this);
    }
}
