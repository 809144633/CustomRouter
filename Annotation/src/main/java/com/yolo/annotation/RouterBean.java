package com.yolo.annotation;


import javax.lang.model.element.Element;

public class RouterBean {
    private String path;
    private String module;
    private Class<?> finalTarget;
    private Element element;
    private jumpType type;

    public enum jumpType{
        /**
         * 跳转类型
         */
        ACTIVITY,
        FRAGMENT
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Class<?> getFinalTarget() {
        return finalTarget;
    }

    public void setFinalTarget(Class<?> finalTarget) {
        this.finalTarget = finalTarget;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public jumpType getType() {
        return type;
    }

    public void setType(jumpType type) {
        this.type = type;
    }

}
