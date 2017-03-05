package com.cout970.modeler.selection.subselection

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.util.getVertexPos
import com.cout970.modeler.model.util.getVertexTex
import com.cout970.modeler.model.util.middle
import com.cout970.modeler.selection.EdgePath
import com.cout970.modeler.selection.ElementSelection
import com.cout970.modeler.selection.FacePath
import com.cout970.modeler.selection.VertexPath
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/03/05.
 */
interface ISubSelection {

    val pathList: List<VertexPath>

    fun isSelected(path: VertexPath): Boolean
    fun isSelected(path: EdgePath): Boolean
    fun isSelected(path: FacePath): Boolean

    fun center3D(model: Model): IVector3 {
        return pathList.map { model.getVertexPos(it) }.middle()
    }

    fun center2D(model: Model): IVector2 {
        return pathList.map { model.getVertexTex(it) }.middle()
    }

    fun toElementSelection(): ElementSelection {
        return ElementSelection(pathList.map { it.elementPath }.distinct())
    }
}