package com.cout970.modeler.model

import com.cout970.modeler.modelcontrol.ISelectable
import com.cout970.modeler.modelcontrol.SelectionMode

/**
 * Created by cout970 on 2016/11/29.
 */

class Model() {

    val objects = mutableListOf<ModelObject>()

    fun getGroups() = objects.map { it.groups }.flatten()

    fun getComponents() = objects.map { it.getComponents() }.flatten()
}

class ModelObject() : ISelectable {

    val transform = Transformation.IDENTITY
    val groups = mutableListOf<ModelGroup>()

    fun getComponents() = groups.map { it.components }.flatten()

    override fun canBeSelected(mode: SelectionMode): Boolean = mode == SelectionMode.OBJECT
}

class ModelGroup() : ISelectable {

    val transform = Transformation.IDENTITY
    val components = mutableListOf<ModelComponent>()

    override fun canBeSelected(mode: SelectionMode): Boolean = mode == SelectionMode.OBJECT
}
