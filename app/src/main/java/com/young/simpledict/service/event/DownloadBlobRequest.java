package com.young.simpledict.service.event;

import android.util.Pair;

import java.io.File;

/**
 * Author: taylorcyang
 * Date:   2014-10-28
 * Time:   20:47
 * Life with passion. Code with creativity!
 */
public class DownloadBlobRequest extends BaseEvent {
    public Pair<String, File> requestUrlAndFileToStore;
}
