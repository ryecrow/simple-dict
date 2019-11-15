package com.young.simpledict.netsoundplayer.service;

import android.media.MediaPlayer;
import android.util.Log;

import com.young.simpledict.filecache.request.GetFileRequest;
import com.young.simpledict.filecache.request.GetFileResponse;
import com.young.simpledict.filecache.util.FileNameDigester;
import com.young.simpledict.netsoundplayer.request.NetSoundRequest;
import com.young.simpledict.netsoundplayer.request.NetSoundResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

/**
 * Author: taylorcyang
 * Date:   2014-10-29
 * Time:   12:09
 * Life with passion. Code with creativity!
 */
public class NetSoundPlayer {
    private static final String TAG = "NetSoundPlayer";
    private MediaPlayer mMediaPlayer;

    public NetSoundPlayer() {
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetSoundRequest request) {
        if (request.releaseAllMediaPlayer) {
            releaseMediaPlayer();
        } else if (request.requestCode == NetSoundRequest.REQUEST_START) {
            GetFileRequest getFileRequest = new GetFileRequest();
            getFileRequest.setEventCode(request.getEventCode());
            getFileRequest.fileName = FileNameDigester.digest(request.soundUrl);
            getFileRequest.fileUrl = request.soundUrl;
            EventBus.getDefault().post(getFileRequest);
        } else {
            int retState = NetSoundResponse.RESPONSE_FAILED;
            if (mMediaPlayer != null) {
                try {
                    switch (request.requestCode) {
                        case NetSoundRequest.REQUEST_PAUSE:
                            mMediaPlayer.pause();
                            retState = NetSoundResponse.RESPONSE_PAUSED;
                            break;
                        case NetSoundRequest.REQUEST_RESUME:
                            mMediaPlayer.start();
                            retState = NetSoundResponse.RESPONSE_PLAYING;
                            break;
                        case NetSoundRequest.REQUEST_STOP:
                            mMediaPlayer.stop();
                            retState = NetSoundResponse.RESPONSE_COMPLETED;
                            break;
                    }
                } catch (IllegalStateException e) {
                    try {
                        //try to reset mediaplayer
                        mMediaPlayer.reset();
                    } catch (IllegalStateException ex) {

                    }
                    //already initialized
                    //retState = NetSoundResponse.RESPONSE_FAILED;
                }
            }
            NetSoundResponse response = new NetSoundResponse();
            response.setEventCode(request.getEventCode());
            response.responseState = retState;
            EventBus.getDefault().post(response);
        }
    }

    /**
     * a small time consuming operation, just do it in EventBus's background thread is fine.
     *
     * @param response
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackgroundThread(GetFileResponse response) {
        MediaPlayer mp = getIdleMediaPlayer();
        final NetSoundResponse res = new NetSoundResponse();
        res.setEventCode(response.getEventCode());
        try {
            mp.setDataSource(response.resultFile.getAbsolutePath());
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    res.responseState = NetSoundResponse.RESPONSE_PLAYING;
                    EventBus.getDefault().post(res);
                }
            });
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try {
                        mp.reset();
                    } catch (IllegalStateException ex) {

                    }
                    res.responseState = NetSoundResponse.RESPONSE_COMPLETED;
                    EventBus.getDefault().post(res);
                }
            });
            mp.prepareAsync();
        } catch (IOException e) {
            Log.i(TAG, "playsound failed");
        }
    }

    private MediaPlayer getIdleMediaPlayer() {
        try {
            if(mMediaPlayer != null) {
                mMediaPlayer.reset();
            } else {
                mMediaPlayer = new MediaPlayer();
            }
        } catch (IllegalStateException e) {
            mMediaPlayer = new MediaPlayer();
        }

        return mMediaPlayer;
    }

    public void releaseMediaPlayer() {
        if (mMediaPlayer!=null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
