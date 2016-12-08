package com.cout970.modeler.modelcontrol.action

import java.io.PrintWriter
import java.util.*

/**
 * Created by cout970 on 2016/12/08.
 */
class HistoryLog {

    private val log = LinkedList<Pair<Type, Any>>()

    fun onDo(action: IAction) {
        log += Type.DO to action
    }

    fun onUndo(action: IAction) {
        log += Type.UNDO to action
    }

    fun onRedo(action: IAction) {
        log += Type.REDO to action
    }

    fun writeLog(out: PrintWriter) {
        for (i in log) {
            out.println("${i.first} -> ${i.second}")
        }
    }

    enum class Type {
        DO,
        UNDO,
        REDO,
        BACKUP
    }
}