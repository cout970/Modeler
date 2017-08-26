package com.cout970.modeler.controller

import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.IUndoableTask
import java.util.*

/**
 * Created by cout970 on 2017/07/17.
 */

interface ITaskProcessor {
    fun processTask(task: ITask)
}

class TaskHistory(val delegate: IFutureExecutor) : ITaskProcessor {

    private val taskStack = Stack<IUndoableTask>()
    private val redoStack = Stack<IUndoableTask>()

    override fun processTask(task: ITask) {
        if (task is IUndoableTask) {
            taskStack += task
            redoStack.clear()
        }
        delegate.addToQueue(task::run)
    }

    fun undo() {
        if (taskStack.isEmpty()) return
        val task = taskStack.pop()
        redoStack += task
        delegate.addToQueue(task::undo)
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val task = redoStack.pop()
        taskStack += task
        delegate.addToQueue(task::run)
    }
}

