package com.cout970.modeler.modeleditor.clipboard

import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.HistoricalRecord
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.SelectionManager
import com.cout970.modeler.modeleditor.action.ActionModifyModelStructure
import com.cout970.modeler.selection.ElementSelection
import com.cout970.modeler.selection.elementSelection

/**
 * Created by cout970 on 2017/02/28.
 */

class ElementClipboard(
        val selectionManager: SelectionManager,
        val modelEditor: ModelEditor,
        val historyRecord: HistoricalRecord
) : IClipboard {

    var content: Pair<ElementSelection, Model>? = null

    override fun copy() {
        if (selectionManager.elementSelection != ElementSelection.EMPTY) {
            content = selectionManager.elementSelection to modelEditor.model.copy()
        }
    }

    override fun cut() {
        if (selectionManager.elementSelection != ElementSelection.EMPTY) {
            content = selectionManager.elementSelection to modelEditor.model.copy()
            val newModel = modelEditor.editTool.deleteElements(modelEditor.model, selectionManager.elementSelection)
            historyRecord.doAction(ActionModifyModelStructure(modelEditor, newModel))
        }
    }

    override fun paste() {
        if (content != null) {
            val (oldSelection, oldModel) = content!!
            val newModel = modelEditor.editTool.pasteElement(modelEditor.model, oldModel, oldSelection)
            historyRecord.doAction(ActionModifyModelStructure(modelEditor, newModel))
        }
    }

    override fun delete() {
        val newModel = modelEditor.editTool.deleteElements(modelEditor.model, selectionManager.elementSelection)
        historyRecord.doAction(ActionModifyModelStructure(modelEditor, newModel))
    }

    override fun clean() {
        content = null
    }
}