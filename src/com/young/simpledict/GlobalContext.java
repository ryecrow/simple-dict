package com.young.simpledict;

import android.app.Application;
import android.content.Context;
import com.young.common.Singleton;
import com.young.simpledict.service.HttpRequestService;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   15:46
 * Life with passion. Code with creativity!
 */
public class GlobalContext {
    private static AtomicReference<Application> sApplication = new AtomicReference<Application>();

    public static void setApplication(Application a) {
        if (a == null) {
            throw new IllegalArgumentException("parameter application can't be null");
        }

        if (sApplication.getAndSet(a) != null) {
            throw new IllegalStateException("Application can only be set once");
        }
    }

    public static Application getApplication() {
        return sApplication.get();
    }

    public static Context getApplicationContext() {
        Application app = getApplication();
        return app == null ? null : app.getApplicationContext();
    }

    private static Singleton<HttpRequestService> sHttpRequestService = new Singleton<HttpRequestService>() {
        @Override
        public HttpRequestService create() {
            return new HttpRequestService();
        }
    };

    public static HttpRequestService getRequestService() {
        return sHttpRequestService.get();
    }
}
