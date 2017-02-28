package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.selection.VertexTexSelection

/**
 * Created by cout970 on 2017/01/29.
 */
class ActionChangeTextureSelection(val oldSelection: VertexTexSelection, val newSelection: VertexTexSelection,
                                   val modelEditor: ModelEditor) : IAction {

    override fun run() {
        modelEditor.selectionManager.vertexTexSelection = newSelection
    }

    override fun undo() {
        modelEditor.selectionManager.vertexTexSelection = oldSelection
    }

    override fun toString(): String {
        return "ActionChangeTextureSelection(oldSelection=$oldSelection, newSelection=$newSelection)"
    }
}