package com.dodge.intercepter

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project
import java.io.File

/**
 *  Created by linzheng on 2019/3/26.
 */

class IntercepterTransform(var project: Project, var config: IntercepterConfig) : Transform() {


    companion object {
        const val TAG = "IntercepterTransform"
    }

    override fun getName(): String {
        return TAG
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    // TODO 实现增量编译
    override fun isIncremental(): Boolean {
        return true
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        // 初始化配置
        println("Dodge_start transform")

        val scanner = IntercepterScanner(project, config)
        scanner.init()
        // Transform
        transformInvocation?.inputs?.forEach { transformInput ->
            // Directory
            transformInput.directoryInputs.forEach { input ->
                val dest = transformInvocation.outputProvider.getContentLocation(input.name, input.contentTypes, input.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(input.file, dest)
                if (input.file.isDirectory) {
                    scanner.scanFromDirectory(input.file, dest)
                }
            }
            // Jar
            transformInput.jarInputs.forEach { input ->
                val dest = getDestFile(input, transformInvocation.outputProvider)
                if (input.file.absolutePath.endsWith(".jar")) {
                    scanner.scanFromJar(input, dest)
                } else {
                    FileUtils.copyFile(input.file, dest)
                }
            }
        }
    }


    private fun getDestFile(jarInput: JarInput, outputProvider: TransformOutputProvider): File {
        var jarName = jarInput.name
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length - 4)
        }
        jarName += DigestUtils.md5Hex(jarInput.file.absolutePath)
        return outputProvider.getContentLocation(jarName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
    }

}