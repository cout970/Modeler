package com.cout970.modeler.modeleditor.tool

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Selection
import com.cout970.modeler.model.applyVertex
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

    override fun translate(source: Model, selection: Selection, translation: IVector3): Model {
        return source.applyVertex(selection) { path, vertex ->
            vertex.copy(pos = vertex.pos + translation)
        }
    }

    override fun rotate(source: Model, selection: Selection, pivot: IVector3, rotation: IQuaternion): Model {
        return source.applyVertex(selection) { path, vertex ->
            vertex.copy(pos = vertex.pos.rotateAround(pivot, rotation))
        }
    }

    override fun scale(source: Model, selection: Selection, center: IVector3, axis: SelectionAxis,
                       offset: Float): Model {

        return source.applyVertex(selection) { path, vertex ->
            vertex.copy(pos = vertex.pos.scale(center, axis, offset))
        }
    }
}