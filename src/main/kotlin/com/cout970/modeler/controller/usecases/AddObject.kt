package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskUpdateModel
import com.cout970.modeler.core.model.Object
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.TRTSTransformation
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2017/07/19.
 */

class AddMeshCube : IUseCase {

    override val key: String = "cube.mesh.new"

    @Inject lateinit var model: IModel
    val name: String get() = "Shape${model.objects.size}"
    val size = vec3Of(8, 8, 8)
    val pos = Vector3.ORIGIN

    override fun createTask(): ITask {
        val mesh = MeshFactory.createCube(size, pos)
        val obj = Object(name, mesh)

        return addObject(model, obj)
    }
}

class AddTemplateCube : IUseCase {

    override val key: String = "cube.template.new"

    @Inject lateinit var model: IModel

    val name: String get() = "Shape${model.objects.size}"
    val pos: IVector3 = Vector3.ORIGIN
    val size: IVector3 = vec3Of(8)
    val material: IMaterialRef = MaterialRef(-1)

    override fun createTask(): ITask {
        val obj = ObjectCube(
                name,
                pos,
                TRTSTransformation.IDENTITY,
                size,
                material = material
        )
        return addObject(model, obj)
    }
}

private fun addObject(model: IModel, obj: IObject): TaskUpdateModel {
    val newModel = model.addObjects(listOf(obj))
    return TaskUpdateModel(model, newModel)
}