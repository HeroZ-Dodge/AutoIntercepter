package com.dodge.intercepter

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *  Created by linzheng on 2019/3/26.
 */

class IntercepterPlugin : Plugin<Project> {

    companion object {
        const val EXT_NAME = "AutoIntercepter"
    }

    override fun apply(project: Project) {
        println("Dodge apply")
        project.extensions.create(EXT_NAME, IntercepterConfig::class.java)
        project.task("my-task").doLast {
            println("do last")
            val config = project.extensions.findByName(EXT_NAME) as IntercepterConfig
            println(config)
        }

        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            val android = project.extensions.getByType(AppExtension::class.java)
            val config = project.extensions.findByName(EXT_NAME) as IntercepterConfig
            val transform = IntercepterTransform(project, config)
            android.registerTransform(transform)
        }

    }

}