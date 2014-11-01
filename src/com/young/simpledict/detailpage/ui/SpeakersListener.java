package com.young.simpledict.detailpage.ui;

import android.util.SparseArray;
import android.view.View;
import com.young.simpledict.netsoundplayer.request.NetSoundRequest;
import com.young.simpledict.netsoundplayer.request.NetSoundResponse;
import de.greenrobot.event.EventBus;

/**
 * Author: taylorcyang
 * Date:   2014-10-30
 * Time:   11:49
 * Life with passion. Code with creativity!
 */
public class SpeakersListener implements View.OnClickListener {
    private int mCurrentState = NetSoundResponse.RESPONSE_COMPLETED;

    private SparseArray<String> mViewAndUrlMap = new SparseArray<String>();

    @Override
    public void onClick(View v) {
        NetSoundRequest request = new NetSoundRequest();
        request.setEventCode(this);
        request.soundUrl = mViewAndUrlMap.get(v.getId());

        switch (mCurrentState) {
            case NetSoundResponse.RESPONSE_FAILED: //fall through
            case NetSoundResponse.RESPONSE_COMPLETED:
                request.requestCode = NetSoundRequest.REQUEST_START;
                changeUI_Preparing();
                break;
            case NetSoundResponse.RESPONSE_PAUSED:
                request.requestCode = NetSoundRequest.REQUEST_RESUME;
                changeUI_Playing();
                break;
            case NetSoundResponse.RESPONSE_PLAYING:
                request.requestCode = NetSoundRequest.REQUEST_PAUSE;
                changeUI_Normal();
                break;
        }
        EventBus.getDefault().post(request);
    }

    public void onEventMainThread(NetSoundResponse response) {
        if (response.getEventCode() != this) {
            return;
        }
        switch (response.responseState) {
            case NetSoundResponse.RESPONSE_PLAYING:
                changeUI_Playing();
                break;
            case NetSoundResponse.RESPONSE_COMPLETED: //fall through
            case NetSoundRequest.REQUEST_PAUSE: //fall through
            case NetSoundResponse.RESPONSE_FAILED:
                changeUI_Normal();
                break;
        }
        mCurrentState = response.responseState;
    }


    private void changeUI_Normal() {

    }

    private void changeUI_Preparing() {

    }

    private void changeUI_Playing() {

    }

    public void addViewToListen(View v, String soundUrl) {
        mViewAndUrlMap.put(v.getId(), soundUrl);
        v.setOnClickListener(this);
    }

    public void register() {
        EventBus.getDefault().register(this);
    }

    public void unRegister() {
        EventBus.getDefault().unregister(this);
    }
}
