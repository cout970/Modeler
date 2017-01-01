package com.cout970.modeler.modeleditor

import com.cout970.modeler.log.Level
import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.action.IAction
import java.util.*
import com.cout970.modeler.log.log as logger

/**
 * Created by cout970 on 2016/12/08.
 */
class HistoryLog {

    private val log = LinkedList<Pair<Type, Any>>()

    fun onDo(action: IAction) {
        log += Type.DO to action
        com.cout970.modeler.log.log(Level.FINEST) { "${Type.DO} -> $action" }
    }

    fun onUndo(action: IAction) {
        log += Type.UNDO to action
        com.cout970.modeler.log.log(Level.FINEST) { "${Type.UNDO} -> $action" }
    }

    fun onRedo(action: IAction) {
        log += Type.REDO to action
        com.cout970.modeler.log.log(Level.FINEST) { "${Type.REDO} -> $action" }
    }

    enum class Type {
        DO,
        UNDO,
        REDO,
        MODEL_CHANGE,
        BACKUP
    }

    fun onModelChange(newModel: Model, oldModel: Model) {
        log += Type.MODEL_CHANGE to Pair(newModel, oldModel)
        com.cout970.modeler.log.log(Level.FINEST) { "${Type.MODEL_CHANGE} -> ${Pair(newModel, oldModel)}" }
    }
}