package com.cout970.modeler.core.model.selection

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.IFaceSelection
import com.cout970.modeler.api.model.selection.IObjectSelection
import com.cout970.modeler.api.model.selection.IPosSelection
import com.cout970.modeler.api.model.selection.ITexSelection

/**
 * Created by cout970 on 2017/05/14.
 */
open class ObjectSelection(
        override val objectIndex: Int
) : IObjectSelection {
    override fun toPosSelection(model: IModel): List<IPosSelection> {
        return (0 until model.objects[objectIndex].mesh.pos.size).map { PosSelection(objectIndex, it) }
    }
}

class FaceSelection(
        objectIndex: Int,
        override val faceIndex: Int
) : ObjectSelection(objectIndex), IFaceSelection {
    override fun toPosSelection(model: IModel): List<IPosSelection> = TODO()
}

class PosSelection(
        objectIndex: Int,
        override val posIndex: Int
) : ObjectSelection(objectIndex), IPosSelection {
    override fun toPosSelection(model: IModel): List<IPosSelection> = listOf(this)
}

class TexSelection(
        objectIndex: Int,
        override val texIndex: Int
) : ObjectSelection(objectIndex), ITexSelection {
    override fun toPosSelection(model: IModel): List<IPosSelection> = emptyList()
}
