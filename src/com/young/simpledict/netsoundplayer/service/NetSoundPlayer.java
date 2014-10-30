package com.young.simpledict.netsoundplayer.service;

import android.media.MediaPlayer;
import android.util.SparseArray;
import com.young.common.YLog;
import com.young.simpledict.GlobalContext;
import com.young.simpledict.filecache.request.GetFileRequest;
import com.young.simpledict.filecache.request.GetFileResponse;
import com.young.simpledict.filecache.util.FileNameDigester;
import com.young.simpledict.netsoundplayer.request.NetSoundRequest;
import com.young.simpledict.netsoundplayer.request.NetSoundResponse;
import de.greenrobot.event.EventBus;

import java.io.IOException;

/**
 * Author: taylorcyang
 * Date:   2014-10-29
 * Time:   12:09
 * Life with passion. Code with creativity!
 */
public class NetSoundPlayer {
    private static final String TAG = "NetSoundPlayer";
    private SparseArray<MediaPlayer> mMediaPlayerPool;
    private static final int MAX_MEDIA_PLAYER_POOL_SIZE = 3;
    private int mMediaPlayerHandel;

    public NetSoundPlayer() {
        EventBus.getDefault().register(this);
        mMediaPlayerPool = new SparseArray<MediaPlayer>();
    }

    public void onEvent(NetSoundRequest request) {
        if (request.releaseAllMediaPlayer) {
            releaseAllMediaPlayer();
        } else if (request.requestCode == NetSoundRequest.REQUEST_START) {
            GetFileRequest getFileRequest = new GetFileRequest();
            getFileRequest.fileName = FileNameDigester.digest(request.soundUrl);
            getFileRequest.fileUrl = request.soundUrl;
            EventBus.getDefault().post(getFileRequest);
        } else {
            MediaPlayer m = mMediaPlayerPool.get(request.mediaPlayerHandle);
            int retState = NetSoundResponse.RESPONSE_FAILED;
            if (m != null) {
                try {
                    switch (request.requestCode) {
                        case NetSoundRequest.REQUEST_PAUSE:
                            m.pause();
                            retState = NetSoundResponse.RESPONSE_PAUSED;
                            break;
                        case NetSoundRequest.REQUEST_RESUME:
                            m.start();
                            retState = NetSoundResponse.RESPONSE_PLAYING;
                            break;
                        case NetSoundRequest.REQUEST_STOP:
                            m.stop();
                            retState = NetSoundResponse.RESPONSE_STOPPED;
                            break;
                    }
                } catch (IllegalStateException e) {
                    //retState = NetSoundResponse.RESPONSE_FAILED;
                }
            }
            NetSoundResponse response = new NetSoundResponse();
            response.setEventCode(request.getEventCode());
            response.responseState = retState;
            response.mediaPlayerHandel = request.mediaPlayerHandle;
            EventBus.getDefault().post(response);
        }
    }

    /**
     * a small time consuming operation, just do it in EventBus's background thread is fine.
     *
     * @param response
     */
    public void onEventBackgroundThread(final GetFileResponse response) {
        MediaPlayer mp = getIdleMediaPlayer();
        try {
            mp.setDataSource(response.resultFile.getAbsolutePath());
            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    //FIXME 测试能否监听resume动作
                }
            });
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    NetSoundResponse response = new NetSoundResponse();
                    response.setEventCode(response.getEventCode());
                    response.responseState = NetSoundResponse.RESPONSE_PLAYING;
                    response.mediaPlayerHandel = getMediaPlayerHandel(mp);
                    EventBus.getDefault().post(response);
                }
            });
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    NetSoundResponse netSoundResponse = new NetSoundResponse();
                    netSoundResponse.setEventCode(response.getEventCode());
                    EventBus.getDefault().post(netSoundResponse);
                    GlobalContext.getApplication().getPackageCodePath();
                    mp.release();

                    NetSoundResponse response = new NetSoundResponse();
                    response.setEventCode(response.getEventCode());
                    response.responseState = NetSoundResponse.RESPONSE_STOPPED;
                    response.mediaPlayerHandel = getMediaPlayerHandel(mp);
                    EventBus.getDefault().post(response);
                }
            });
            mp.prepareAsync();
        } catch (IOException e) {
            YLog.i(TAG, "playsound failed");
        }
    }

    private int getMediaPlayerHandel(MediaPlayer mp) {
        return mMediaPlayerPool.indexOfValue(mp);
    }

    private MediaPlayer getIdleMediaPlayer() {
        int len = mMediaPlayerPool.size();
        for (int i = 0; i < len; i++) {
            MediaPlayer m = mMediaPlayerPool.valueAt(i);
            try {
                m.isPlaying();
            } catch (IllegalStateException e) {
                //the mediaplayer is released or not initialized
                m.reset();
                return m;
            }
        }

        MediaPlayer m = new MediaPlayer();
        if (len < MAX_MEDIA_PLAYER_POOL_SIZE) {
            mMediaPlayerPool.put(++mMediaPlayerHandel, m);
        }
        return m;
    }

    public void releaseAllMediaPlayer() {
        YLog.i(TAG, "release all media player");
        int len = mMediaPlayerPool.size();
        for (int i = 0; i < len; i++) {
            MediaPlayer m = mMediaPlayerPool.valueAt(i);
            m.release();
        }
        mMediaPlayerPool.clear();
    }
}
