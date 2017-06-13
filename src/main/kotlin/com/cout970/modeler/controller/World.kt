package com.cout970.modeler.controller

import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.IObject
import com.cout970.modeler.controller.selector.IRotable
import com.cout970.modeler.controller.selector.IScalable
import com.cout970.modeler.controller.selector.ISelectable
import com.cout970.modeler.controller.selector.ITranslatable
import com.cout970.raytrace.IRayObstacle

/**
 * Created by cout970 on 2017/06/08.
 */
data class World(val models: List<IModel>) {

    val cache: MutableList<List<VAO>> = models.map { listOf<VAO>() }.toMutableList()

    fun getSelectableObjects(state: GuiState): List<ISelectable> {
        if (state.editMode == EditMode.OBJECT) {
            return models.first().objects.map { obj -> SelectedObject(obj) }
        }
        return emptyList()
    }
}

class SelectedObject(val obj: IObject) : ISelectable {
    override val isPersistent: Boolean = true
    override val hitbox: IRayObstacle = RayTracer.toRayObstacle(obj)
    override val translatableAxis: List<ITranslatable> = emptyList()
    override val rotableAxis: List<IRotable> = emptyList()
    override val scalableAxis: List<IScalable> = emptyList()
}