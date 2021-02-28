package com.hzp.hi.library.restful

import java.lang.reflect.Type

/**
 * 返回数据解析接口
 */
interface HiConvert {
    /**
     * 数据转换为HiResponse<T>
     * @rawData:原始数据类型
     * @dataType:HiResponse中data数据类型
     */
    fun <T> convert(rawData: String, dataType: Type):HiResponse<T>
}