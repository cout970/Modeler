package com.cout970.modeler.modelcontrol

import com.cout970.modeler.model.ModelComponent
import com.cout970.modeler.model.ModelGroup
import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.Vertex

/**
 * Created by cout970 on 2016/12/07.
 */
abstract class Selection {

    abstract val mode: SelectionMode
    abstract val paths: List<ModelPath>
}

object SelectionNone : Selection() {

    override val mode: SelectionMode = SelectionMode.GROUP
    override val paths: List<ModelPath> = emptyList()
}

data class SelectionGroup(val group: List<ModelPath>) : Selection() {

    override val mode: SelectionMode = SelectionMode.GROUP
    override val paths: List<ModelPath> get() = group

    fun isSelected(component: ModelGroup): Boolean {
        return group.any { it.group == component }
    }
}

data class SelectionComponent(val components: List<ModelPath>) : Selection() {

    override val mode: SelectionMode = SelectionMode.COMPONENT
    override val paths: List<ModelPath> get() = components

    fun isSelected(component: ModelComponent): Boolean {
        return components.any { it.component == component }
    }
}

data class SelectionQuad(val quads: List<ModelPath>) : Selection() {

    override val mode: SelectionMode = SelectionMode.QUAD
    override val paths: List<ModelPath> get() = quads

    fun isSelected(component: Quad): Boolean {
        return quads.any { it.quad == component }
    }
}


data class SelectionVertex(val vertex: List<ModelPath>) : Selection() {

    override val mode: SelectionMode = SelectionMode.VERTEX
    override val paths: List<ModelPath> get() = vertex

    fun isSelected(component: Vertex): Boolean {
        return vertex.any { it.vertex == component }
    }
}