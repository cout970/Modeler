package com.cout970.modeler.model

import com.cout970.modeler.model.api.IElement

/**
 * Created by cout970 on 2017/02/11.
 */

data class Model(val elements: List<IElement>, val resources: ModelResources, val id: Int = modelIds++) {

    companion object {
        // the id is used to get a different hashCode for every model, so this can be used to detect changes
        private var modelIds = 0
    }

    //copies the model with a different modelId so the hashCode of the model is different
    fun copy(elements: List<IElement> = this.elements): Model {
        return Model(elements, resources)
    }

    fun getVertices(): List<Vertex> = elements.flatMap { it.getVertices() }
    fun getQuads(): List<Quad> = elements.flatMap { it.getQuads() }
}