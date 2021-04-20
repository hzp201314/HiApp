package com.hzp.hi.library.taskflow

/**
 * 创建具体的任务
 */
interface ITaskCreator {
    fun createTask(taskName: String): Task
}