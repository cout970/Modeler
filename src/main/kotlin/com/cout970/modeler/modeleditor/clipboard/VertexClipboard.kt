package com.cout970.modeler.modeleditor.clipboard

import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.HistoricalRecord
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.SelectionManager
import com.cout970.modeler.modeleditor.action.ActionModifyModelStructure
import com.cout970.modeler.selection.VertexPosSelection
import com.cout970.modeler.selection.vertexPosSelection

/**
 * Created by cout970 on 2017/02/28.
 */

class VertexClipboard(
        val selectionManager: SelectionManager,
        val modelEditor: ModelEditor,
        val historyRecord: HistoricalRecord
) : IClipboard {

    var content: Pair<VertexPosSelection, Model>? = null

    override fun copy() {
        if (selectionManager.vertexPosSelection != VertexPosSelection.EMPTY) {
            content = selectionManager.vertexPosSelection to modelEditor.model.copy()
        }
    }

    override fun cut() {
        if (selectionManager.vertexPosSelection != VertexPosSelection.EMPTY) {
            content = selectionManager.vertexPosSelection to modelEditor.model.copy()
            val newModel = modelEditor.editTool.deleteFaces(modelEditor.model, selectionManager.vertexPosSelection)
            historyRecord.doAction(ActionModifyModelStructure(modelEditor, newModel))
        }
    }

    override fun paste() {
        if (content != null) {
            val (oldSelection, oldModel) = content!!
            val newModel = modelEditor.editTool.pasteFaces(modelEditor.model, oldModel, oldSelection)
            historyRecord.doAction(ActionModifyModelStructure(modelEditor, newModel))
        }
    }

    override fun delete() {
        val newModel = modelEditor.editTool.deleteFaces(modelEditor.model, selectionManager.vertexPosSelection)
        historyRecord.doAction(ActionModifyModelStructure(modelEditor, newModel))
    }

    override fun clean() {
        content = null
    }
}