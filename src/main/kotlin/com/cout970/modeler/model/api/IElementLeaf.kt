package com.cout970.modeler.model.api

import com.cout970.modeler.selection.VertexPath
import com.cout970.modeler.selection.VertexPosSelection
import com.cout970.modeler.selection.VertexTexSelection
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

interface IElementLeaf : IElement {

    val positions: List<IVector3>
    val textures: List<IVector2>
    val faces: List<QuadIndex>

    fun transformPos(selection: VertexPosSelection, func: (VertexPath, IVector3) -> IVector3): IElement
    fun transformTex(selection: VertexTexSelection, func: (VertexPath, IVector2) -> IVector2): IElement
    fun removeFaces(faces: List<Int>): IElementLeaf
}

