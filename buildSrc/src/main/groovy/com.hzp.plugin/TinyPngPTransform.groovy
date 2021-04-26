package com.hzp.plugin


import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import javassist.CtMethod
import javassist.CtNewConstructor
import javassist.Modifier
import javassist.bytecode.ClassFile
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class TinyPngPTransform extends Transform {
    private ClassPool classPool = ClassPool.getDefault()

    TinyPngPTransform(Project project) {
        //为了能够查找到android 相关的类，需要把android.jar包的路径添加到classPool  类搜索路径
        classPool.appendClassPath(project.android.bootClasspath[0].toString())

        classPool.importPackage("android.os.Bundle")
        classPool.importPackage("android.widget.Toast")
        classPool.importPackage("android.app.Activity")

        classPool.importPackage("java.lang.Runnable")
        classPool.importPackage("android.widget.ImageView")
        classPool.importPackage("androidx.appcompat.widget.AppCompatImageView")
        classPool.importPackage("android.graphics.drawable.Drawable")
    }

    @Override
    String getName() {
        return "TinyPngPTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        //接收的输入数据的类型
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        //1. 对inputs -->directory-->class 文件进行遍历
        //2 .对inputs -->jar-->class 文件进行遍历
        //3. //符合我们的项目包名，并且class文件的路径包含Activity.class结尾,还不能是buildconfig.class,R.class $.class

        def outputProvider = transformInvocation.outputProvider
        transformInvocation.inputs.each { input ->

            input.directoryInputs.each { dirInput ->
                println("dirInput abs file path:" + dirInput.file.absolutePath)
                //遍历处理每一个文件夹下面的class文件
                handleDirectory(dirInput.file)

                //将输入的所有目录复制到output指定目录
                //把input->dir->class-->dest目标目录下去。
                def dest = outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(dirInput.file, dest)
            }


            input.jarInputs.each { jarInputs ->
                println("jarInputs abs file path :" + jarInputs.file.absolutePath)
                //对jar 修改完之后，会返回一个新的jar文件Q
                def srcFile = handleJar(jarInputs.file)

                //主要是为了防止重名
                def jarName = jarInputs.name
                def md5 = DigestUtils.md5Hex(jarInputs.file.absolutePath)
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //获取jar包的输出路径
                def dest = outputProvider.getContentLocation(md5 + jarName, jarInputs.contentTypes, jarInputs.scopes, Format.JAR)
                FileUtils.copyFile(srcFile, dest)
            }
        }

        classPool.clearImportedPackages()
    }


    //处理当前目录下所有的class文件
    void handleDirectory(File dir) {
        //将当前路径加入到类池，不然找不到这个类
        classPool.appendClassPath(dir.absolutePath)

        if (dir.isDirectory()) {
            dir.eachFileRecurse { file ->
                def filePath = file.absolutePath
                //确保当前文件是class文件，并且不是系统自动生成的class文件
                ///Users/timian/Desktop/AndroidArchitect/AndroidArchitect/ASProj/app/build/intermediates/transforms/AndroidEntryPointTransform/debug/1/com/hzp/proj/main/degrade/DegradeGlobalActivity.class
                println("handleDirectory file path:" + filePath)
                if (shouldModifyClass(filePath)) {
                    def inputStream = new FileInputStream(file)
                    //修改class
                    def ctClass = modifyClass(inputStream)
                    //把修改后的数据写入到当前目录下
                    ctClass.writeFile(dir.name)
                    ctClass.detach()
                    println("修改完成")
                }
            }
        }
    }

    File handleJar(File jarFile) {
        //添加类搜索路径，否则下面classPool.get()查找类会找不到
        classPool.appendClassPath(jarFile.absolutePath)
        //通过jarFile，才能获取到jar包里的一个个子文件夹
        //ssesWithTinyPngPTransformForDebug
        //jarInputs abs file path :/Users/timian/Desktop/AndroidArchitect/AndroidArchitect/ASProj/app/build/intermediates/transforms/com.alibaba.arouter/debug/0.jar
        def inputJarFile = new JarFile(jarFile)
        //进行迭代遍历
        def enumeration = inputJarFile.entries()

        //jar包里面的class修改之后，不能原路写入jar。会破坏jar文件结构
        //需要单独的一个jar中
        def outputJarFile = new File(jarFile.parentFile, "temp_" + jarFile.name)
        if (outputJarFile.exists()) outputJarFile.delete()
        def jarOutputStream = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(outputJarFile)))
        while (enumeration.hasMoreElements()) {
            def inputJarEntry = enumeration.nextElement()
            def inputJarEntryName = inputJarEntry.name

            //构建一个个需要写入的JarEntry，并且添加到jar包
            def outputJarEntry = new JarEntry(inputJarEntryName)
            jarOutputStream.putNextEntry(outputJarEntry)
            //com/leon/channel/helper/BuildConfig.class
            println("inputJarEntryName: " + inputJarEntryName)

            def inputStream = inputJarFile.getInputStream(inputJarEntry)
            if (!shouldModifyClass(inputJarEntryName)) {
//            if (!shouldModifyClass2(inputJarEntryName)) {
                //如果这个类不需要被修改，那也需要向output jar写入数据，否则class文件会丢失
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
                inputStream.close()
                continue
            }

            def ctClass = modifyClass(inputStream)
//            def ctClass = modifyClass2(inputStream)
            def byteCode = ctClass.toBytecode()
            ctClass.detach()
            inputStream.close()

            //向output jar写入数据
            jarOutputStream.write(byteCode)
            jarOutputStream.flush()
        }
        inputJarFile.close()
        jarOutputStream.closeEntry()
        jarOutputStream.flush()
        jarOutputStream.close()
        return outputJarFile
    }

    //这个方法是 往AppcompatImageView -setImageDrawable --插入不合理大图检测的代码段
    CtClass modifyClass2(InputStream is) {
        def classFile = new ClassFile(new DataInputStream(new BufferedInputStream(is)))
        //com.hzp.hiapp.main.degrade.DegradeGlobalActivity
        println("modifyClass name：" + classFile.name)//全类名
        def ctClass = classPool.get(classFile.name)
        if (ctClass.isFrozen()) {
            ctClass.defrost()
        }

//        def drawable = classPool.get("android.graphics.drawable.Drawable")
//        CtClass[] params = Arrays.asList(drawable).toArray()
//        def setImageDrawableMethod = ctClass.getDeclaredMethod("setImageDrawable", params)
//
//        CtClass runnableImpl = classPool.makeClass("com.hzp.hiapp.debug.RunnableImpl")
//        if (runnableImpl.isFrozen()) {
//            runnableImpl.defrost()
//        }
//
//        CtField viewField = new CtField(classPool.get("androidx.appcompat.widget.AppCompatImageView"), "view", runnableImpl)
//        viewField.setModifiers(Modifier.PUBLIC)
//        runnableImpl.addField(viewField)
//
//
//        CtField drawableField = new CtField(classPool.get("android.graphics.drawable.Drawable"), "drawable", runnableImpl)
//        drawableField.setModifiers(Modifier.PUBLIC)
//        runnableImpl.addField(drawableField)
//
//        runnableImpl.addConstructor(CtNewConstructor.make("public RunnableImpl(android.view.View view, android.graphics.drawable.Drawable drawable) {\n" +
//                "            this.view = view;\n" +
//                "            this.drawable = drawable;\n" +
//                "        }", runnableImpl))
//
//        runnableImpl.addInterface(classPool.get("java.lang.Runnable"))
//
//        CtMethod runMethod = new CtMethod(CtClass.voidType, "run", null, runnableImpl)
//        runMethod.setModifiers(Modifier.PUBLIC)
//        runMethod.setBody("{int width = view.getWidth();\n" +
//                "            int height = view.getHeight();\n" +
//                "            int drawableWidth = drawable.getIntrinsicWidth();\n" +
//                "            int drawableHeight = drawable.getIntrinsicHeight();\n" +
//                "            if (width > 0 && height > 0) {\n" +
//                "                if (drawableWidth >= 2 * width && drawableHeight >= 2 * height) {\n" +
//                "                    android.util.Log.e(\"LargeBitmapChecker\", \"bitmap:[\" + drawableWidth + \",\" + drawableHeight + \"],view:[\" + width + \",\" + height + \"],className:\" + view.getContext().getClass().getSimpleName());\n" +
//                "                }\n" +
//                "            }\n" +
//                "            android.util.Log.e(\"LargeBitmapChecker\", \"bitmap:[\" + drawableWidth + \",\" + drawableHeight + \"],view:[\" + width + \",\" + height + \"],className:\" + view.getContext().getClass().getSimpleName());}")
//        runnableImpl.addMethod(runMethod)
//        runnableImpl.writeFile("hi_debugtool/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes")
//        runnableImpl.toClass()
//
//        classPool.insertClassPath("com.hzp.hiapp.debug.RunnableImpl")
//
//        setImageDrawableMethod.insertBefore("if(drawable!=null){ post(new RunnableImpl(this, drawable));}")
        return ctClass
    }

    //通过文件输入流，使用javassist加载出ctclass类
    //从而操作字节码
    CtClass modifyClass(InputStream is) {
        //之所以使用InputStream，是为了兼顾jar包里面的class文件的处理场景
        //从jar包中获取的一个个文件，只能通过流的形式获取
        def classFile = new ClassFile(new DataInputStream(new BufferedInputStream(is)))
        //com.hzp.hiapp.main.degrade.DegradeGlobalActivity
        println("modifyClass name：" + classFile.name)//全类名
        def ctClass = classPool.get(classFile.name)
        //解冻，意思是指如果该class之前已经被别人加载并修改过了。默认不允许再次被编辑修改
        if (ctClass.isFrozen()) {
            ctClass.defrost()
        }

        //构造onCreate方法的Bundle入参
        def bundle = classPool.get("android.os.Bundle")
        CtClass[] params = Arrays.asList(bundle).toArray()
        //通过ctClass
        def method = ctClass.getDeclaredMethod("onCreate", params)

        def message = classFile.name
        //向方法最后一行插入Toast，message为当前类的全类名
        method.insertAfter("Toast.makeText(this," + "\"" + message + "\"" + ",Toast.LENGTH_SHORT).show();")
        println("modifyClass success name：" + classFile.name+"===>Toast.makeText(this," + "\"" + message + "\"" + ",Toast.LENGTH_SHORT).show();")//全类名
        return ctClass
    }

    boolean shouldModifyClass2(String filePath) {
        return filePath.contains("androidx/appcompat/widget/AppCompatImageView")
    }

    //校验文件的路径以判断是否应该对它修改
    //不是我们包内的class，不是actvity.class都不修改
    boolean shouldModifyClass(String filePath) {
        return (filePath.contains("com\\hzp\\hiapp")
                && filePath.endsWith("Activity.class")
                && !filePath.contains("R.class")
                && !filePath.contains('$')
                && !filePath.contains('R$')
                && !filePath.contains("BuildConfig.class"))
    }

}