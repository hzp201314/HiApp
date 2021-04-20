package com.hzp.hi.library.taskflow

import androidx.core.os.TraceCompat
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.ArrayList

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
) :Runnable,Comparable<Task> {

    //任务执行时间
    var executeTime:Long = 0
        protected set

    //任务的状态
    var state:Int = TaskState.IDLE
        protected set

    //当前task 依赖了那些前置任务，
    // 只有当dependTasks集合中的所有任务执行完，当前才可以执行
    val dependTasks:MutableList<Task> = ArrayList()

    //用于运行时log 统计输出，输出当前task  依赖了那些前置任务，这些前置任务的名称 我们将它存储在这里
    val dependTasksName: MutableList<String> = ArrayList()

    //当前task 被那些后置任务依赖，只有当当前这个task执行完，behindTasks集合中的后置任务才可以执行
    val behindTasks: MutableList<Task> = ArrayList()

    //任务运行状态监听器集合
    private val taskListeners: MutableList<TaskListener> = ArrayList()

    //用于输出task 运行时的日志
    private var taskRuntimeListener: TaskRuntimeListener?= TaskRuntimeListener()


    /*添加任务监听器*/
    open fun addTaskListener(taskListener: TaskListener){
        if(!taskListeners.contains(taskListener)){
            taskListeners.add(taskListener)
        }
    }


    open fun start(){
        //是否已经启动过
        if(state!=TaskState.IDLE){
            throw RuntimeException("cannot run task $id again")
        }

        toStart()
        executeTime = System.currentTimeMillis()
        //执行当前任务
        TaskRuntime.executeTask(this)
    }

    private fun toStart() {
        state=TaskState.START
        TaskRuntime.setStateInfo(this)
        for (listener in taskListeners) {
            listener.onStart(this)
        }
        taskRuntimeListener?.onStart(this)
    }

    override fun run() {
        //改变任务的状态--onstart  onrunning onfinshed --通知后置任务去开始执行
        TraceCompat.beginSection(id)

        //运行状态
        toRunning()

        //真正的执行 初始化任务的代码的方法
        run(id)

        //结束状态
        toFinish()

        //通知它的后置任务去执行
        notifyBehindTasks()

        //回收数据对象
        recycle()

        TraceCompat.endSection()
    }

    private fun recycle() {
        dependTasks.clear()
        behindTasks.clear()
        taskListeners.clear()
        taskRuntimeListener = null
    }

    private fun notifyBehindTasks() {
        //通知后置任务去尝试执行
        if(behindTasks.isNotEmpty()){
            if(behindTasks.size>1){
                Collections.sort(behindTasks,TaskRuntime.taskComparator)
            }

            //遍历behindTask后置任务，通知他们，告诉他们你的一个前置依赖任务已经执行完成了
            for (behindTask in behindTasks) {
                // A behindTasks ->(B,C)  A执行完成之后，B,C才可以执行。
                behindTask.dependTaskFinished(this)
            }
        }
    }

    /**
     * 通知后置任务，依赖的前置任务执行完了
     */
    private fun dependTaskFinished(dependTask: Task) {
        // A behindTasks ->(B,C)  A执行完成之后，B,C才可以执行。
        // task= B,C ,dependTask=A
        if(behindTasks.isEmpty()){
            return
        }

        //把A从  B,C的前置依赖任务 集合中移除
        dependTasks.remove(dependTask)

        //B,C的所有前置任务 是否都执行完了，如果是执行B,C
        if(dependTasks.isEmpty()){
            start()
        }
    }

    //给当前task 添加一个 前置的依赖任务
    open fun dependOn(dependTask: Task){
        var task =dependTask
        if(task!=this){
            if(dependTask is Project){
                task = dependTask.endTask
            }
            dependTasks.add(task)

            //这里就不remove了 ，因为这样可以把 endTask 前面所有的依赖路径都收集起来
            //比如 start -- task1 --task2 --task3 --end
            //    start -- task4 --task5 --task6 --end
            //此时endTask 是依赖了前面所有的任务
            //dependTasksName.add(task.id)

            //当前task 依赖了dependTask ,那么我们还需要吧dependTask-里面的behindTask 添加进去当前的task
            if(!task.behindTasks.contains(this)){
                task.behindTasks.add(this)
            }
        }
    }

    //给当前task 移除一个前置依赖任务
    open fun removeDependence(dependTask: Task){
        var task = dependTask
        if(dependTask!=this){
            if(dependTask is Project){
                task = dependTask.endTask
            }
            dependTasks.remove(task)
            dependTasksName.add(task.id)

            //把当前task 从dependTask的 后置依赖任务集合behindTasks中移除
            //达到接触 两个任务依赖关系的目的
            if(task.behindTasks.contains(this)){
                task.behindTasks.remove(this)
            }
        }
    }

    //给当前任务添加后置依赖项
    //它和 dependOn 是相反的
    open fun behind(behindTask: Task){
        var task = behindTask
        if(behindTask!=this){
            if(behindTask is Project){
                task= behindTask.startTask
            }

            //这个是把behindTask  添加到当前task的后面
            behindTasks.add(task)

            //把当前task 添加到 behindTask 的前面
            behindTask.dependOn(this)
        }
    }

    //给当前task 移除一个 后置的任务
    open fun removeBehind(behindTask: Task){
        var task = behindTask
        if(behindTask!=this){
            if(behindTask is Project){
                task= behindTask.startTask
            }
            behindTasks.remove(task)

            behindTask.removeDependence(this)
        }
    }

    private fun toFinish() {
        state = TaskState.FINISHED
        TaskRuntime.setStateInfo(this)
        TaskRuntime.removeBlockTask(this.id)
        for (listener in taskListeners) {
            listener.onFinished(this)
        }
        taskRuntimeListener?.onFinished(this)
    }

    private fun toRunning() {
        state = TaskState.RUNNING
        TaskRuntime.setStateInfo(this)
        TaskRuntime.setThreadName(this, Thread.currentThread().name)
        for (listener in taskListeners) {
            listener.onRunning(this)
        }
        taskRuntimeListener?.onRunning(this)
    }

    abstract fun run(id: String)

    override fun compareTo(other: Task): Int {
        return Utils.compareTask(this, other)
    }


}