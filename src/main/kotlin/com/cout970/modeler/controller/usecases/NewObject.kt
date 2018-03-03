package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskUpdateModel
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.`object`.Group
import com.cout970.modeler.core.model.`object`.Object
import com.cout970.modeler.core.model.`object`.ObjectCube
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.vector.extensions.Quaternion
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2017/07/19.
 */

@UseCase("cube.mesh.new")
fun newObject(model: IModel): ITask {
    val mesh = MeshFactory.createCube(vec3Of(4, 16, 4), vec3Of(8, 8, 8))
    val obj = Object("Object ${model.objects.size}", mesh)

    return addObject(model, obj)
}

@UseCase("cube.template.new")
fun newObjectCube(model: IModel): ITask {
    val obj = ObjectCube(
            name = "Object ${model.objects.size}",
            transformation = TRSTransformation(vec3Of(4, 16, 4), Quaternion.IDENTITY, vec3Of(8, 8, 8))
    )
    return addObject(model, obj)
}

private fun addObject(model: IModel, obj: IObject): TaskUpdateModel {
    val newModel = model.addObjects(listOf(obj))
    return TaskUpdateModel(model, newModel)
}

@UseCase("group.add")
fun addGroup(model: IModel): ITask {
    val group = Group("Group ${model.groupTree.root.size}")
    val newGroupTree = model.groupTree.addGroup(null, group)
    val newModel = model.withGroupTree(newGroupTree)
    return TaskUpdateModel(model, newModel)
}