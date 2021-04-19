package com.hzp.hiapp.demo.uml.jetpack;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.SuperNotCalledException;
//import androidx.lifecycle.Lifecycle;
//import androidx.lifecycle.LifecycleObserver;
//import androidx.lifecycle.LifecycleOwner;
//import androidx.lifecycle.OnLifecycleEvent;

public class test {
}
////法一
////1.自定义的LifecycleObserver观察者，用注解声明每个方法观察的宿主的状态
class LocationObserver implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart(@NonNull LifecycleOwner owner){
        //开启定位
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop(@NonNull LifecycleOwner owner){
        //停止定位
    }
}
//
////2.注册观察者，观察宿主 生命周期状态变化
//class MyFragment extends Fragment{
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        LocationObserver observer = new LocationObserver();
//        getLifecycle().addObserver(observer);
//    }
//}

////法二
////FullLifecycleObserver
//interface FullLifecycleObserver extends LifecycleObserver {
//    void onCreate(LifecycleOwner owner);
//    void onStart(LifecycleOwner owner);
//    void onResume(LifecycleOwner owner);
//    void onPause(LifecycleOwner owner);
//    void onStop(LifecycleOwner owner);
//    void onDestroy(LifecycleOwner owner);
//}
//
//class LocationObserver implements FullLifecycleObserver {
//
//    public void onCreate(LifecycleOwner owner) {
//
//    }
//
//    public void onStart(LifecycleOwner owner) {
//
//    }
//
//    public void onResume(LifecycleOwner owner) {
//
//    }
//
//    public void onPause(LifecycleOwner owner) {
//
//    }
//
//    public void onStop(LifecycleOwner owner) {
//
//    }
//
//    public void onDestroy(LifecycleOwner owner) {
//
//    }
//}

////法三
////LifecycleEventObserver
//interface  LifecycleEventObserver extends LifecycleObserver{
//    void onStateChanged(LifecycleOwner source,Lifecycle.Event event);
//}
//
//class LocationObserver implements LifecycleEventObserver{
//
//    @Override
//    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
//        //需要自行判断Lifecycle.Event 是onStart() 还是onStop()
//    }
//}

//public class Fragment implements LifecycleOwner{
//
//    @Override
//    public Lifecycle getLifecycle() {
//        return mLifecycleRegistry;
//    }
//
//    void performCreate(Bundle savedInstanceState) {
//        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
//    }
//
//    void performStart() {
//        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
//
//    }
//    ...
//    void performResume() {
//        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
//    }
//}


