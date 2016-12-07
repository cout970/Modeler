package com.cout970.modeler.modelcontrol

import com.cout970.glutilities.device.Keyboard
import com.cout970.matrix.extensions.times
import com.cout970.modeler.render.renderer.ModelRenderer
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJOML
import com.cout970.modeler.util.toJoml3d
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*
import org.joml.Vector3d

/**
 * Created by cout970 on 2016/12/07.
 */
class SelectionManager(val modelController: ModelController) {

    var selectionMode: SelectionMode = SelectionMode.COMPONENT
    var selection: Selection = SelectionNone

    fun mouseTrySelect(mouse: IVector2, renderer: ModelRenderer, viewport: IVector2) {
        val matrixMVP = renderer.matrixP * renderer.matrixV * renderer.matrixM

        val m = matrixMVP.toJOML()
        val a = m.unproject(vec3Of(mouse.x, viewport.yd - mouse.yd, 0.0).toJoml3d(), intArrayOf(0, 0, viewport.xi, viewport.yi), Vector3d()).toIVector()
        val b = m.unproject(vec3Of(mouse.x, viewport.yd - mouse.yd, 1.0).toJoml3d(), intArrayOf(0, 0, viewport.xi, viewport.yi), Vector3d()).toIVector()

        val ray = Ray(a, b)
        if (selectionMode == SelectionMode.COMPONENT) {
            val hits = mutableListOf<RayTraceResult>()

            for (obj in modelController.model.objects) {
                for (group in obj.groups) {
                    for (i in group.components) {
                        i.rayTrace(ray)?.let {
                            hits += it.apply {
                                it.extraData = ModelPath(modelController.model, obj, group, i)
                            }
                        }
                    }
                }
            }

            val hit = if (hits.isEmpty()) null
            else if (hits.size == 1) hits.first()
            else hits.apply { sortBy { it.hit.distance(ray.start) } }.first()

            if (hit != null) {
                if (handleSelection(hit.extraData as ModelPath)) {
                    renderer.cache.clear()
                }
            }
        }
    }


    fun handleSelection(path: ModelPath): Boolean {
        if (selectionMode == SelectionMode.COMPONENT) {
            if (modelController.eventController.keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL) && selection.mode == SelectionMode.COMPONENT) {
                selection = SelectionComponent(selection.paths + path)
            } else {
                selection = SelectionComponent(listOf(path))
            }
            return true
        }
        return false
    }
}