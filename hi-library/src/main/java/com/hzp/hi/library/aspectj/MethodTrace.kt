package com.hzp.hi.library.aspectj

@Target(AnnotationTarget.FUNCTION,AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.RUNTIME)
annotation class MethodTrace {
}