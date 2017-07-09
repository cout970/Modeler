package com.cout970.modeler.controller.selector


import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.SelectionHandler
import com.cout970.modeler.core.tool.EditTool
import com.cout970.modeler.util.FakeRayObstacle
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.render.tool.camera.Camera
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times

/**
 * Created by cout970 on 2017/04/09.
 */
class Cursor {

    var center: IVector3 = Vector3.ORIGIN

    fun getCursorParameters(camera: Camera, viewport: IVector2): CursorParameters {
        return CursorParameters.create(camera.zoom, viewport)
    }

    fun getSelectableParts(gui: Gui, camera: Camera, viewport: IVector2): List<ISelectable> {
        if (gui.selectionHandler.ref.isEmpty()) {
            return emptyList()
        }
        val parameters = CursorParameters.create(camera.zoom, viewport)
        return when (gui.state.transformationMode) {
            TransformationMode.TRANSLATION -> listOf(
                    CursorPartTranslate(this, parameters, Vector3.X_AXIS),
                    CursorPartTranslate(this, parameters, Vector3.Y_AXIS),
                    CursorPartTranslate(this, parameters, Vector3.Z_AXIS)
            )
            TransformationMode.ROTATION -> listOf()
            TransformationMode.SCALE -> listOf()
        }
    }

    class CursorPartTranslate(val cursor: Cursor, val parameters: CursorParameters,
                              override val translationAxis: IVector3) : ITranslatable {

        override val hitbox: IRayObstacle get() = object : IRayObstacle {
            override fun rayTrace(ray: Ray): RayTraceResult? {
                val (a, b) = calculateHitbox()
                return RayTraceUtil.rayTraceBox3(a, b, ray, FakeRayObstacle)
            }
        }

        fun calculateHitbox(): Pair<IVector3, IVector3> {
            return Pair(
                    cursor.center - Vector3.ONE * parameters.width,
                    cursor.center + translationAxis * parameters.length + Vector3.ONE * parameters.width
            )
        }

        override fun applyTranslation(offset: Float, selHandler: SelectionHandler, model: IModel): IModel {

            val sel = selHandler.ref
            return EditTool.translate(model, sel, translationAxis * offset)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is CursorPartTranslate) return false

            if (translationAxis != other.translationAxis) return false

            return true
        }

        override fun hashCode(): Int {
            return translationAxis.hashCode()
        }
    }
}
