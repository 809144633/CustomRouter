package com.yolo.customrouter;

import android.os.Bundle;
import android.view.View;

import com.yolo.annotation.Constant;
import com.yolo.annotation.ZJRouter;
import com.yolo.base.BaseActivity;
import com.yolo.routerabout.RouterImpl;

/**
 * @Author: YOLO
 * @Date: 2020/4/9 21:43
 * @Description:
 */
@ZJRouter(path = Constant.ROUTER_MAIN_PATH, module = Constant.ROUTER_APP_MODULE)
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterImpl
                        .getInstance()
                        .with(MainActivity.this)
                        .path(Constant.ROUTER_LOGIN_PATH)
                        .module(Constant.ROUTER_LOGIN_MODULE)
                        .navigate();
            }
        });
    }
}
