package com.young.simpledict.netsoundplayer.request;

import com.young.simpledict.service.event.BaseEvent;

/**
 * Author: taylorcyang
 * Date:   2014-10-29
 * Time:   12:12
 * Life with passion. Code with creativity!
 */
public class NetSoundResponse extends BaseEvent {
    public static final int RESPONSE_PLAYING = 1;
    public static final int RESPONSE_PAUSED = 2;
    public static final int RESPONSE_COMPLETED = 3;
    public static final int RESPONSE_FAILED = -1;

    public int responseState;
}
