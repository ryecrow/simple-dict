package com.young.simpledict.detailpage.ui;

import android.view.View;
import com.young.simpledict.netsoundplayer.request.NetSoundRequest;
import com.young.simpledict.netsoundplayer.request.NetSoundResponse;
import de.greenrobot.event.EventBus;

import java.util.HashMap;

/**
 * Author: taylorcyang
 * Date:   2014-10-30
 * Time:   11:49
 * Life with passion. Code with creativity!
 */
public class SpeakerListener implements View.OnClickListener {
    private View mAttachView;
    private int mCurrentState = NetSoundResponse.RESPONSE_STOPPED;
    private int mMediaPlayerHandel = -1;

    public SpeakerListener(View v) {
        mAttachView = v;
        HashMap<String, String> map = newHashNap();
    }
    public static <K,V> HashMap<K,V> newHashNap() {
        return new HashMap<K, V>();
    }


    @Override
    public void onClick(View v) {
        NetSoundRequest request = new NetSoundRequest();
        request.setEventCode(this);
        request.mediaPlayerHandle = mMediaPlayerHandel;

        switch (mCurrentState) {
            case NetSoundResponse.RESPONSE_FAILED:
                //fall through
            case NetSoundResponse.RESPONSE_STOPPED:
                request.requestCode = NetSoundRequest.REQUEST_START;
                break;
            case NetSoundResponse.RESPONSE_PAUSED:
                request.requestCode = NetSoundRequest.REQUEST_RESUME;
                break;
            case NetSoundResponse.RESPONSE_PLAYING:
                request.requestCode = NetSoundRequest.REQUEST_PAUSE;
                break;
        }
        EventBus.getDefault().post(request);
    }

    public void onEvent(NetSoundResponse response) {
        if (response.getEventCode() != this) {
            return;
        }
        mCurrentState = response.responseState;
        mMediaPlayerHandel = response.mediaPlayerHandel;
    }

    public static void setOnClickListener(View v) {
        v.setOnClickListener(new SpeakerListener(v));
    }
}
