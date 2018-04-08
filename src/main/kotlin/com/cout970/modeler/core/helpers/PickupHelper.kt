package com.cout970.modeler.core.helpers

import com.cout970.collision.IPolygon
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.selection.IRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.util.*
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*
import org.joml.Vector3d

object PickupHelper {

    fun pickup3D(canvas: Canvas, absPos: IVector2, model: IModel, type: SelectionType): Pair<RayTraceResult, IRef>? {
        val ray = getMouseRayAbsolute(canvas, absPos)
        val obstacles = getModelObstacles(model, type)
        return getFirstCollision(ray, obstacles)
    }

    fun getMouseRayAbsolute(canvas: Canvas, absMousePos: IVector2): Ray {
        return getMouseRay(canvas, absMousePos - canvas.absolutePositionV)
    }

    fun getMouseRay(canvas: Canvas, mousePos: IVector2): Ray {
        // MVP matrix of the canvas
        val matrix = canvas.cameraHandler.camera.getMatrix(canvas.size.toIVector()).toJOML()

        // Since the position of the mouse is in the range [0..maxX), [maxY..0)
        // the view port must use the same coordinates
        val viewportSize = canvas.size.toIVector()
        val viewport = intArrayOf(0, 0, viewportSize.xi, viewportSize.yi)

        val a = Vector3d()
        val b = Vector3d()

        // project the mouse position into the scene, as close as possible to the camera
        matrix.unproject(
                Vector3d(mousePos.xd, viewportSize.yd - mousePos.yd, 0.0), // y is inverted
                viewport, a
        )

        // project the mouse position into the scene, as far as possible to the camera
        matrix.unproject(
                Vector3d(mousePos.xd, viewportSize.yd - mousePos.yd, 1.0),
                viewport, b
        )

        return Ray(a.toIVector(), b.toIVector())
    }

    fun <T> getFirstCollision(ray: Ray, obstacles: List<Pair<IRayObstacle, T>>): Pair<RayTraceResult, T>? {
        val res = obstacles.mapNotNull { (obj, ref) ->
            obj.rayTrace(ray)?.let { result -> result to ref }
        }
        return res.getClosest(ray)
    }

    fun getModelObstacles(model: IModel, type: SelectionType): List<Pair<IRayObstacle, IRef>> {
        val objs = model.objectMap.toList().filter { it.second.visible }

        return when (type) {
            SelectionType.OBJECT -> objs.map { (ref, obj) -> obj.toRayObstacle() to ref }
            SelectionType.FACE -> objs.flatMap { (ref, obj) -> obj.getFaceRayObstacles(ref) }
            SelectionType.EDGE -> objs.flatMap { (ref, obj) -> obj.getEdgeRayObstacles(ref) }
            SelectionType.VERTEX -> objs.flatMap { (ref, obj) -> obj.getVertexRayObstacles(ref) }
        }
    }


    fun getMousePosAbsolute(canvas: Canvas, absPos: IVector2): IVector2 {
        val cam = canvas.textureCamera.camera
        val aspectRatio = (canvas.size.y / canvas.size.x)
        val camPos = vec2Of(cam.position.xd, cam.position.yd)

        val center = canvas.absolutePositionV + canvas.size.toIVector() * 0.5
        val distanceToCenter = (absPos - center) * 2

        val relPos = distanceToCenter / canvas.size.toIVector() * vec2Of(1.0 / aspectRatio, 1)
        val scaledPos = relPos * cam.zoom * vec2Of(1, -1)

        return scaledPos - camPos
    }

    fun getMousePos(canvas: Canvas, mousePos: IVector2): IVector2 {
        val cam = canvas.textureCamera.camera
        val camPos = cam.position.toVector2()
        val aspectRatio = (canvas.size.y / canvas.size.x)
        val center = canvas.size.toIVector() * 0.5

        val distanceToCenter = (mousePos - center) * 2

        // (dist * 2 / size) * zoom

        val relPos = distanceToCenter / canvas.size.toIVector() * vec2Of(1.0 / aspectRatio, 1)

        val scaledPos = relPos * cam.zoom * vec2Of(1, -1)

        return scaledPos - camPos
    }

    fun getTexturePolygons(model: IModel, selection: Nullable<ISelection>, type: SelectionType, mat: IMaterial)
            : List<Pair<IPolygon, IRef>> {

        val selectedObjects = selection.map { sel ->
            sel.objects
                    .map { it to model.getObject(it) }
                    .filter { it.second.visible && it.second.material == mat.ref }
        }.flatMapList()

        val objs = when {
            selectedObjects.isNotEmpty() -> selectedObjects
            else -> model.objectMap
                    .toList()
                    .filter { it.second.visible && it.second.material == mat.ref }
        }

        return when (type) {
            SelectionType.OBJECT -> objs.flatMap { (ref, obj) -> obj.getTexturePolygon(ref) }
            SelectionType.FACE -> objs.flatMap { (ref, obj) -> obj.getFaceTexturePolygons(ref) }
            SelectionType.EDGE -> objs.flatMap { (ref, obj) -> obj.getEdgeTexturePolygons(ref, mat) }
            SelectionType.VERTEX -> objs.flatMap { (ref, obj) -> obj.getVertexTexturePolygons(ref, mat) }
        }
    }
}