package com.yolo.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.yolo.annotation.Constant;
import com.yolo.annotation.ZJRouter;
import com.yolo.base.BaseActivity;

/**
 * @Author: YOLO
 * @Date: 2020/4/9 21:43
 * @Description:
 */
@ZJRouter(path = Constant.ROUTER_LOGIN_PATH, module = Constant.ROUTER_LOGIN_MODULE)
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
