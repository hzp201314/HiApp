package com.hzp.hiapp.demo.concurrent_demo.threadpool;

public interface Function<T, R> {
    R apply(T value);
}
