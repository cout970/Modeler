package com.cout970.modeler.model

import com.cout970.vector.extensions.Quaternion
import com.cout970.vector.extensions.Vector3

/**
 * Created by cout970 on 2016/11/29.
 */

class Model() {

    val objects = mutableListOf<ModelObject>()

    fun getGroups() = objects.map { it.groups }.flatten()

    fun getComponents() = objects.map { it.getComponents() }.flatten()
}

class ModelObject() {

    val groups = mutableListOf<ModelGroup>()

    fun getComponents() = groups.map { it.components }.flatten()
}

class ModelGroup() {

    val transform = Transformation(Vector3.ORIGIN, Quaternion.IDENTITY, Vector3.ORIGIN)
    val components = mutableListOf<ModelComponent>()
}
