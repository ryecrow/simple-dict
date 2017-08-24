package com.young.simpledict.service.task;

import com.young.common.YLog;
import com.young.simpledict.service.event.DownloadBlobRequest;
import com.young.simpledict.service.event.DownloadBlobResponse;
import com.young.simpledict.service.httphandler.DownloadBlobHandler;
import de.greenrobot.event.EventBus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;

/**
 * Author: taylorcyang
 * Date:   2014-10-28
 * Time:   20:49
 * Life with passion. Code with creativity!
 */
public class DownloadBlobTask extends BaseTask<File> {
    private static final String TAG = "DownloadBlobTask";
    DownloadBlobRequest mRequest;

    public DownloadBlobTask(DownloadBlobRequest request) {
        mRequest = request;
    }

    @Override
         public File doTheJob() {
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(mRequest.requestUrlAndFileToStore.first);
            File response = httpclient.execute(httpget,
                    new DownloadBlobHandler(mRequest.requestUrlAndFileToStore.second));
            DownloadBlobResponse r = new DownloadBlobResponse();
            r.setEventCode(mRequest.getEventCode());
            r.resultFile = response;
            EventBus.getDefault().post(r);
            return response;
        } catch (Exception e) {
            YLog.i(TAG, "request failed", e);
        }
        return null;
    }

    @Override
    public void onTaskFail() {
        DownloadBlobResponse rep = new DownloadBlobResponse();
        rep.setEventCode(mRequest.getEventCode());
        rep.resultFile = null;
        EventBus.getDefault().post(rep);
    }
}
