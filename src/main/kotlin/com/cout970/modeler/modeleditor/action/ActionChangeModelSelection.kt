package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.selection.ElementSelection
import com.cout970.modeler.selection.VertexPosSelection
import com.cout970.modeler.selection.VertexTexSelection

/**
 * Created by cout970 on 2016/12/08.
 */
data class ActionChangeModelSelection(

        var oldElementSelection: ElementSelection,
        var oldVertexPosSelection: VertexPosSelection,
        var oldVertexTexSelection: VertexTexSelection,

        var newElementSelection: ElementSelection,
        var newVertexPosSelection: VertexPosSelection,
        var newVertexTexSelection: VertexTexSelection,

        val modelEditor: ModelEditor) : IAction {

    override fun run() {
        modelEditor.selectionManager.elementSelection = newElementSelection
        modelEditor.selectionManager.vertexPosSelection = newVertexPosSelection
        modelEditor.selectionManager.vertexTexSelection = newVertexTexSelection
    }

    override fun undo() {
        modelEditor.selectionManager.elementSelection = oldElementSelection
        modelEditor.selectionManager.vertexPosSelection = oldVertexPosSelection
        modelEditor.selectionManager.vertexTexSelection = oldVertexTexSelection
    }

    override fun toString(): String {
        return "ActionChangeModelSelection(" +
               "ElementSelection=$newElementSelection" +
               "VertexPosSelection=$newVertexPosSelection" +
               "VertexTexSelection=$newVertexTexSelection" +
               ")"
    }
}
