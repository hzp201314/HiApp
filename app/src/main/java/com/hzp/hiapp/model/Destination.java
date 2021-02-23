package com.hzp.hiapp.model;

public class Destination {
    public String pageUrl;      //页面url
    public int id;              //路由节点（页面）id
    public boolean asStarter;   //是否作为路由的第一个启动页
    public String destType;     //路由节点(页面)类型,activity,dialog,fragment
    public String clazzName;    //全类名
}
