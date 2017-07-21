package com.cout970.modeler.controller.selector

import com.cout970.glutilities.device.Mouse
import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.controller.RayTracer
import com.cout970.modeler.controller.selector.helpers.CanvasHelper
import com.cout970.modeler.controller.selector.helpers.CanvasHelper.getMouseSpaceContext
import com.cout970.modeler.controller.selector.helpers.RotationHelper
import com.cout970.modeler.controller.selector.helpers.ScaleHelper
import com.cout970.modeler.controller.selector.helpers.TranslationHelper
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.functional.ITaskProcessor
import com.cout970.modeler.functional.tasks.TaskUpdateModel
import com.cout970.modeler.util.*
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.event.IInput
import com.cout970.modeler.view.gui.comp.canvas.Canvas
import com.cout970.modeler.view.gui.comp.canvas.CanvasContainer
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector2

/**
 * Created by cout970 on 2017/04/08.
 */
class Selector {

    lateinit var gui: Gui
    lateinit var processor: ITaskProcessor
    //    val projectController: ProjectController get() = gui.projectController
    val cursor = Cursor()
    val input: IInput get() = gui.input

    private var activeCanvas: Canvas? = null
    private var lastMousePos: IVector2? = null
    private var mousePress = false

    private var translationLastOffset = 0f
    private var rotationLastOffset = 0f
    private var scaleLastOffset = 0f

    fun update(canvasContainer: CanvasContainer) {
        activeCanvas = canvasContainer.selectedCanvas

        activeCanvas?.let { activeScene ->
            val context = getMouseSpaceContext(activeScene, input.mouse.getMousePos())
            val state = gui.state
            val click = Config.keyBindings.selectModelControls.check(input)

            if (state.holdingSelection == null) { // no selection

                // update hoverObject
                val camera = activeScene.cameraHandler.camera
                val viewport = activeScene.size.toIVector()
                val objects = cursor.getSelectableParts(gui, camera, viewport)
                state.hoveredObject = getHoveredObject(context, objects)

                // if clicked add object to selection
                if (click && state.hoveredObject != null) {
                    state.holdingSelection = state.hoveredObject
                    state.hoveredObject = null
                }
            } else { // has selection
                if (!click) { // end selection
                    // apply changes
                    state.tmpModel?.let { newModel ->
                        processor.processTask(TaskUpdateModel(gui.projectManager.model, newModel))
                    }
                    // reset selection
                    state.tmpModel = null
                    state.holdingSelection = null
                }
            }
        }
        val mousePos = input.mouse.getMousePos()
        val click = input.mouse.isButtonPressed(Mouse.BUTTON_LEFT)
        if (!click) {
            mousePress = false
            lastMousePos = null
            canvasContainer.canvas.forEach { canvas ->
                if (mousePos.isInside(canvas.absolutePosition, canvas.size.toIVector())) {
                    canvasContainer.selectedCanvas = canvas
                }
            }
        } else {
            if (lastMousePos == null && click) {
                lastMousePos = mousePos
            } else {
                lastMousePos?.let { onDrag(EventMouseDrag(it, mousePos)) }
            }
        }
    }

    fun onClick(e: EventMouseClick, canvas: Canvas) {
        val state = gui.state
        if (state.holdingSelection != null || state.hoveredObject != null) return

        val click = Config.keyBindings.selectModelControls.check(e)
        if (click && e.keyState == EnumKeyState.PRESS) {
            val pos = input.mouse.getMousePos()
            val context = CanvasHelper.getMouseSpaceContext(canvas, pos)
            val obj = getModelParts()
                    .mapNotNull { pair ->
                        val res = pair.first.rayTrace(context.mouseRay)
                        res?.let { first -> first to pair.second }
                    }
                    .getClosest(context.mouseRay)

            gui.selectionHandler.onSelect(obj?.second, gui)
            updateCursorCenter(gui.selectionHandler.getSelection())
        }
    }

    fun getModelParts(): List<Pair<IRayObstacle, ObjectRef>> {
        val model = gui.projectManager.model
        return model.objects.mapIndexed { index, obj ->
            RayTracer.toRayObstacle(obj) to ObjectRef(index)
        }
    }

    fun updateCursorCenter(selection: ISelection?) {
        if (selection == null) return
        val model = gui.state.tmpModel ?: gui.projectManager.model

        val newCenter = model.getSelectedObjects(selection)
                .map { it.getCenter() }
                .middle()

        cursor.center = newCenter
    }

    fun onDrag(event: EventMouseDrag) {

        activeCanvas?.let { activeScene ->
            val state = gui.state
            val sel = gui.selectionHandler

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


                    if (translationLastOffset != offset) {
                        translationLastOffset = offset
                        state.tmpModel = selectedObject.applyTranslation(offset, sel, gui.projectManager.model)
                        updateCursorCenter(gui.selectionHandler.getSelection())
                    }
                } else {
                    translationLastOffset = 0f
                }

                if (selectedObject is IRotable && state.transformationMode == TransformationMode.ROTATION) {
                    val context = CanvasHelper.getContext(activeScene, event.oldPos to event.newPos)
                    val offset = RotationHelper.getOffset(
                            obj = selectedObject,
                            input = input,
                            newContext = context.first,
                            oldContext = context.second
                    )

                    if (rotationLastOffset != offset) {
                        rotationLastOffset = offset
                        state.tmpModel = selectedObject.applyRotation(offset, sel, gui.projectManager.model)
                    }
                } else {
                    rotationLastOffset = 0f
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
                        state.tmpModel = selectedObject.applyScale(offset, sel, gui.projectManager.model)
                    }
                } else {
                    scaleLastOffset = 0f
                }
            }
        }
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