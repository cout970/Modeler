package com.cout970.modeler.controller.selector

import com.cout970.glutilities.device.Mouse
import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.modeler.controller.GuiState
import com.cout970.modeler.controller.ProjectController
import com.cout970.modeler.controller.World
import com.cout970.modeler.controller.selector.CanvasHelper.getMouseSpaceContext
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.record.HistoricalRecord
import com.cout970.modeler.core.record.action.ActionModifyModelShape
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.getClosest
import com.cout970.modeler.util.isInside
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.event.IInput
import com.cout970.modeler.view.gui.comp.canvas.Canvas
import com.cout970.modeler.view.gui.comp.canvas.CanvasContainer
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector2

/**
 * Created by cout970 on 2017/04/08.
 */
class Selector(val projectController: ProjectController, val input: IInput) {

    private var activeCanvas: Canvas? = null
    private var lastMousePos: IVector2? = null
    private var mousePress = false

    private var translationLastOffset = 0f
    private var rotationLastOffset = 0f
    private var scaleLastOffset = 0f

    fun update(canvasContainer: CanvasContainer, historyRecord: HistoricalRecord) {
        activeCanvas = canvasContainer.selectedCanvas

        activeCanvas?.let { activeScene ->
            val context = getMouseSpaceContext(activeScene, input.mouse.getMousePos())
            val state = projectController.guiState
            val click = Config.keyBindings.selectModelControls.check(input)

            if (state.holdingSelection == null) { // no selection

                // update hoverObject
                val objects = projectController.world.getSelectableObjects(state).filter { !it.isPersistent }
                state.hoveredObject = getHoveredObject(context, objects)

                // if clicked add object to selection
                if (click && state.hoveredObject != null) {
                    state.holdingSelection = state.hoveredObject
                    state.hoveredObject = null
                }
            } else { // has selection
                if (!click) { // end selection

                    // apply changes
                    state.tmpModel?.let { model ->
                        historyRecord.doAction(ActionModifyModelShape(projectController, model))
                    }
                    // reset selection
                    state.holdingSelection = null
                }
            }
        }
        val mousePos = input.mouse.getMousePos()
        if (!input.mouse.isButtonPressed(Mouse.BUTTON_LEFT)) {
            mousePress = false
            lastMousePos = null
            canvasContainer.canvas.forEach { canvas ->
                if (mousePos.isInside(canvas.absolutePosition, canvas.size.toIVector())) {
                    canvasContainer.selectedCanvas = canvas
                }
            }
        } else {
            lastMousePos?.let { onDrag(EventMouseDrag(it, mousePos)) }
        }
    }

    fun onClick(e: EventMouseClick, canvas: Canvas) {
        val click = Config.keyBindings.selectModelControls.check(e)
        if (click && e.keyState == EnumKeyState.PRESS) {
            val state = projectController.guiState
            val pos = input.mouse.getMousePos()
            val context = CanvasHelper.getMouseSpaceContext(canvas, pos)
            val obj = projectController.world.getSelectableObjects(state)
                    .filter { it.isPersistent }
                    .find { it.hitbox.rayTrace(context.mouseRay) != null }
            state.persistentSelection = obj
            println(obj)
        }
    }

    fun onDrag(event: EventMouseDrag) {
        activeCanvas?.let { activeScene ->
            val state = projectController.guiState
            state.holdingSelection?.let { selectedObject ->

                if (selectedObject is ITranslatable && state.transformationMode == TransformationMode.TRANSLATION) {
                    val context = CanvasHelper.getContext(activeScene, event.oldPos to event.newPos)
                    val offset = TranslationHelper.getOffset(
                            obj = selectedObject,
                            canvas = activeScene,
                            input = input,
                            newContext = context.first,
                            oldContext = context.second
                    )

                    if (rotationLastOffset != offset) {
                        rotationLastOffset = offset
                        state.tmpModel = selectedObject.applyTranslation(offset, projectController.project.model)
                    }
                } else {
                    rotationLastOffset = 0f
                }

                if (selectedObject is IRotable && state.transformationMode == TransformationMode.ROTATION) {
                    val context = CanvasHelper.getContext(activeScene, event.oldPos to event.newPos)
                    val offset = RotationHelper.getOffset(
                            obj = selectedObject,
                            input = input,
                            newContext = context.first,
                            oldContext = context.second
                    )

                    if (translationLastOffset != offset) {
                        translationLastOffset = offset
                        state.tmpModel = selectedObject.applyRotation(offset, projectController.project.model)
                    }
                } else {
                    translationLastOffset = 0f
                }

                if (selectedObject is IScalable && state.transformationMode == TransformationMode.SCALE) {
                    val context = CanvasHelper.getContext(activeScene, event.oldPos to event.newPos)
                    val offset = ScaleHelper.getOffset(
                            obj = selectedObject,
                            canvas = activeScene,
                            input = input,
                            newContext = context.first,
                            oldContext = context.second
                    )

                    if (scaleLastOffset != offset) {
                        scaleLastOffset = offset
                        state.tmpModel = selectedObject.applyScale(offset, projectController.project.model)
                    }
                } else {
                    scaleLastOffset = 0f
                }
            }
        }
    }

    private fun updateHovered(target: GuiState, context: SceneSpaceContext, world: World) {
        target.hoveredObject = getHoveredObject(context, world.getSelectableObjects(target))
    }

    private fun getHoveredObject(ctx: SceneSpaceContext, objs: List<ISelectable>): ISelectable? {
        val ray = ctx.mouseRay
        val list = mutableListOf<Pair<RayTraceResult, ISelectable>>()

        objs.forEach { obj ->
            val res = obj.hitbox.rayTrace(ray)
            res?.let { list += it to obj }
        }

        return list.getClosest(ray)?.second
    }
}