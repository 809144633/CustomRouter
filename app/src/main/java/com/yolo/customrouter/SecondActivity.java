package com.yolo.customrouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.yolo.annotation.Constant;
import com.yolo.annotation.ZJRouter;

@ZJRouter(path = Constant.ROUTER_SECOND_PATH, module = Constant.ROUTER_APP_MODULE)
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
