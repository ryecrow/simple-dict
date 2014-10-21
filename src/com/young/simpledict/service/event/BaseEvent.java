package com.young.simpledict.service.event;

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   17:40
 * Life with passion. Code with creativity!
 */
public class BaseEvent {
    Object eventCode;
    public BaseEvent setEventCode(Object code) {
        eventCode = code;
        return this;
    }
}
