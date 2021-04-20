package com.hzp.hi.library.taskflow

/**
 * 任务监听器
 */
interface TaskListener {
    fun onStart(task: Task)

    fun onRunning(task: Task)

    fun onFinished(task: Task)
}