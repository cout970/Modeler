package com.cout970.modeler.controller

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.IObject
import com.cout970.modeler.controller.selector.Cursor
import com.cout970.modeler.controller.selector.ISelectable
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.raytrace.IRayObstacle

/**
 * Created by cout970 on 2017/06/08.
 */
data class World(val models: List<IModel>, val cursor: Cursor) {

    var lastModified = -1L

    fun getModelParts(): List<Pair<IRayObstacle, ObjectRef>> {
        return models.firstOrNull()?.objects?.mapIndexed { index, obj ->
            RayTracer.toRayObstacle(obj) to ObjectRef(index)
        } ?: emptyList()
    }
}

class SelectedObject(val obj: IObject) : ISelectable {
    override val hitbox: IRayObstacle = RayTracer.toRayObstacle(obj)
}