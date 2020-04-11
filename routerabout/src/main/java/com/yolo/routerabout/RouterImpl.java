package com.yolo.routerabout;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.ActivityCompat;

import com.yolo.annotation.Constant;
import com.yolo.annotation.RouterBean;

import java.util.Set;

/**
 * @Author: YOLO
 * @Date: 2020/4/11 13:36
 * @Description:
 */
public class RouterImpl {
    private volatile static RouterImpl instance;
    private Application mApplication;
    private Context mContext;
    private String path;
    private String module;

    public static RouterImpl getInstance() {
        if (instance == null) {
            synchronized (RouterImpl.class) {
                if (instance == null) {
                    instance = new RouterImpl();
                }
            }
        }
        return instance;
    }

    public void init(Application application) {
        this.mApplication = application;
        loadRouter();
    }

    private void loadRouter() {
        if (mApplication == null) {
            throw new IllegalArgumentException("初始化需要传入Application");
        }
        try {
            Set<String> rootRouterFilesName = Utils.getFileNameByPackageName(mApplication, Constant.PACKAGE_NAME);
            for (String file : rootRouterFilesName) {
                if (file.endsWith(Constant.ROUTER_ROOT_END)) {
                    IRootRouter instance = (IRootRouter) Class.forName(file).newInstance();
                    instance.loadMap(RouterDepository.rootMap);
                }
            }
        } catch (InterruptedException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public RouterImpl with(Context context) {
        this.mContext = context;
        return this;
    }

    public RouterImpl path(String path) {
        this.path = path;
        return this;
    }

    public RouterImpl module(String module) {
        this.module = module;
        return this;
    }

    public void navigate() {
        if (!Utils.isMainThread()) {
            throw new IllegalThreadStateException("只能在主线程跳转");
        }
        if (mApplication == null) {
            throw new IllegalArgumentException("路由未初始化");
        }
        if (module == null || path == null) {
            throw new IllegalArgumentException("完善path和module参数");
        }
        Class<? extends IModuleRouter> cls = RouterDepository.rootMap.get(module);
        try {
            IModuleRouter iModuleRouter = cls.getConstructor().newInstance();
            iModuleRouter.loadMap(RouterDepository.moduleMap);
            RouterBean routerBean = RouterDepository.moduleMap.get(path);
            Intent intent = new Intent(mContext, routerBean.getFinalTarget());
            ActivityCompat.startActivity(mContext, intent, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
