package com.cout970.modeler.newView.viewtarget

import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.newView.gui.ContentPanel
import com.cout970.modeler.newView.gui.Scene
import com.cout970.modeler.newView.selector.ISelectable

/**
 * Created by cout970 on 2017/04/08.
 */
class ModelViewTarget(modelEditor: ModelEditor, contentPanel: ContentPanel) : ViewTarget(modelEditor, contentPanel) {

    override val is3d: Boolean = true

    override fun getSelectableObjects(scene: Scene): List<ISelectable> = cursor.getSubParts(scene)
}