package com.cout970.modeler.modeleditor.tool

import com.cout970.modeler.model.freemodel.FreeModel
import com.cout970.modeler.model.freemodel.Selection
import com.cout970.modeler.model.freemodel.apply
import com.cout970.modeler.util.rotateAround
import com.cout970.modeler.util.scale
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.plus

/**
 * Created by cout970 on 2017/02/11.
 */
class EditTool : IModelTranslate, IModelRotate, IModelScale {

    override fun translate(source: FreeModel, selection: Selection, translation: IVector3): FreeModel {
        return source.apply(selection) { path, vertex ->
            vertex.copy(pos = vertex.pos + translation)
        }
    }

    override fun rotate(source: FreeModel, selection: Selection, pivot: IVector3, rotation: IQuaternion): FreeModel {
        return source.apply(selection) { path, vertex ->
            vertex.copy(pos = vertex.pos.rotateAround(pivot, rotation))
        }
    }

    override fun scale(source: FreeModel, selection: Selection, center: IVector3, axis: SelectionAxis,
                       offset: Float): FreeModel {

        return source.apply(selection) { path, vertex ->
            vertex.copy(pos = vertex.pos.scale(center, axis, offset))
        }
    }
}