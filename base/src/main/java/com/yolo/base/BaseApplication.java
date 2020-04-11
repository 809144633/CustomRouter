package com.yolo.base;

import android.app.Application;

import com.yolo.routerabout.RouterImpl;

/**
 * @Author: YOLO
 * @Date: 2020/4/8 22:43
 * @Description:
 */
public class BaseApplication extends Application {
    private Application application = this;

    @Override
    public void onCreate() {
        super.onCreate();
        RouterImpl.getInstance().init(this);
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
