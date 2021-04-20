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

        project.android.registerTransform(new TinyPngPTransform(project))

        //def android = project.extensions.findByType(AppExtension.class)
        //android.registerTransform(new TinyPngPTransform(project))
    }
}