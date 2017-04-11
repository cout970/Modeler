package com.cout970.modeler.newView.viewtarget

import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.newView.gui.Scene
import com.cout970.modeler.newView.selector.ISelectable

/**
 * Created by cout970 on 2017/04/08.
 */
class ModelViewTarget(modelEditor: ModelEditor) : ViewTarget(modelEditor) {

    override val is3d: Boolean = true

    override fun getSelectableObjects(scene: Scene): List<ISelectable> = scene.cursor.getSubParts()
}