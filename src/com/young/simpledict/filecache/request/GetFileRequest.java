package com.young.simpledict.filecache.request;

import com.young.simpledict.service.event.BaseEvent;

/**
 * Author: taylorcyang
 * Date:   2014-10-29
 * Time:   12:01
 * Life with passion. Code with creativity!
 */
public class GetFileRequest extends BaseEvent {
    public String fileName;
    public String fileUrl;
}
