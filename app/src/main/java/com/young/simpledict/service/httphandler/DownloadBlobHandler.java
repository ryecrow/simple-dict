package com.young.simpledict.service.httphandler;

import com.young.common.IOUtil;
import com.young.common.YLog;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author: taylorcyang
 * Date:   2014-10-28
 * Time:   21:38
 * Life with passion. Code with creativity!
 */
    public class DownloadBlobHandler implements ResponseHandler<File> {
        private static final String TAG = "DownloadBlobHandler";
        private File mFile;

        public DownloadBlobHandler(File fileToStore) {
            if (fileToStore == null) {
                throw new NullPointerException("param fileToStore can not be null");
            }
            mFile = fileToStore;
        }

        @Override
        public File handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
            int status = httpResponse.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    FileOutputStream out = new FileOutputStream(mFile);
                    try {
                        entity.writeTo(out);
                    } finally {
                        IOUtil.closeSilently(out);
                    }
                }
                return mFile;
            } else {
                YLog.i(TAG, "Unexpected response status: " + status);
            }
            return null;
        }
}
