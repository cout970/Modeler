package com.cout970.modeler.api.model.selection

import com.cout970.modeler.core.model.selection.ObjectRef

/**
 * Created by cout970 on 2017/05/14.
 */

interface IRef

interface IObjectRef : IRef {
    val objectIndex: Int
}

interface IFaceRef : IRef {
    val objectIndex: Int
    val faceIndex: Int
}

interface IEdgeRef : IRef {
    val objectIndex: Int
    val firstIndex: Int
    val secondIndex: Int
}

interface IPosRef : IRef {
    val objectIndex: Int
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

inline val IFaceRef.objectRef: IObjectRef get() = ObjectRef(objectIndex)
inline val IEdgeRef.objectRef: IObjectRef get() = ObjectRef(objectIndex)
inline val IPosRef.objectRef: IObjectRef get() = ObjectRef(objectIndex)