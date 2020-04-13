package com.yolo.annotation;

import java.util.Map;

/**
 * @Author: YOLO
 * @Date: 2020/4/9 20:51
 * @Description:
 */
public class Constant {
    public static final String MODULE_NAME = "moduleName";
    public static final String MODULE_METHOD_NAME = "loadMap";
    public static final String MODULE_PARAMETER_NAME = "moduleMap";
    public static final String ROOT_METHOD_NAME = "loadMap";
    public static final String ROOT_PARAMETER_NAME = "rootMap";
    public static final String ACTIVITY = "android.app.Activity";
    public static final String PACKAGE_NAME = "com.yolo.router.processor";
    /**
     * JavaPoet命名规范
     */
    public static final String ROUTER_ROOT_END = "$$Root$$Router";
    public static final String ROUTER_MODULE_END = "$$Module$$Router";
    /**
     * app Module
     */
    public static final String ROUTER_APP_MODULE = "app";
    public static final String ROUTER_MAIN_PATH = "/MainActivity";
    public static final String ROUTER_SECOND_PATH = "/SecondActivity";
    /**
     * login Module
     */
    public static final String ROUTER_LOGIN_PATH = "/LoginActivity";
    public static final String ROUTER_LOGIN_MODULE = "login";


}
