package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.IRef
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.helpers.PickupHelper
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.canvas.helpers.CanvasHelper
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.util.absolutePositionV
import com.cout970.modeler.util.getClosest
import com.cout970.modeler.util.toNullable
import com.cout970.modeler.util.toRads
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
private fun switchCameraProjection(canvasContainer: CanvasContainer): ITask {
    canvasContainer.selectedCanvas?.cameraHandler?.let { handler ->
        return ModifyGui { handler.setOrtho(handler.camera.perspective) }
    }
    return TaskNone
}

@UseCase("view.set.texture.mode")
private fun setCanvasModeTexture(canvasContainer: CanvasContainer): ITask {
    canvasContainer.selectedCanvas?.let { canvas ->
        return ModifyGui { canvas.viewMode = SelectionTarget.TEXTURE }
    }
    return TaskNone
}

@UseCase("view.set.model.mode")
private fun setCanvasModeModel(canvasContainer: CanvasContainer): ITask {
    canvasContainer.selectedCanvas?.let { canvas ->
        return ModifyGui { canvas.viewMode = SelectionTarget.MODEL }
    }
    return TaskNone
}

@UseCase("camera.set.isometric")
private fun setIsometricCamera(canvasContainer: CanvasContainer): ITask {
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
private fun jumpCameraToCanvas(component: Component, gui: Gui, input: IInput, model: IModel): ITask {
    if (gui.state.cursor.getParts().any { it.hovered }) return TaskNone

    val canvas = component as Canvas
    val pos = input.mouse.getMousePos()
    val (result, _) = PickupHelper.pickup3D(canvas, pos, model, SelectionType.OBJECT, gui.animator) ?: return TaskNone

    return ModifyGui { canvas.cameraHandler.setPosition(-result.hit) }
}

@UseCase("canvas.select.model")
private fun selectPartInCanvas(component: Component, input: IInput, gui: Gui): ITask {
    val canvas = component as Canvas

    if (canvas.viewMode != SelectionTarget.MODEL) return TaskNone

    if (gui.state.cursor.visible && gui.state.cursor.getParts().any { it.hovered }) return TaskNone
    return onModel(canvas, gui, input)
}

@UseCase("canvas.select.texture")
private fun selectPartInCanvas2(component: Component, input: IInput, gui: Gui): ITask {
    val canvas = component as Canvas

    if (canvas.viewMode != SelectionTarget.TEXTURE) return TaskNone

    return onTexture(canvas, input, gui)
}

private fun onModel(canvas: Canvas, gui: Gui, input: IInput): ITask {

    tryClickOrientationCube(gui, canvas, input)?.let { return it }

    val multiSelection = Config.keyBindings.multipleSelection.check(input)
    val (model, selection) = gui.programState
    val pos = input.mouse.getMousePos()
    val obj = PickupHelper.pickup3D(canvas, pos, model, gui.state.selectionType, gui.animator)?.second

    val newSelection = gui.programState.modelSelectionHandler.updateSelection(
            selection.toNullable(),
            multiSelection,
            obj
    )

    return TaskUpdateModelSelection(
            oldSelection = selection,
            newSelection = newSelection
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
    val selHandler = gui.programState.textureSelectionHandler
    val obj = getTextureSelection(canvas, input, gui)
    val multiSelection = Config.keyBindings.multipleSelection.check(input)
    val selection = selHandler.getSelection()

    return TaskUpdateTextureSelection(
            oldSelection = selection,
            newSelection = selHandler.updateSelection(
                    selection.toNullable(),
                    multiSelection,
                    obj
            )
    )
}

fun getTextureSelection(canvas: Canvas, input: IInput, gui: Gui): IRef? {
    val (model, modSel) = gui.programState

    val mouse = input.mouse.getMousePos()
    val actualMaterial = model.getMaterial(gui.state.selectedMaterial)
    val selectionType = gui.state.selectionType

    return PickupHelper.pickup2D(canvas, mouse, model, modSel, actualMaterial, selectionType)?.second
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