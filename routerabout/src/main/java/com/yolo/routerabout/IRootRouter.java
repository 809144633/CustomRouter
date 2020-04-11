package com.yolo.routerabout;

import java.util.Map;

/**
 * @Author: YOLO
 * @Date: 2020/4/11 13:22
 * @Description:
 */
public interface IRootRouter {
    Map loadMap(Map<String, Class<? extends IModuleRouter>> rootMap);
}
