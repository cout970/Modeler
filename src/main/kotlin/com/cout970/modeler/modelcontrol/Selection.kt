package com.cout970.modeler.modelcontrol

import com.cout970.modeler.model.ModelComponent

/**
 * Created by cout970 on 2016/12/07.
 */
abstract class Selection {

    abstract val mode: SelectionMode
    abstract val paths: List<ModelPath>
}

object SelectionNone : Selection() {

    override val mode: SelectionMode = SelectionMode.OBJECT
    override val paths: List<ModelPath> = emptyList()
}

class SelectionComponent(val components: List<ModelPath>) : Selection() {

    override val mode: SelectionMode = SelectionMode.COMPONENT
    override val paths: List<ModelPath> get() = components

    fun isSelected(component: ModelComponent): Boolean {
        return components.any { it.component == component }
    }
}