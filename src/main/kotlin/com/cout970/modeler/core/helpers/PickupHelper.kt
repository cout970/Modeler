package com.cout970.modeler.core.helpers

import com.cout970.collision.IPolygon
import com.cout970.collision.collide
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.selection.IRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.collision.TexturePolygon
import com.cout970.modeler.core.model.getGlobalMesh
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.*
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*
import org.joml.Vector3d

object PickupHelper {

    fun pickup3D(canvas: Canvas, absPos: IVector2, model: IModel, type: SelectionType, animator: Animator): Pair<RayTraceResult, IRef>? {
        val ray = getMouseRayAbsolute(canvas, absPos)
        val obstacles = getModelObstacles(model, type, animator)
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

    fun getModelObstacles(model: IModel, type: SelectionType, animator: Animator): List<Pair<IRayObstacle, IRef>> {
        val objs = model.objectMap.toList().filter { it.second.visible }

        return when (type) {
            SelectionType.OBJECT -> objs.map { (ref, obj) ->
                obj.getGlobalMesh(model, animator).toRayObstacle() to ref
            }
            SelectionType.FACE -> objs.flatMap { (ref, obj) ->
                obj.getGlobalMesh(model, animator).getFaceRayObstacles(ref)
            }
            SelectionType.EDGE -> objs.flatMap { (ref, obj) ->
                obj.getGlobalMesh(model, animator).getEdgeRayObstacles(ref)
            }
            SelectionType.VERTEX -> objs.flatMap { (ref, obj) ->
                obj.getGlobalMesh(model, animator).getVertexRayObstacles(ref)
            }
        }
    }

    fun getMousePosAbsolute(canvas: Canvas, absPos: IVector2): IVector2 {
        return getMousePos(canvas, absPos - canvas.absolutePositionV)
    }

    fun getMousePos(canvas: Canvas, mousePos: IVector2): IVector2 {
        val cam = canvas.textureCamera.camera
        val aspectRatio = (canvas.size.x / canvas.size.y)
        val camPos = cam.position.toVector2()

        val relative = mousePos / canvas.size.toIVector()
        val yAdjusted = relative * vec2Of(1, -1) + vec2Of(0, 1)
        val centered = yAdjusted * 2 - 1
        val scaledPos = centered * vec2Of(aspectRatio, 1) * cam.zoom

        return scaledPos - camPos
    }

    fun <T> getFirstCollision(point: IVector2, obstacles: List<Pair<IPolygon, T>>): Pair<IPolygon, T>? {
        val mouseCollisionBox = getPointPolygon(point)
        val selected = obstacles.filter { it.first.collide(mouseCollisionBox) }
        return selected.firstOrNull()
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

    fun getPointPolygon(point: IVector2): IPolygon {
        val scale = vec2Of(0.01)
        return TexturePolygon(listOf(
                point + vec2Of(-scale.xd, -scale.yd), point + vec2Of(scale.xd, -scale.yd),
                point + vec2Of(scale.xd, scale.yd), point + vec2Of(-scale.xd, scale.yd)
        ))
    }


    fun fromCanvasToMaterial(pos: IVector2, material: IMaterial): IVector2 {
        val scaled = pos / material.size
        return vec2Of(scaled.xd, 1.0 - scaled.yd)
    }

    fun fromMaterialToCanvas(pos: IVector2, material: IMaterial): IVector2 {
        return vec2Of(pos.xd, 1.0 - pos.yd) * material.size
    }

    fun pickup2D(canvas: Canvas, mouse: IVector2, model: IModel, selection: Nullable<ISelection>,
                 material: IMaterial, selectionType: SelectionType): Pair<IPolygon, IRef>? {

        val clickPos = getMousePosAbsolute(canvas, mouse)
        val polygons = getTexturePolygons(model, selection, selectionType, material)
        val finalPos = fromCanvasToMaterial(clickPos, material)
        return getFirstCollision(finalPos, polygons)
    }
}