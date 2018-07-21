package com.cout970.modeler.gui.canvas.tool

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.api.model.selection.toObjectRef
import com.cout970.modeler.core.model.*
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.cursor.CursorParameters
import com.cout970.modeler.render.tool.camera.Camera
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.util.getClosest
import com.cout970.modeler.util.getHits
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

enum class CursorMode { TRANSLATION, ROTATION, SCALE }
enum class CursorOrientation { GLOBAL, LOCAL }

class Cursor3D {

    var position: IVector3 = Vector3.ZERO
    var visible: Boolean = true

    var mode: CursorMode = CursorMode.TRANSLATION
    var orientation: CursorOrientation = CursorOrientation.LOCAL

    var rotation: IQuaternion = Quaternion.IDENTITY

    private val parts = CursorMode.values().map { mode ->
        val pairs = listOf(Vector3.X_AXIS, Vector3.Y_AXIS, Vector3.Z_AXIS)
        mode to pairs.map { CursorPart(mode, it, it) }
    }.toMap()

    fun getParts(): List<CursorPart> = parts[mode]!!

    fun update(gui: Gui) {
        val sel = gui.state.modelSelection
        val model = gui.state.tmpModel ?: gui.programState.model

        if (sel.isNull()) {
            visible = false
        } else {
            visible = true
            val selection = sel.getNonNull()
            position = model.getSelectionCenter(selection, gui.animator)
            rotation = getSelectionMatrix(model, selection).toTRS().rotation
        }
    }

    fun getSelectionMatrix(model: IModel, sel: ISelection): ITransformation {
        if (orientation == CursorOrientation.GLOBAL) {
            return TRSTransformation.IDENTITY
        }

        when (sel.selectionType) {
            SelectionType.OBJECT -> {
                if (sel.size != 1) return TRSTransformation.IDENTITY
                val obj = sel.objects.first()

                return model.getObject(obj).getGlobalTransform(model)
            }
            SelectionType.FACE -> {
                val groups = sel.faces.groupBy { it.objectId }
                if (groups.size != 1) return TRSTransformation.IDENTITY

                val firstRef = groups.entries.first().value.first()
                return model.getObject(firstRef.toObjectRef()).getGlobalTransform(model)
            }
            SelectionType.EDGE -> {
                val groups = sel.edges.groupBy { it.objectId }
                if (groups.size != 1) return TRSTransformation.IDENTITY

                val firstRef = groups.entries.first().value.first()
                return model.getObject(firstRef.toObjectRef()).getGlobalTransform(model)
            }
            SelectionType.VERTEX -> {
                val groups = sel.pos.groupBy { it.objectId }
                if (groups.size != 1) return TRSTransformation.IDENTITY

                val firstRef = groups.entries.first().value.first()
                return model.getObject(firstRef.toObjectRef()).getGlobalTransform(model)
            }
        }
    }
}

data class CursorPart(val mode: CursorMode, val vector: IVector3, val color: IVector3, var hovered: Boolean = false) {


    fun calculateHitbox(cursor: Cursor3D, camera: Camera, viewport: IVector2): IMesh {
        val params = CursorParameters.create(camera.zoom, viewport)

        return when (mode) {
            CursorMode.TRANSLATION, CursorMode.SCALE -> {
                val min = -Vector3.ONE * params.width
                val max = vector * params.length + Vector3.ONE * params.width
                MeshFactory.createAABB(min, max)
                        .transform(TRSTransformation(
                                translation = cursor.position,
                                rotation = cursor.rotation
                        ))
            }
            CursorMode.ROTATION -> {
                RenderUtil.createCircleMesh(Vector3.ZERO, vector, params.length, params.width)
                        .transform(TRSTransformation(
                                translation = cursor.position,
                                rotation = cursor.rotation
                        ))
            }
        }
    }

    fun calculateHitbox2(cursor: Cursor3D, camera: Camera, viewport: IVector2): IRayObstacle {
        val mesh = calculateHitbox(cursor, camera, viewport)
        return object : IRayObstacle {
            override fun rayTrace(ray: Ray): RayTraceResult? {
                return mesh.getHits(ray).getClosest(ray)
            }
        }
    }
}
