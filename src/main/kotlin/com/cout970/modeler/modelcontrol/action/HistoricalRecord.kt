package com.cout970.modeler.modelcontrol.action

import com.cout970.modeler.modelcontrol.ModelController
import java.util.*

/**
 * Created by cout970 on 2016/12/08.
 */
class HistoricalRecord(val historyLog: HistoryLog, val modelController: ModelController) {

    private val actionStack = Stack<IAction>()
    private val redoStack = Stack<IAction>()

    fun doAction(action: IAction) {
        actionStack += action
        redoStack.clear()
        modelController.addToQueue {
            action.run()
            historyLog.onDo(action)
        }
    }

    fun undo() {
        if (actionStack.isEmpty()) return
        val action = actionStack.pop()
        redoStack += action
        modelController.addToQueue {
            action.undo()
            historyLog.onUndo(action)
        }
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val action = redoStack.pop()
        actionStack += action
        modelController.addToQueue {
            action.run()
            historyLog.onRedo(action)
        }
    }
}