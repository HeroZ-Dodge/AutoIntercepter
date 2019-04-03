package com.dodge.intercepter

import com.android.build.api.transform.JarInput
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter
import java.io.File
import java.io.InputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 *  Created by linzheng on 2019/3/27.
 */

class IntercepterScanner(var project: Project, var config: IntercepterConfig) {

    private var isDebug = false
    private var intercepterList: MutableList<IntercepterInfo> = mutableListOf()


    fun init() {
        intercepterList.clear()
        config.intercepterList.forEach { map ->
            val intercepter = IntercepterInfo()
            intercepter.annotation = map.getValue("annotation").replace(".", "/")
            intercepter.intercepterName = map.getValue("intercepterName").replace(".", "/")
            intercepter.intercepterMethod = map.getValue("intercepterMethod").replace(".", "/")
            intercepterList.add(intercepter)
        }
        isDebug = config.isDebug
        if (isDebug) {
            project.logger.debug("IntercepterScanner", intercepterList.toString())
        }
    }

    fun scanFromDirectory(directory: File, destDir: File) {
        var root = directory.absolutePath
        if (!root.endsWith(File.separator)) {
            root += File.separator
        }
        println("IntercepterScanner root = $root")
        forEachFile(directory) { file ->
            if (file.absolutePath.endsWith(".class")) {
                println("IntercepterScanner file = ${file.absolutePath}")
                val inputStream = file.inputStream()
                val result = insertCode(inputStream)
                if (result.first) {
                    val name = file.absolutePath.replace(root, "")
                    val dest = File(destDir, name)
                    val code = result.second.toByteArray()
                    FileUtils.copyInputStreamToFile(code.inputStream(), dest)
                }
                inputStream.close()
            }
        }
    }

    fun scanFromClassFile(root: String, file: File, destDir: File) {
        println("IntercepterScanner root = $root")
        if (file.absolutePath.endsWith(".class")) {
            println("IntercepterScanner file = ${file.absolutePath}")
            val inputStream = file.inputStream()
            val result = insertCode(inputStream)
            if (result.first) {
                val name = file.absolutePath.replace(root, "")
                val dest = File(destDir, name)
                val code = result.second.toByteArray()
                FileUtils.copyInputStreamToFile(code.inputStream(), dest)
            }
            inputStream.close()
        }
    }


    fun scanFromJar(jarInput: JarInput, dest: File) {
        if (dest.exists()) {
            dest.delete()
        }
        val jarOutputStream = JarOutputStream(dest.outputStream())
        // jar file
        val jarFile = JarFile(jarInput.file)
        val enumeration = jarFile.entries()
        while (enumeration.hasMoreElements()) { // every entry
            val jarEntry = enumeration.nextElement()
            val inputStream = jarFile.getInputStream(jarEntry)
            val zipEntry = ZipEntry(jarEntry.name)
            jarOutputStream.putNextEntry(zipEntry)
            //
            if (jarEntry.name.endsWith(".class")) {
                val result = insertCode(inputStream)
                jarOutputStream.write(result.second.toByteArray())
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            inputStream.close()
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        jarFile.close()
    }

    /**
     * 递归遍历文件夹下的所有文件
     */
    private fun forEachFile(file: File, callback: (file: File) -> Unit) {
        file.listFiles().forEach {
            if (it.isDirectory) {
                forEachFile(it, callback)
            } else {
                callback.invoke(it)
            }
        }
    }

    /**
     * 扫描接口
     */
    private fun insertCode(inputStream: InputStream): Pair<Boolean, ClassWriter> {
        val classReader = ClassReader(inputStream)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val classVisitor = MyClassVisitor(Opcodes.ASM6, classWriter)
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        return Pair(classVisitor.hasAnnotation(), classWriter)
    }


    inner class MyClassVisitor(api: Int, cv: ClassVisitor?) : ClassVisitor(api, cv) {

        private var methodVisitor: MyMethodVisitor? = null

        override fun visitMethod(access: Int, name: String?, desc: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
            val mv = super.visitMethod(access, name, desc, signature, exceptions)
            methodVisitor = MyMethodVisitor(Opcodes.ASM6, mv, access, name, desc)
            return methodVisitor!!
        }

        fun hasAnnotation(): Boolean {
            return methodVisitor?.hasAnnotation() ?: false
        }


    }


    inner class MyMethodVisitor(api: Int, mv: MethodVisitor?, access: Int, var name: String?, var desc: String?) : AdviceAdapter(api, mv, access, name, desc) {

        private var intercepter: IntercepterInfo? = null

        override fun visitAnnotation(desc: String?, visible: Boolean): AnnotationVisitor {
            if (intercepter == null) {
                intercepter = intercepterList.find {
                    if (desc?.isEmpty() != false) {
                        return@find false
                    } else {
                        return@find desc.contains(it.annotation)
                    }
                }

                if (intercepter != null) {
                    println("find annotation = $desc")
                }


            }
            return super.visitAnnotation(desc, visible)
        }


        override fun onMethodEnter() {
            intercepter?.let {
                println("name = $name, desc = $desc")
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, it.intercepterName, it.intercepterMethod, "()Z", false)
                val l1 = Label()
                mv.visitJumpInsn(Opcodes.IFEQ, l1)
                mv.visitInsn(Opcodes.RETURN)
                mv.visitLabel(l1)
            }
        }


        override fun onMethodExit(opcode: Int) {

        }


        fun hasAnnotation(): Boolean {
            return intercepter != null
        }


    }


}