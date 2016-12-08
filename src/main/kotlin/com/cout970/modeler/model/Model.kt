package com.cout970.modeler.model

/**
 * Created by cout970 on 2016/11/29.
 */

class Model() {

    val objects = mutableListOf<ModelObject>()

    fun getGroups() = objects.map { it.groups }.flatten()

    fun getComponents() = objects.map { it.getComponents() }.flatten()

    fun copy(): Model {
        val model = Model()
        for (i in objects) {
            model.objects += i.copy()
        }
        return model
    }
}

class ModelObject() {

    val transform = Transformation.IDENTITY
    val groups = mutableListOf<ModelGroup>()

    fun getComponents() = groups.map { it.components }.flatten()

    fun copy(): ModelObject {
        val obj = ModelObject()
        for (i in groups) {
            obj.groups += i.copy()
        }
        return obj
    }
}

class ModelGroup() {

    var transform = Transformation.IDENTITY
    val components = mutableListOf<ModelComponent>()

    fun copy(): ModelGroup {
        val group = ModelGroup()
        group.transform = transform
        for (i in components) {
            group.components += i
        }
        return group
    }
}
