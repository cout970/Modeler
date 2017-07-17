package com.cout970.modeler.core.record

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.record.action.IAction
import java.util.*
import com.cout970.modeler.core.log.log as logger

/**
 * Created by cout970 on 2016/12/08.
 */
@Deprecated("to be deleted")
class HistoryLog {

    private val log = LinkedList<Pair<Type, Any>>()

    fun onDo(action: IAction) {
        log += Type.DO to action
        logger(Level.FINEST) { "${Type.DO} -> $action" }
    }

    fun onUndo(action: IAction) {
        log += Type.UNDO to action
        logger(Level.FINEST) { "${Type.UNDO} -> $action" }
    }

    fun onRedo(action: IAction) {
        log += Type.REDO to action
        logger(Level.FINEST) { "${Type.REDO} -> $action" }
    }

    enum class Type {
        DO,
        UNDO,
        REDO,
        MODEL_CHANGE,
        BACKUP
    }

    //TODO
//    fun onModelChange(newModel: Model, oldModel: Model) {
//        log += Type.MODEL_CHANGE to Pair(newModel, oldModel)
//        logger(Level.FINEST) { "${Type.MODEL_CHANGE} -> ${Pair(newModel, oldModel)}" }
//    }
}