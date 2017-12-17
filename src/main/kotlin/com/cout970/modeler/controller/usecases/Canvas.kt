package com.cout970.modeler.controller.usecases

import com.cout970.collision.IPolygon
import com.cout970.collision.collide
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.IRef
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.injection.Inject
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
import com.cout970.vector.extensions.div
import com.cout970.vector.extensions.unaryMinus
import com.cout970.vector.extensions.vec2Of
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/20.
 */

class CanvasSelectPart : IUseCase {

    override val key: String = "canvas.select"

    @Inject lateinit var component: Component
    @Inject lateinit var state: GuiState
    @Inject lateinit var input: IInput
    @Inject lateinit var model: IModel
    @Inject lateinit var gui: Gui

    override fun createTask(): ITask {
        if (state.hoveredObject != null) return TaskNone

        val canvas = component as Canvas
        return when (canvas.viewMode) {
            SelectionTarget.MODEL -> onModel(canvas)
            SelectionTarget.TEXTURE -> onTexture(canvas)
            SelectionTarget.ANIMATION -> onModel(canvas)
        }
    }

    private fun onModel(canvas: Canvas): ITask {
        val obj = trySelectModel(canvas)
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

    fun trySelectModel(canvas: Canvas): IRef? {
        val pos = input.mouse.getMousePos()
        val context = CanvasHelper.getMouseSpaceContext(canvas, pos)
        val obstacles = model.getModelObstacles(state.selectionType)
        val res = obstacles.mapNotNull { (obj, ref) -> obj.rayTrace(context.mouseRay)?.let { result -> result to ref } }
        return res.getClosest(context.mouseRay)?.second
    }

    fun trySelectTexture(canvas: Canvas): IRef? {
        val mouse = input.mouse.getMousePos()
        val clickPos = CanvasHelper.getMouseProjection(canvas, mouse)
        val materialRef = state.selectedMaterial
        val polygons = model.getTexturePolygons(state.selectionType, materialRef)
        val actualMaterial = model.getMaterial(materialRef)

        val finalPos = (clickPos / actualMaterial.size).run { vec2Of(xd, 1 - yd) }
        val mouseCollisionBox = getVertexTexturePolygon(finalPos)

        val selected = polygons.filter { it.first.collide(mouseCollisionBox) }
        val results = selected.map { it.second }.distinct()
        println(results.firstOrNull())
        return results.firstOrNull()
    }

    private fun onTexture(canvas: Canvas): ITask {
        val obj = trySelectTexture(canvas)
        val multiSelection = Config.keyBindings.multipleSelection.check(input)
        val selection = gui.modelAccessor.textureSelectionHandler.getSelection()

        return TaskUpdateTextureSelection(
                oldSelection = selection,
                newSelection = gui.modelAccessor.textureSelectionHandler.updateSelection(
                        selection.toNullable(),
                        multiSelection,
                        obj
                )
        )
    }
}

class CanvasJumpCamera : IUseCase {

    override val key: String = "canvas.jump.camera"

    @Inject lateinit var component: Component
    @Inject lateinit var state: GuiState
    @Inject lateinit var input: IInput
    @Inject lateinit var model: IModel

    override fun createTask(): ITask {
        if (state.hoveredObject != null) return TaskNone

        val canvas = component as Canvas
        val pos = input.mouse.getMousePos()
        val context = CanvasHelper.getMouseSpaceContext(canvas, pos)
        val obstacles = model.getModelObstacles(SelectionType.OBJECT)
        val res = obstacles.mapNotNull { (obj, ref) -> obj.rayTrace(context.mouseRay)?.let { result -> result to ref } }
        val obj = res.getClosest(context.mouseRay)

        val point = obj?.first ?: return TaskNone
        return TaskUpdateCameraPosition(canvas, -point.hit)
    }
}

class SwitchProjection : IUseCase {

    override val key: String = "view.switch.ortho"

    @Inject lateinit var canvasContainer: CanvasContainer

    override fun createTask(): ITask {
        canvasContainer.selectedCanvas?.cameraHandler?.let {
            return TaskUpdateCameraProjection(handler = it, ortho = it.camera.perspective)
        }
        return TaskNone
    }
}

class SetTextureMode : IUseCase {
    override val key: String = "view.set.texture.mode"

    @Inject lateinit var canvasContainer: CanvasContainer

    override fun createTask(): ITask {
        canvasContainer.selectedCanvas?.let {
            return TaskUpdateCanvasViewMode(it, SelectionTarget.TEXTURE)
        }
        return TaskNone
    }
}

class SetModelMode : IUseCase {
    override val key: String = "view.set.model.mode"

    @Inject lateinit var canvasContainer: CanvasContainer

    override fun createTask(): ITask {
        canvasContainer.selectedCanvas?.let {
            return TaskUpdateCanvasViewMode(it, SelectionTarget.MODEL)
        }
        return TaskNone
    }
}

class SetAnimationMode : IUseCase {
    override val key: String = "view.set.animation.mode"

    @Inject lateinit var canvasContainer: CanvasContainer

    override fun createTask(): ITask {
        canvasContainer.selectedCanvas?.let {
            return TaskUpdateCanvasViewMode(it, SelectionTarget.ANIMATION)
        }
        return TaskNone
    }
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

fun IModel.getTexturePolygons(selectionType: SelectionType, material: IMaterialRef): List<Pair<IPolygon, IRef>> {
    val objs = objectRefs
            .filter { isVisible(it) }
            .map { getObject(it) to it }
            .filter { it.first.material == material }

    return when (selectionType) {
        SelectionType.OBJECT -> objs.flatMap { (obj, ref) -> obj.getTexturePolygon(ref) }
        SelectionType.FACE -> objs.flatMap { (obj, ref) -> obj.getFaceTexturePolygons(ref) }
        SelectionType.EDGE -> objs.flatMap { (obj, ref) -> obj.getEdgeTexturePolygons(ref) }
        SelectionType.VERTEX -> objs.flatMap { (obj, ref) -> obj.getVertexTexturePolygons(ref) }
    }
}