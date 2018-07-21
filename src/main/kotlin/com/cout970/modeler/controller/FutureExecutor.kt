package com.cout970.modeler.controller

import com.cout970.modeler.Program
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.IUndoableTask
import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.util.ITickeable
import java.util.*

/**
 * Created by cout970 on 2017/07/17.
 */

interface IFutureExecutor {
    fun doTask(task: ITask)
    fun undoTask(task: IUndoableTask)
}

class FutureExecutor : ITickeable, IFutureExecutor {

    lateinit var programState: Program
    private val queue = LinkedList<Pair<ITask, (Program) -> Unit>>()

    override fun doTask(task: ITask) {
        synchronized(queue) {
            queue.add(task to task::run)
        }
    }

    override fun undoTask(task: IUndoableTask) {
        synchronized(queue) {
            queue.add(task to task::undo)
        }
    }

    override fun tick() = Unit

    override fun postTick() {
        Profiler.startSection("runTasks")
        synchronized(queue) {
            if (queue.isNotEmpty()) {
                var index = 0
                while (queue.isNotEmpty()) {
                    val (task, func) = queue.poll()
                    Profiler.startSection("""${task.javaClass.simpleName}${index++}""")
                    func.invoke(programState)
                    Profiler.endSection()
                }
            }
        }
        Profiler.endSection()
    }
}