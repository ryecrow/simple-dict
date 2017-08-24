package com.young.simpledict.service.event;

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   17:40
 * Life with passion. Code with creativity!
 */
public class BaseEvent {
    public Object eventCode;

    public BaseEvent() {

    }

    public BaseEvent(Object eventCode) {
        this.eventCode = eventCode;
    }

    public BaseEvent setEventCode(Object code) {
        eventCode = code;
        return this;
    }

    public Object getEventCode() {
        return eventCode;
    }
}
