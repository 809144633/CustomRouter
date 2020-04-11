package com.yolo.routerabout;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;

import com.yolo.annotation.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dalvik.system.DexFile;

/**
 * @Author: YOLO
 * @Date: 2020/4/11 22:25
 * @Description:
 */
public class Utils {
    /**
     * 根据包名 找到包下的类
     *
     * @param application
     * @param pageName
     * @return
     */
    public static Set<String> getFileNameByPackageName(Application application, final String pageName) throws InterruptedException {
        final Set<String> classNams = new HashSet<>();
        List<String> sourcePath = getSourcePath(application);//apk 的资源路径
        //使用同步计数器判断均处理完成
        final CountDownLatch countDownLatch = new CountDownLatch(sourcePath.size());
        ExecutorService executorService = Executors.newFixedThreadPool(sourcePath.size());
        for (final String path : sourcePath) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    DexFile dexFile = null;
                    try {
                        //加载apk中的dex遍历 获得所有包名为pageName的类
                        dexFile = new DexFile(path);
                        Enumeration<String> entries = dexFile.entries();
                        while (entries.hasMoreElements()) {
                            String className = entries.nextElement();
                            if (className.startsWith(pageName)) {
                                classNams.add(className);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (null != dexFile) {
                            try {
                                dexFile.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //释放1个
                        countDownLatch.countDown();
                    }
                }
            });
        }
        //等待执行完成
        countDownLatch.await();
        return classNams;
    }

    /**
     * 获得程序所有的apk(instant run会产生很多split apk)
     *
     * @param context
     * @return
     */
    public static List<String> getSourcePath(Context context) {
        try {
            ApplicationInfo applicationInfo = context
                    .getPackageManager()
                    .getApplicationInfo(context.getPackageName(), 0);//flags Annotation retention policy.
            List<String> sourceList = new ArrayList<>();
            sourceList.add(applicationInfo.sourceDir);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null != applicationInfo.splitSourceDirs) {
                    sourceList.addAll(Arrays.asList(applicationInfo.splitSourceDirs));
                }
            }
            return sourceList;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}
