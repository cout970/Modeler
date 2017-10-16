package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.canvas.helpers.CanvasHelper
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.getClosest
import com.cout970.modeler.util.toNullable
import com.cout970.modeler.util.toRayObstacle
import com.cout970.raytrace.IRayObstacle
import com.cout970.vector.extensions.unaryMinus
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/20.
 */


class CanvasSelectPart : IUseCase {

    override val key: String = "canvas.select"

    @Inject lateinit var component: Component
    @Inject lateinit var selection: Nullable<ISelection>
    @Inject lateinit var state: GuiState
    @Inject lateinit var input: IInput
    @Inject lateinit var model: IModel
    @Inject lateinit var gui: Gui

    override fun createTask(): ITask {
        if (state.hoveredObject != null) return TaskNone

        val canvas = component as Canvas
        val pos = input.mouse.getMousePos()
        val context = CanvasHelper.getMouseSpaceContext(canvas, pos)
        val obj = model.getObstacles()
                .mapNotNull { (obj, ref) ->
                    val res = obj.rayTrace(context.mouseRay)
                    res?.let { result -> result to ref }
                }
                .getClosest(context.mouseRay)

        val multiSelection = Config.keyBindings.multipleSelection.check(input)

        return TaskUpdateModelSelection(
                oldSelection = selection.toNullable(),
                newSelection = gui.modelAccessor.modelSelectionHandler.updateSelection(selection.toNullable(),
                        multiSelection, obj?.second))
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
        val obj = model.getObstacles()
                .mapNotNull { (obj, ref) ->
                    val res = obj.rayTrace(context.mouseRay)
                    res?.let { result -> result to ref }
                }
                .getClosest(context.mouseRay)

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

fun IModel.getObstacles(): List<Pair<IRayObstacle, IObjectRef>> {
    return objectRefs
            .filter { isVisible(it) }
            .map { getObject(it) to it }
            .map { (obj, ref) -> obj.toRayObstacle() to ref }
}