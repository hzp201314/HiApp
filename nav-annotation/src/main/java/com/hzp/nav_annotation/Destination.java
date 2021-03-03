package com.hzp.nav_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface Destination {

    String pageUrl();

    boolean asStarter() default false;
}
