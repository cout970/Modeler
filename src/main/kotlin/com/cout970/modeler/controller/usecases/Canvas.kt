package com.cout970.modeler.controller.usecases

import com.cout970.collision.IPolygon
import com.cout970.collision.collide
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.canvas.helpers.CanvasHelper
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.util.*
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/20.
 */

@UseCase("view.switch.ortho")
fun switchCameraProjection(canvasContainer: CanvasContainer): ITask {
    canvasContainer.selectedCanvas?.cameraHandler?.let { handler ->
        return ModifyGui { handler.setOrtho(handler.camera.perspective) }
    }
    return TaskNone
}

@UseCase("view.set.texture.mode")
fun setCanvasModeTexture(canvasContainer: CanvasContainer): ITask {
    canvasContainer.selectedCanvas?.let { canvas ->
        return ModifyGui { canvas.viewMode = SelectionTarget.TEXTURE }
    }
    return TaskNone
}

@UseCase("view.set.model.mode")
fun setCanvasModeModel(canvasContainer: CanvasContainer): ITask {
    canvasContainer.selectedCanvas?.let { canvas ->
        return ModifyGui { canvas.viewMode = SelectionTarget.MODEL }
    }
    return TaskNone
}

@UseCase("view.set.animation.mode")
fun setCanvasModeAnimation(canvasContainer: CanvasContainer): ITask {
    canvasContainer.selectedCanvas?.let { canvas ->
        return ModifyGui { canvas.viewMode = SelectionTarget.ANIMATION }
    }
    return TaskNone
}

@UseCase("camera.set.isometric")
fun setIsometricCamera(canvasContainer: CanvasContainer): ITask {
    canvasContainer.selectedCanvas?.let { canvas ->
        if (canvas.viewMode.is3D) {
            return ModifyGui {
                canvas.modelCamera.setOrtho(true)
                canvas.modelCamera.setRotation(45.toRads(), (-45).toRads())
            }
        }
    }
    return TaskNone
}

@UseCase("canvas.jump.camera")
fun jumpCameraToCanvas(component: Component, state: GuiState, input: IInput, model: IModel): ITask {
    if (state.hoveredObject != null) return TaskNone

    val canvas = component as Canvas
    val pos = input.mouse.getMousePos()
    val context = CanvasHelper.getMouseSpaceContext(canvas, pos)
    val obstacles = model.getModelObstacles(SelectionType.OBJECT)
    val res = obstacles.mapNotNull { (obj, ref) -> obj.rayTrace(context.mouseRay)?.let { result -> result to ref } }
    val obj = res.getClosest(context.mouseRay)

    val point = obj?.first ?: return TaskNone
    return ModifyGui { canvas.cameraHandler.setPosition(-point.hit) }
}

@UseCase("canvas.select")
fun selectPartInCanvas(component: Component, input: IInput, model: IModel, gui: Gui): ITask {
    if (gui.state.hoveredObject != null) return TaskNone

    val canvas = component as Canvas
    return when (canvas.viewMode) {
        SelectionTarget.MODEL -> onModel(canvas, gui, input)
        SelectionTarget.TEXTURE -> onTexture(canvas, input, gui)
        SelectionTarget.ANIMATION -> onModel(canvas, gui, input)
    }
}

private fun onModel(canvas: Canvas, gui: Gui, input: IInput): ITask {

    tryClickOrientationCube(gui, canvas, input)?.let { return it }

    val model = gui.modelAccessor.model
    val obj = trySelectModel(canvas, model, gui.state, input)
    val multiSelection = Config.keyBindings.multipleSelection.check(input)
    val selection = gui.modelAccessor.modelSelectionHandler.getSelection()

    return TaskUpdateModelSelection(
            oldSelection = selection,
            newSelection = gui.modelAccessor.modelSelectionHandler.updateSelection(
                    selection.toNullable(),
                    multiSelection,
                    obj
            )
    )
}

private fun tryClickOrientationCube(gui: Gui, canvas: Canvas, input: IInput): ITask? {

    val pos = input.mouse.getMousePos()

    val viewportPos = vec2Of(canvas.absolutePosition.x, canvas.absolutePositionV.yf + canvas.size.y - 150f)

    val context = CanvasHelper.getContextForOrientationCube(canvas, viewportPos, vec2Of(150, 150), pos)

    val obstacles = getOrientationCubeFaces(gui.resources.orientationCubeMesh)
    val res = obstacles.mapNotNull { (obj, ref) -> obj.rayTrace(context.mouseRay)?.let { result -> result to ref } }
    val obj = res.getClosest(context.mouseRay)

    val angles = obj?.second ?: return null
    return ModifyGui { canvas.cameraHandler.setRotation(angles.xd.toRads(), angles.yd.toRads()) }
}

private fun onTexture(canvas: Canvas, input: IInput, gui: Gui): ITask {
    val access = gui.modelAccessor
    val obj = trySelectTexture(canvas, access.modelSelection, access.model, gui.state, input)
    val multiSelection = Config.keyBindings.multipleSelection.check(input)
    val selection = access.textureSelectionHandler.getSelection()

    return TaskUpdateTextureSelection(
            oldSelection = selection,
            newSelection = access.textureSelectionHandler.updateSelection(
                    selection.toNullable(),
                    multiSelection,
                    obj
            )
    )
}

private fun trySelectModel(canvas: Canvas, model: IModel, state: GuiState, input: IInput): IRef? {
    val pos = input.mouse.getMousePos()
    val context = CanvasHelper.getMouseSpaceContext(canvas, pos)
    val obstacles = model.getModelObstacles(state.selectionType)
    val res = obstacles.mapNotNull { (obj, ref) -> obj.rayTrace(context.mouseRay)?.let { result -> result to ref } }
    return res.getClosest(context.mouseRay)?.second
}

private fun trySelectTexture(canvas: Canvas, modelSelection: Nullable<ISelection>, model: IModel, state: GuiState,
                             input: IInput): IRef? {
    val mouse = input.mouse.getMousePos()
    val clickPos = CanvasHelper.getMouseProjection(canvas, mouse)
    val materialRef = state.selectedMaterial
    val actualMaterial = model.getMaterial(materialRef)
    val polygons = model.getTexturePolygons(modelSelection, state.selectionType, materialRef, actualMaterial)

    val finalPos = CanvasHelper.fromRenderToMaterial(clickPos, actualMaterial)
    val mouseCollisionBox = getVertexTexturePolygon(finalPos, actualMaterial)

    val selected = polygons.filter { it.first.collide(mouseCollisionBox) }
    val results = selected.map { it.second }.distinct()

    return results.firstOrNull()
}


private fun getOrientationCubeFaces(mesh: IMesh): List<Pair<IRayObstacle, IVector2>> {
    val obstacles = mesh.faces.map { face ->
        val pos = face.pos.map { mesh.pos[it] }.map { (it * 2.0) + vec3Of(-8) }

        object : IRayObstacle {
            override fun rayTrace(ray: Ray): RayTraceResult? {
                return RayTraceUtil.rayTraceQuad(ray, this, pos[0], pos[1], pos[2], pos[3])
            }
        }
    }
    return listOf(
            obstacles[0] to vec2Of(-90.0, 0.0), // bottom
            obstacles[1] to vec2Of(90.0, 0.0),  // top
            obstacles[2] to vec2Of(0.0, 180.0), // north
            obstacles[3] to vec2Of(0.0, 0.0),   // south
            obstacles[4] to vec2Of(0.0, 90.0),  // west
            obstacles[5] to vec2Of(0.0, -90.0)  // east
    )
}

fun IModel.getModelObstacles(selectionType: SelectionType): List<Pair<IRayObstacle, IRef>> {
    val objs = objectRefs
            .filter { isVisible(it) }
            .map { getObject(it) to it }

    return when (selectionType) {
        SelectionType.OBJECT -> objs.map { (obj, ref) -> obj.toRayObstacle() to ref }
        SelectionType.FACE -> objs.flatMap { (obj, ref) -> obj.getFaceRayObstacles(ref) }
        SelectionType.EDGE -> objs.flatMap { (obj, ref) -> obj.getEdgeRayObstacles(ref) }
        SelectionType.VERTEX -> objs.flatMap { (obj, ref) -> obj.getVertexRayObstacles(ref) }
    }
}

fun IModel.getTexturePolygons(modelSelection: Nullable<ISelection>, selectionType: SelectionType, matRef: IMaterialRef,
                              mat: IMaterial): List<Pair<IPolygon, IRef>> {
    val selectedObjects = modelSelection.map {
        it.refs
                .filterIsInstance<IObjectRef>()
                .filter { isVisible(it) }
                .map { getObject(it) to it }
                .filter { it.first.material == matRef }

    }.getOr(emptyList())

    val objs = when {
        selectedObjects.isNotEmpty() -> selectedObjects
        else -> objectRefs
                .filter { isVisible(it) }
                .map { getObject(it) to it }
                .filter { it.first.material == matRef }
    }

    return when (selectionType) {
        SelectionType.OBJECT -> objs.flatMap { (obj, ref) -> obj.getTexturePolygon(ref) }
        SelectionType.FACE -> objs.flatMap { (obj, ref) -> obj.getFaceTexturePolygons(ref) }
        SelectionType.EDGE -> objs.flatMap { (obj, ref) -> obj.getEdgeTexturePolygons(ref, mat) }
        SelectionType.VERTEX -> objs.flatMap { (obj, ref) -> obj.getVertexTexturePolygons(ref, mat) }
    }
}