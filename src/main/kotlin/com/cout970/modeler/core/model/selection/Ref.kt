package com.cout970.modeler.core.model.selection

import com.cout970.modeler.api.model.selection.IEdgeRef
import com.cout970.modeler.api.model.selection.IFaceRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.IPosRef
import com.cout970.modeler.core.model.`object`.ObjectNone
import java.util.*

/**
 * Created by cout970 on 2017/05/14.
 */

object ObjectRefNone: IObjectRef{
    override val objectId: UUID = ObjectNone.id
}

data class ObjectRef(
        override val objectId: UUID
) : IObjectRef

data class FaceRef(
        override val objectId: UUID,
        override val faceIndex: Int
) : IFaceRef

data class EdgeRef(
        override val objectId: UUID,
        override val firstIndex: Int,
        override val secondIndex: Int
) : IEdgeRef

data class PosRef(
        override val objectId: UUID,
        override val posIndex: Int
) : IPosRef