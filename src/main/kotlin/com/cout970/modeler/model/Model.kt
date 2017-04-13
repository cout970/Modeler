package com.cout970.modeler.model

import com.cout970.modeler.model.api.IElement

/**
 * Created by cout970 on 2017/02/11.
 */

class Model(val elements: List<IElement>, val resources: ModelResources, val id: Int = modelIds++) {

    companion object {
        // the id is used to get a different hashCode for every model, so this can be used to detect changes
        private var modelIds = 0
    }

    //copies the model with a different modelId so the hashCode of the model is different
    fun copy(elements: List<IElement> = this.elements, resources: ModelResources = this.resources): Model {
        return Model(elements, resources)
    }

    fun getVertices(): List<Vertex> = elements.flatMap { it.getVertices() }
    fun getQuads(): List<Quad> = elements.flatMap { it.getQuads() }

    override fun hashCode(): Int {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Model) return false
        if (id != other.id) return false
        return true
    }

    override fun toString(): String {
        return "Model(elements=$elements, resources=$resources, id=$id)"
    }
}