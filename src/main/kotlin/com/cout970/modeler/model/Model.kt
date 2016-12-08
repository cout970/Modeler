package com.cout970.modeler.model

/**
 * Created by cout970 on 2016/11/29.
 */

class Model() {

    val objects = mutableListOf<ModelObject>()

    fun getGroups() = objects.map { it.groups }.flatten()

    fun getComponents() = objects.map { it.getComponents() }.flatten()
}

class ModelObject() {

    val transform = Transformation.IDENTITY
    val groups = mutableListOf<ModelGroup>()

    fun getComponents() = groups.map { it.components }.flatten()
}

class ModelGroup() {

    val transform = Transformation.IDENTITY
    val components = mutableListOf<ModelComponent>()
}
