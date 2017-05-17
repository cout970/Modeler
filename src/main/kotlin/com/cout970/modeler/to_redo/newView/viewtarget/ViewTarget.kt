package com.cout970.modeler.to_redo.newView.viewtarget

import com.cout970.modeler.to_redo.model.Model
import com.cout970.modeler.to_redo.modeleditor.ModelEditor
import com.cout970.modeler.to_redo.newView.gui.ContentPanel
import com.cout970.modeler.to_redo.newView.gui.Scene
import com.cout970.modeler.to_redo.newView.selector.Cursor
import com.cout970.modeler.to_redo.newView.selector.ISelectable
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/04/08.
 */
abstract class ViewTarget(val modelEditor: ModelEditor, val contentPanel: ContentPanel) {

    val cursor: Cursor = Cursor(contentPanel, modelEditor)
    var tmpCursorCenter: IVector3? = null

    var hoveredObject: ISelectable? = null
    var selectedObject: ISelectable? = null

    abstract val is3d: Boolean

    var tmpModel: Model? = null

    fun getModel(): Model = tmpModel ?: modelEditor.model

    abstract fun getSelectableObjects(scene: Scene): List<ISelectable>

    fun hashSelection(): Int = (hoveredObject?.hashCode() ?: 0xFEA) xor ((selectedObject?.hashCode() ?: 0xFEA) shl 1)
}