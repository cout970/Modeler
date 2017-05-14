package com.cout970.modeler.core.clipboard

import com.cout970.modeler.core.record.HistoricalRecord
import com.cout970.modeler.core.record.action.ActionModifyModelStructure
import com.cout970.modeler.to_redo.model.Model
import com.cout970.modeler.to_redo.modeleditor.ModelEditor
import com.cout970.modeler.to_redo.modeleditor.SelectionManager
import com.cout970.modeler.to_redo.selection.ElementSelection
import com.cout970.modeler.to_redo.selection.elementSelection

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