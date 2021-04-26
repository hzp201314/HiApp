package com.hzp.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

class TinyPngPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        //插件只能运行在com.android.application工程中
        if(!project.plugins.hasPlugin("com.android.application")){
            throw new ProjectConfigurationException("plugin:com.android.application must be apply",null)
        }

        //注册插件
        project.android.registerTransform(new TinyPngPTransform(project))

        //def android = project.extensions.findByType(AppExtension.class)
        //android.registerTransform(new TinyPngPTransform(project))

//        //扩展类属性bean对象
//        class TinyPngExt{
//            List<Integer> whiteList;
//            String apiKey;
//        }
//
//        //
//        //1.获取ExtensionContainer对象
//        def extContainer = project.extensions
//
//        //2.注册扩展对象
//        //name：要创建的Extension的名字，可以是人以符合命名规则的字符串，不能与已有的重复，否则会抛出异常；
//        //type：该Extension的类class类型；
//        //constructionArguments：类的构造函数参数值
//        extContainer.create("tinyPng",TinyPngExt.class,Object... constructionArguments)

    }
}