package com.yolo.routerabout;

import com.yolo.annotation.RouterBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: YOLO
 * @Date: 2020/4/11 22:33
 * @Description:
 */
public class RouterDepository {
    public static Map<String, Class<? extends IModuleRouter>> rootMap = new HashMap<>();
    public static Map<String, RouterBean> moduleMap = new HashMap<>();
}
