package com.hzp.common.core

/**
 * 通信桥接
 * @P Flutter、RN、H5
 *
 */
interface IHiBridge<P,Callback> {
    /*返回上一页*/
    fun onBack(p: P?)
    /*调转原生页面*/
    fun goToNative(p: P)
    /*获取原生header信息*/
    fun getHeaderParams(callback: Callback)
}