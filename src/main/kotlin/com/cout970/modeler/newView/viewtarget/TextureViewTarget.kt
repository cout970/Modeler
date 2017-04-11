package com.cout970.modeler.newView.viewtarget

import com.cout970.modeler.model.material.MaterialNone
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.newView.gui.Scene
import com.cout970.modeler.newView.selector.ISelectable
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/04/09.
 */

class TextureViewTarget(modelEditor: ModelEditor) : ViewTarget(modelEditor) {

    override val is3d: Boolean = false

    fun fromTextureToWorld(point: IVector2): IVector3 {
        val texture = modelEditor.model.resources.materials.firstOrNull() ?: MaterialNone
        val size = texture.size
        val offset = size / 2

        val min = vec2Of(-offset.xi * (size.xi / size.xi), -offset.yi)
        val max = vec2Of(-offset.xi + size.xi * (size.xi / size.xi), size.yi - offset.yi)

        return vec3Of(min.xd + (max.xd - min.xd) * point.xd, min.yd + (max.yd - min.yd) * (1 - point.yd), 0)
    }

    override fun getSelectableObjects(scene: Scene): List<ISelectable> = scene.cursor.getSubParts()
}