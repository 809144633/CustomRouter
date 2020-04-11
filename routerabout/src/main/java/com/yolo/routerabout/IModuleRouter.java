package com.yolo.routerabout;


import com.yolo.annotation.RouterBean;

import java.util.Map;

/**
 * @Author: YOLO
 * @Date: 2020/4/11 13:22
 * @Description:
 */
public interface IModuleRouter {
    Map loadMap(Map<String, RouterBean> moduleMap);
}
