package com.young.simpledict;

import android.app.Application;
import com.young.simpledict.service.event.OnApplicationStartEvent;
import com.young.simpledict.service.event.OnApplicationTerminateEvent;
import de.greenrobot.event.EventBus;

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   16:46
 * Life with passion. Code with creativity!
 */
public class SimpledictApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GlobalContext.setApplication(this);
        initApplication();
        EventBus.getDefault().post(
                new OnApplicationStartEvent().setEventCode(SimpledictApplication.class));
    }

    private void initApplication() {
        GlobalContext.getRequestService();

    }

    @Override
    public void onTerminate() {
        EventBus.getDefault().post(
                new OnApplicationTerminateEvent().setEventCode(SimpledictApplication.class));
        super.onTerminate();
    }
}
