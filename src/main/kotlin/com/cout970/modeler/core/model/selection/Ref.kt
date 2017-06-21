package com.cout970.modeler.core.model.selection

import com.cout970.modeler.api.model.selection.IEdgeRef
import com.cout970.modeler.api.model.selection.IFaceRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.IPosRef

/**
 * Created by cout970 on 2017/05/14.
 */
data class ObjectRef(
        override val objectIndex: Int
) : IObjectRef

data class FaceRef(
        override val objectIndex: Int,
        override val faceIndex: Int
) : IFaceRef

data class EdgeRef(
        override val objectIndex: Int,
        override val firstIndex: Int,
        override val secondIndex: Int
) : IEdgeRef

data class PosRef(
        override val objectIndex: Int,
        override val posIndex: Int
) : IPosRef