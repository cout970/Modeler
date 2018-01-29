package com.cout970.modeler.api.model.selection

import com.cout970.modeler.core.model.selection.EdgeRef
import com.cout970.modeler.core.model.selection.FaceRef
import com.cout970.modeler.core.model.selection.PosRef
import java.util.*

/**
 * Created by cout970 on 2017/05/14.
 */

interface IRef

interface IObjectRef : IRef {
    val objectId: UUID
}

interface IFaceRef : IObjectRef {
    val faceIndex: Int
}

interface IEdgeRef : IObjectRef {
    val firstIndex: Int
    val secondIndex: Int
}

interface IPosRef : IObjectRef {
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
    is IObjectRef -> SelectionType.OBJECT
    is IFaceRef -> SelectionType.FACE
    is IEdgeRef -> SelectionType.EDGE
    else -> SelectionType.VERTEX
}

inline fun UUID.toFaceRef(index: Int) = FaceRef(this, index)
inline fun UUID.toEdgeRef(a: Int, b: Int) = EdgeRef(this, a, b)
inline fun UUID.toPosRef(index: Int) = PosRef(this, index)

inline fun IObjectRef.toFaceRef(index: Int) = FaceRef(this.objectId, index)
inline fun IObjectRef.toEdgeRef(a: Int, b: Int) = EdgeRef(this.objectId, a, b)
inline fun IObjectRef.toPosRef(index: Int) = PosRef(this.objectId, index)

@Deprecated("Not needed anymore") inline val IFaceRef.objectRef: IObjectRef get() = TODO("REMOVE this function")
@Deprecated("Not needed anymore") inline val IEdgeRef.objectRef: IObjectRef get() = TODO("REMOVE this function")
@Deprecated("Not needed anymore") inline val IPosRef.objectRef: IObjectRef get() =  TODO("REMOVE this function")