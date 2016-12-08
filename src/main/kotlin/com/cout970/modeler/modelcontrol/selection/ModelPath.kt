package com.cout970.modeler.modelcontrol.selection

import com.cout970.modeler.model.*

/**
 * Created by cout970 on 2016/12/07.
 */
data class ModelPath(
        val model: Model,
        val obj: ModelObject? = null,
        val group: ModelGroup? = null,
        val component: ModelComponent? = null,
        val quad: Quad? = null,
        val vertex: Vertex? = null) {

    val level: Int by lazy {
        if (obj == null) 0
        else if (group == null) 1
        else if (component == null) 2
        else if (quad == null) 3
        else if (vertex == null) 4
        else 5
    }

    override fun toString(): String {
        return "ModelPath(model=${model.javaClass}, obj=${obj?.javaClass}, group=${group?.javaClass}, component=${component?.javaClass}, quad=$quad, vertex=$vertex)"
    }
}