package com.cout970.modeler.core.record

import com.cout970.modeler.core.record.action.IAction
import java.util.*

/**
 * Created by cout970 on 2016/12/08.
 */
@Deprecated("Use TaskHistory instead")
class HistoricalRecord(val historyLog: HistoryLog, val enqueue: (() -> Unit) -> Unit) {

    private val actionStack = Stack<IAction>()
    private val redoStack = Stack<IAction>()

    fun doAction(action: IAction) {
        actionStack += action
        redoStack.clear()
        enqueue {
            action.run()
            historyLog.onDo(action)
        }
    }

    fun undo() {
        if (actionStack.isEmpty()) return
        val action = actionStack.pop()
        redoStack += action
        enqueue {
            action.undo()
            historyLog.onUndo(action)
        }
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val action = redoStack.pop()
        actionStack += action
        enqueue {
            action.run()
            historyLog.onRedo(action)
        }
    }
}