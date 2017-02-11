package com.cout970.modeler.modeleditor

import com.cout970.modeler.model.SelectionNone
import com.cout970.modeler.modeleditor.action.ActionModifyModel

/**
 * Created by cout970 on 2017/02/11.
 */
class ModelTexturizer(val editor: ModelEditor) {

    fun splitTextures() {
        if (editor.selectionManager.textureSelection != SelectionNone) {
            val newModel = editor.model.splitUV(editor.selectionManager.textureSelection)
            editor.historyRecord.doAction(ActionModifyModel(editor, newModel))
        }
    }
}