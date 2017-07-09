package com.cout970.modeler.to_redo.model.api

interface IElementGroup : IElement {

    val elements: List<IElement>
    val name: String

    fun deepCopy(elements: List<IElement>): IElementGroup
}