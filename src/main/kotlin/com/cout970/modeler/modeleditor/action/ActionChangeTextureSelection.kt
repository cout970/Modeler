package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.selection.ITextureSelection

/**
 * Created by cout970 on 2017/01/29.
 */
class ActionChangeTextureSelection(val oldSelection: ITextureSelection, val newSelection: ITextureSelection,
                                   val modelEditor: ModelEditor) : IAction {

    override fun run() {
        modelEditor.selectionManager.textureSelection = newSelection
    }

    override fun undo() {
        modelEditor.selectionManager.textureSelection = oldSelection
    }

    override fun toString(): String {
        return "ActionChangeTextureSelection(oldSelection=$oldSelection, newSelection=$newSelection)"
    }
}