package com.hzp.hi.library.taskflow

/**
 * 启动阶段 需要初始化的任务，在taskflow中 对应着一个Task
 */
abstract class Task @JvmOverloads constructor(
    /**任务名称**/
    val id: String,
    /**是否是异步任务**/
    val isAsyncTask: Boolean = false,
    /**延迟执行的时间**/
    val delayMills: Long = 0,
    /**任务的优先级**/
    var priority: Int = 0
) {
}