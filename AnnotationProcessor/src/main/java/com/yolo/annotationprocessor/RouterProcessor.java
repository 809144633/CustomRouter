package com.yolo.annotationprocessor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.yolo.annotation.Constant;
import com.yolo.annotation.RouterBean;
import com.yolo.annotation.ZJRouter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @Author: YOLO
 * @Date: 2020/4/8 22:28
 * @Description:
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.yolo.annotation.ZJRouter"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({Constant.MODULE_NAME})
public class RouterProcessor extends AbstractProcessor {
    private Messager messager;
    private Map<String, String> options;
    private Elements elementUtils;
    private Filer filer;
    private Types typeUtils;
    private TypeElement activity;
    private Map<String, String> rootMap;
    private Map<String, List<RouterBean>> moduleMap;
    private ZJRouter zjRouter;

    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
        moduleMap = new HashMap<>();
        rootMap = new HashMap<>();
        messager = environment.getMessager();
        options = environment.getOptions();
        elementUtils = environment.getElementUtils();
        filer = environment.getFiler();
        typeUtils = environment.getTypeUtils();
        if (!options.isEmpty()) {
            String successFlag = "connect success:" + options.get(Constant.MODULE_NAME);
            messager.printMessage(Diagnostic.Kind.NOTE, successFlag);
        }
        activity = elementUtils.getTypeElement(Constant.ACTIVITY);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!set.isEmpty()) {
            Set<? extends Element> routers = roundEnvironment.getElementsAnnotatedWith(ZJRouter.class);
            if (!routers.isEmpty()) {
                process(routers);
            }
            return true;
        }
        return false;
    }

    private void process(Set<? extends Element> routers) {
        for (Element router : routers) {
            zjRouter = router.getAnnotation(ZJRouter.class);
            //检查@ZJRouter是否正确修饰
            checkAnnotationIsLegal(router);
        }
        //创建module路由
        createModuleRouterFile();
        createRootRouterFile();
    }

    private void createModuleRouterFile() {
        //Map<String, RouterBean> moduleMap
        ParameterSpec parameterMap = ParameterSpec
                .builder(
                        ParameterizedTypeName.get(
                                ClassName.get(Map.class),
                                ClassName.get(String.class),
                                ClassName.get(RouterBean.class)),
                        Constant.MODULE_PARAMETER_NAME
                ).build();
        for (Map.Entry<String, List<RouterBean>> entry : moduleMap.entrySet()) {
            MethodSpec.Builder methodBuilder = MethodSpec
                    .methodBuilder(Constant.MODULE_METHOD_NAME)
                    .returns(Map.class)
                    .addParameter(parameterMap)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC);
            String moduleName = entry.getKey();
            int i = 0;
            for (RouterBean bean : entry.getValue()) {
                String beanName = "bean" + i;
                methodBuilder.addStatement("$T " + beanName + " = new $T()", ClassName.get(RouterBean.class), ClassName.get(RouterBean.class));
                methodBuilder.addStatement(beanName + ".setPath($S)", bean.getPath());
                methodBuilder.addStatement(beanName + ".setModule($S)", bean.getModule());
                methodBuilder.addStatement(beanName + ".setFinalTarget($T.class)", ClassName.get((TypeElement) bean.getElement()));
                methodBuilder.addStatement(beanName + ".setType($T.$L)", ClassName.get(RouterBean.jumpType.class), bean.getType());
                methodBuilder.addStatement(Constant.MODULE_PARAMETER_NAME + ".put($S, " + beanName + ")", bean.getPath());
                i++;
            }
            methodBuilder.addStatement("return " + Constant.MODULE_PARAMETER_NAME);
            MethodSpec methodSpec = methodBuilder.build();
            String moduleClsName = Utils.captureName(moduleName) + Constant.ROUTER_MODULE_END;
            TypeSpec cls = TypeSpec.classBuilder(moduleClsName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ClassName.get("com.yolo.routerabout", "IModuleRouter"))
                    .addMethod(methodSpec).build();
            JavaFile file = JavaFile.builder(Constant.PACKAGE_NAME, cls)
                    .build();
            try {
                file.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Utils.isEmpty(rootMap.get(moduleName))) {
                rootMap.put(moduleName, moduleClsName);
            }
        }

    }

    private void createRootRouterFile() {
        //Map<String, Class<? extends IModuleRouter>> rootMap
        if (Utils.isEmpty(rootMap)) {
            throw new RuntimeException("路由出错，无法创建根路由");
        }
        ParameterSpec parameterMap = ParameterSpec
                .builder(
                        ParameterizedTypeName.get(
                                ClassName.get(Map.class),
                                ClassName.get(String.class),
                                ParameterizedTypeName.get(
                                        ClassName.get(Class.class),
                                        WildcardTypeName.subtypeOf(ClassName.get("com.yolo.routerabout", "IModuleRouter"))
                                )),
                        Constant.ROOT_PARAMETER_NAME
                ).build();

        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.ROOT_METHOD_NAME)
                    .returns(ClassName.get(Map.class))
                    .addAnnotation(Override.class)
                    .addParameter(parameterMap)
                    .addModifiers(Modifier.PUBLIC);
            methodBuilder.addStatement(
                    Constant.ROOT_PARAMETER_NAME + ".put($S, $T.class)",
                    entry.getKey(), ClassName.get(Constant.PACKAGE_NAME, entry.getValue())
            );
            methodBuilder.addStatement("return " + Constant.ROOT_PARAMETER_NAME);
            MethodSpec methodSpec = methodBuilder.build();
            TypeSpec cls = TypeSpec.classBuilder(Utils.captureName(entry.getKey()) + Constant.ROUTER_ROOT_END)
                    .addMethod(methodSpec)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(
                            ClassName.get(
                                    "com.yolo.routerabout",
                                    "IRootRouter"
                            )
                    ).build();
            JavaFile file = JavaFile.builder(Constant.PACKAGE_NAME, cls).build();
            try {
                file.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void checkAnnotationIsLegal(Element router) {
        TypeMirror routerType = router.asType();
        if (!typeUtils.isSubtype(routerType, activity.asType())) {
            messager.printMessage(Diagnostic.Kind.NOTE, "@Router只能修饰在Activity上");
            //指定异常抛出
            throw new RuntimeException("@Router只能修饰在Activity上");
        }
        RouterBean routerBean = createRouterBean(router, true);
        List<RouterBean> routerBeans = moduleMap.get(routerBean.getModule());
        if (routerBeans == null) {
            routerBeans = new ArrayList<>();
            routerBeans.add(routerBean);
        } else {
            routerBeans.add(routerBean);
        }
        moduleMap.put(routerBean.getModule(), routerBeans);
    }

    private RouterBean createRouterBean(Element router, boolean isActivity) {
        RouterBean routerBean = new RouterBean();
        String path = zjRouter.path();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        routerBean.setType(isActivity ? RouterBean.jumpType.ACTIVITY : RouterBean.jumpType.FRAGMENT);
        routerBean.setPath(path);
        routerBean.setModule(zjRouter.module());
        routerBean.setElement(router);
        return routerBean;
    }
}
