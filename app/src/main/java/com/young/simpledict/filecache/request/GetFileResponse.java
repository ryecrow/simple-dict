package com.young.simpledict.filecache.request;

import com.young.simpledict.service.event.BaseEvent;

import java.io.File;

/**
 * Author: landerlyoung
 * Date:   2014-10-29
 * Time:   12:02
 * Life with passion. Code with creativity!
 */
public class GetFileResponse extends BaseEvent {
    public File resultFile;
}
