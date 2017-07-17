package com.cout970.modeler.functional.usecases

import com.cout970.modeler.ProgramState
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.functional.tasks.ITask
import com.cout970.modeler.functional.tasks.TaskUpdateModel
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Quaternion
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec3Of
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/17.
 */
class AddCubeUseCase : IUseCase<AddCubeEvent> {

    override val key = "cube.template.new"
    override val processor = AddCubeProcessor()

    override fun buildEvent(state: ProgramState, caller: Component?): AddCubeEvent {
        return AddCubeEvent(state.projectManager.model)
    }
}

data class AddCubeEvent(
        val model: IModel,
        val name: String = "Shape${model.objects.size}",
        val pos: IVector3 = Vector3.ORIGIN,
        val size: IVector3 = vec3Of(8),
        val material: IMaterialRef = MaterialRef(-1)
) : IUserEvent

class AddCubeProcessor : IEventProcessor<AddCubeEvent> {

    override fun processEvent(event: AddCubeEvent): ITask {
        val obj = ObjectCube(
                event.name,
                event.pos,
                Quaternion.IDENTITY,
                event.size,
                material = event.material
        )
        return addObject(event.model, obj)
    }

    fun addObject(model: IModel, obj: IObject): TaskUpdateModel {
        val newModel = model.addObjects(listOf(obj))
        return TaskUpdateModel(model, newModel)
    }
}

