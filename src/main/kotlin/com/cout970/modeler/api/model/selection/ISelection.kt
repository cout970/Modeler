package com.cout970.modeler.api.model.selection

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

    fun isSelected(obj: IObjectRef): Boolean
    fun isSelected(obj: IFaceRef): Boolean
    fun isSelected(obj: IEdgeRef): Boolean
    fun isSelected(obj: IPosRef): Boolean
}

enum class SelectionTarget {
    MODEL,
    TEXTURE
}

enum class SelectionType {
    OBJECT,
    FACE,
    EDGE,
    VERTEX
}