package com.young.simpledict.filecache.service;

import android.util.Pair;
import com.young.simpledict.filecache.request.GetFileRequest;
import com.young.simpledict.filecache.request.GetFileResponse;
import com.young.simpledict.service.event.DownloadBlobRequest;
import com.young.simpledict.service.event.DownloadBlobResponse;
import de.greenrobot.event.EventBus;

import java.io.File;

/**
 * Author: taylorcyang
 * Date:   2014-10-28
 * Time:   20:43
 * Life with passion. Code with creativity!
 */
public class FileCache {
    private String mCacheDir;

    public FileCache(String cacheDir) {
        mCacheDir = cacheDir;
        EventBus.getDefault().register(this);
    }

    public String getCacheDir() {
        return mCacheDir;
    }

    public void onEvent(GetFileRequest req) {
        String filename = mCacheDir + req.fileName;
        File file = new File(filename);
        if (file.exists()) {
            GetFileResponse resp = new GetFileResponse();
            resp.setEventCode(req.getEventCode());
            resp.resultFile = file;
            EventBus.getDefault().post(resp);
        } else {
            DownloadBlobRequest downloadRequest = new DownloadBlobRequest();
            downloadRequest.setEventCode(req.getEventCode());
            downloadRequest.requestUrlAndFileToStore = new
                    Pair<String, File>(req.fileUrl, file);
            EventBus.getDefault().post(downloadRequest);
        }
    }

    public void onEvent(DownloadBlobResponse resp) {
        GetFileResponse getFileResponse = new GetFileResponse();
        getFileResponse.setEventCode(resp.getEventCode());
        getFileResponse.resultFile = resp.resultFile;
        EventBus.getDefault().post(getFileResponse);
    }
}
