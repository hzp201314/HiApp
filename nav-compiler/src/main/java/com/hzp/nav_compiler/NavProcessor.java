package com.hzp.nav_compiler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.auto.service.AutoService;
import com.hzp.nav_annotation.Destination;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.hzp.nav_annotation.Destination")
public class NavProcessor extends AbstractProcessor {

    private static final String PAGE_TYPE_ACTIVITY = "Activity";
    private static final String PAGE_TYPE_FRAGMENT = "Fragment";
    private static final String PAGE_TYPE_DIALOG = "Dialog";
    private static final String OUTPUT_FILE_NAME = "destination.json";
    private Messager messager;
    private Filer filer;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "enter init...");
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Destination.class);
//        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ActivityDestination.class);

        if (!elements.isEmpty()) {
            HashMap<String, JSONObject> destMap = new HashMap<>();
            handleDestination(elements, Destination.class, destMap);

            try {
                FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", OUTPUT_FILE_NAME);
                // 生成临时文件绝对路径:/app/build/intermediates/javac/debug/classes/
                // 目标存储路径：/app/main/assets/
                String resourcePath = resource.toUri().getPath();

                String appPath = resourcePath.substring(0, resourcePath.indexOf("app") + 4);
                String assetsPath = appPath + "src/main/assets";

                File file = new File(assetsPath);
                if(!file.exists()){
                    file.mkdirs();
                }

                String content = JSON.toJSONString(destMap);

                File outputFile = new File(assetsPath, OUTPUT_FILE_NAME);
                if(outputFile.exists()){
                    outputFile.delete();
                }
                outputFile.createNewFile();

                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
                writer.write(content);
                writer.flush();

                fileOutputStream.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return false;
    }

    private void handleDestination(Set<? extends Element> elements, Class<Destination> destinationClass, HashMap<String, JSONObject> destMap) {
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;

            //全类名
            String clazzName = typeElement.getQualifiedName().toString();

            //获取Destination注解
            Destination annotation = typeElement.getAnnotation(destinationClass);

            //获取url
            String pageUrl = annotation.pageUrl();
            //获取是否作为启动页
            boolean asStarter = annotation.asStarter();
            //页面id
            int id = Math.abs(clazzName.hashCode());

            //获取注解所在类的类型(Activity,Fragment,...)
            String destType = getDestinationType(typeElement);

            if(destMap.containsKey(pageUrl)){
                messager.printMessage(Diagnostic.Kind.ERROR,"不同的页面不允许使用相同的pageUrl:"+pageUrl);
            }else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clazzName",clazzName);
                jsonObject.put("pageUrl",pageUrl);
                jsonObject.put("asStarter",asStarter);
                jsonObject.put("id",id);
                jsonObject.put("destType",destType);
                destMap.put(pageUrl,jsonObject);
            }

        }
    }

    private String getDestinationType(TypeElement typeElement) {

        //获取父类的类型
        TypeMirror typeMirror = typeElement.getSuperclass();
        //父类全类名(androidx.fragment.app.Fragment)
        String superClazzName = typeMirror.toString();

        if (superClazzName.contains(PAGE_TYPE_ACTIVITY.toLowerCase())) {
            return PAGE_TYPE_ACTIVITY.toLowerCase();
        } else if (superClazzName.contains(PAGE_TYPE_FRAGMENT.toLowerCase())) {
            return PAGE_TYPE_FRAGMENT.toLowerCase();
        } else if (superClazzName.contains(PAGE_TYPE_DIALOG.toLowerCase())) {
            return PAGE_TYPE_DIALOG.toLowerCase();
        }

        //这个父类的类型是类的类型，或者是接口的类型
        if (typeMirror instanceof DeclaredType) {
            Element element = ((DeclaredType) typeMirror).asElement();
            if (element instanceof TypeElement) {
                //递归调用
                return getDestinationType((TypeElement) element);
            }
        }

        return null;
    }


}
