package com.cout970.modeler.gui.canvas.tool

import com.cout970.modeler.core.model.getSelectionCenter
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.cursor.AABBObstacle
import com.cout970.modeler.gui.canvas.cursor.CursorParameters
import com.cout970.modeler.render.tool.camera.Camera
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.util.getClosest
import com.cout970.modeler.util.getHits
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times

enum class CursorMode { TRANSLATION, ROTATION, SCALE }
enum class CursorOrientation { GLOBAL, LOCAL }

class Cursor3D {

    var position: IVector3 = Vector3.ZERO
    var visible: Boolean = true

    var mode: CursorMode = CursorMode.TRANSLATION
    var orientation: CursorOrientation = CursorOrientation.GLOBAL

    val upDir: IVector3 = Vector3.Y_AXIS
    val rigthDir: IVector3 = Vector3.X_AXIS

    private val parts = CursorMode.values().map { mode ->
        mode to listOf(Vector3.X_AXIS, Vector3.Y_AXIS, Vector3.Z_AXIS).map { dir -> CursorPart(mode, dir) }
    }.toMap()

    fun getParts(): List<CursorPart> = parts[mode]!!

    fun update(gui: Gui) {
        val sel = gui.state.modelSelection
        val model = gui.state.tmpModel ?: gui.programState.model

        if (sel.isNull()) {
            visible = false
        } else {
            visible = true
            position = model.getSelectionCenter(sel.getNonNull(), gui.animator)
        }
    }
}

data class CursorPart(val mode: CursorMode, val vector: IVector3, var hovered: Boolean = false) {

    fun calculateHitbox(cursor: Cursor3D, camera: Camera, viewport: IVector2): IRayObstacle {
        val params = CursorParameters.create(camera.zoom, viewport)

        when (mode) {
            CursorMode.TRANSLATION, CursorMode.SCALE -> {
                return AABBObstacle {
                    Pair(cursor.position - Vector3.ONE * params.width,
                            cursor.position + vector * params.length + Vector3.ONE * params.width)
                }
            }
            CursorMode.ROTATION -> {
                val mesh = RenderUtil.createCircleMesh(cursor.position, vector, params.length, params.width)

                return object : IRayObstacle {
                    override fun rayTrace(ray: Ray): RayTraceResult? {
                        return mesh.getHits(ray).getClosest(ray)
                    }
                }
            }
        }
    }
}
