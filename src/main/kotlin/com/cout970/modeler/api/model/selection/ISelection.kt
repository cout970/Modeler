package com.cout970.modeler.api.model.selection

import com.cout970.modeler.core.model.selection.EdgeRef
import com.cout970.modeler.core.model.selection.FaceRef
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.core.model.selection.PosRef
import java.util.*

/**
 * Created by cout970 on 2017/05/14.
 */

interface IRef

interface IObjectRef : IRef {
    val objectId: UUID
}

interface IFaceRef : IRef {
    val objectId: UUID
    val faceIndex: Int
}

interface IEdgeRef : IRef {
    val objectId: UUID
    val firstIndex: Int
    val secondIndex: Int
}

interface IPosRef : IRef {
    val objectId: UUID
    val posIndex: Int
}

interface ISelection {

    val selectionTarget: SelectionTarget
    val selectionType: SelectionType
    val size: Int
    val refs: Set<IRef>

    fun isSelected(obj: IObjectRef): Boolean
    fun isSelected(obj: IFaceRef): Boolean
    fun isSelected(obj: IEdgeRef): Boolean
    fun isSelected(obj: IPosRef): Boolean
}

enum class SelectionTarget(val is3D: Boolean) {
    MODEL(true),
    TEXTURE(false),
    ANIMATION(true)
}

enum class SelectionType {
    OBJECT,
    FACE,
    EDGE,
    VERTEX
}

fun IRef.getSelectionType() = when (this) {
    is IFaceRef -> SelectionType.FACE
    is IEdgeRef -> SelectionType.EDGE
    is IPosRef -> SelectionType.VERTEX
    else -> SelectionType.OBJECT
}

inline fun IFaceRef.toObjectRef() = ObjectRef(objectId)
inline fun IEdgeRef.toObjectRef() = ObjectRef(objectId)
inline fun IPosRef.toObjectRef() = ObjectRef(objectId)

inline fun IObjectRef.toFaceRef(index: Int) = FaceRef(this.objectId, index)
inline fun IObjectRef.toEdgeRef(a: Int, b: Int) = EdgeRef(this.objectId, a, b)
inline fun IObjectRef.toPosRef(index: Int) = PosRef(this.objectId, index)