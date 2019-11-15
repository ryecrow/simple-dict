package com.young.simpledict.service.task;

import android.util.Log;

import com.young.simpledict.service.event.DownloadBlobRequest;
import com.young.simpledict.service.event.DownloadBlobResponse;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

/**
 * Author: landerlyoung
 * Date:   2014-10-28
 * Time:   20:49
 * Life with passion. Code with creativity!
 */
public class DownloadBlobTask extends BaseTask<File> {
    private static final String TAG = "DownloadBlobTask";
    private DownloadBlobRequest mRequest;

    public DownloadBlobTask(DownloadBlobRequest request) {
        mRequest = request;
    }

    @Override
    public File doTheJob() {
        OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier((hostname, session) -> true)
                .build();
        Request getFileRequest = new Request.Builder()
                .get()
                .url(mRequest.requestUrlAndFileToStore.first)
                .build();
        Response response;
        try {
            response = client.newCall(getFileRequest).execute();
        } catch (IOException e) {
            Log.i(TAG, "Failed to execute request.", e);
            return null;
        }
        ResponseBody body = response.body();
        if (!response.isSuccessful() || (body == null)) {
            Log.i(TAG, "Unexpected response status: " + response.code());
            return null;
        }
        File fileToWrite = mRequest.requestUrlAndFileToStore.second;
        try (BufferedSink sink = Okio.buffer(Okio.sink(fileToWrite))) {
            sink.writeAll(body.source());
        } catch (IOException e) {
            Log.i(TAG, "request failed", e);
            return null;
        }
        DownloadBlobResponse r = new DownloadBlobResponse();
        r.setEventCode(mRequest.getEventCode());
        r.resultFile = fileToWrite;
        EventBus.getDefault().post(r);
        return fileToWrite;
    }

    @Override
    public void onTaskFail() {
        DownloadBlobResponse rep = new DownloadBlobResponse();
        rep.setEventCode(mRequest.getEventCode());
        rep.resultFile = null;
        EventBus.getDefault().post(rep);
    }
}
