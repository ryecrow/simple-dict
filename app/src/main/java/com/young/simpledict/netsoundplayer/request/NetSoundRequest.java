package com.young.simpledict.netsoundplayer.request;

import com.young.simpledict.service.event.BaseEvent;

/**
 * Author: taylorcyang
 * Date:   2014-10-29
 * Time:   12:11
 * Life with passion. Code with creativity!
 */
public class NetSoundRequest extends BaseEvent {
    public static final int REQUEST_START = 1;
    public static final int REQUEST_PAUSE = 2;
    public static final int REQUEST_STOP = 3;
    public static final int REQUEST_RESUME = 4;

    public String soundUrl;
    public int requestCode = REQUEST_START;
    public boolean releaseAllMediaPlayer = false;
}
