package com.cout970.modeler.newView.selector

import com.cout970.modeler.config.Config
import com.cout970.modeler.event.IInput
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.action.ActionModifyModelShape
import com.cout970.modeler.newView.ControllerState
import com.cout970.modeler.newView.EventMouseDrag
import com.cout970.modeler.newView.SceneSpaceContext
import com.cout970.modeler.newView.TransformationMode
import com.cout970.modeler.newView.gui.ContentPanel
import com.cout970.modeler.newView.gui.Scene
import com.cout970.modeler.newView.viewtarget.ViewTarget
import com.cout970.modeler.util.*
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*
import org.joml.Vector3d

/**
 * Created by cout970 on 2017/04/08.
 */
class Selector(val modelEditor: ModelEditor, val contentPanel: ContentPanel, val input: IInput) {

    private var activeScene: Scene? = null

    fun update() {
        activeScene = contentPanel.selectedScene

        activeScene?.let { activeScene ->
            val context = getMouseSpaceContext(activeScene, input.mouse.getMousePos())
            val target = activeScene.viewTarget

            val click = Config.keyBindings.selectModelControls.check(input)
            if (target.selectedObject == null) {
                updateHovered(target, context, activeScene)

                val hovered = target.hoveredObject

                if (click && hovered != null) {
                    activeScene.tmpCursorCenter = activeScene.cursor.center
                    target.selectedObject = hovered
                    target.hoveredObject = null
                }
            } else if (!click) {
                activeScene.viewTarget.tmpModel?.let { model ->
                    modelEditor.historyRecord.doAction(ActionModifyModelShape(modelEditor, model))
                }
                activeScene.tmpCursorCenter = null
                target.selectedObject = null
                updateHovered(target, context, activeScene)
            }
        }
    }


    private var translationLastOffset = 0f
    private var rotationLastOffset = 0f
    private var scaleLastOffset = 0f

    fun onDrag(state: ControllerState, event: EventMouseDrag) {
        activeScene?.let { activeScene ->
            activeScene.viewTarget.selectedObject?.let { selectedObject ->

                if (selectedObject is ITranslatable && state.transformationMode == TransformationMode.TRANSLATION) {
                    val context = getContext(activeScene, event)
                    val offset = TranslationHelper.getOffset(
                            obj = selectedObject,
                            scene = activeScene,
                            input = input,
                            newContext = context.first,
                            oldContext = context.second
                    )

                    if (rotationLastOffset != offset) {
                        rotationLastOffset = offset
                        activeScene.viewTarget.tmpModel = selectedObject.applyTranslation(offset, modelEditor.model)
                    }
                } else {
                    rotationLastOffset = 0f
                }

                if (selectedObject is IRotable && state.transformationMode == TransformationMode.ROTATION) {
                    val context = getContext(activeScene, event)
                    val offset = RotationHelper.getOffset(
                            obj = selectedObject,
                            input = input,
                            newContext = context.first,
                            oldContext = context.second
                    )

                    if (translationLastOffset != offset) {
                        translationLastOffset = offset
                        activeScene.viewTarget.tmpModel = selectedObject.applyRotation(offset, modelEditor.model)
                    }
                } else {
                    translationLastOffset = 0f
                }

                if (selectedObject is IScalable && state.transformationMode == TransformationMode.SCALE) {
                    val context = getContext(activeScene, event)
                    val offset = ScaleHelper.getOffset(
                            obj = selectedObject,
                            scene = activeScene,
                            input = input,
                            newContext = context.first,
                            oldContext = context.second
                    )

                    if (scaleLastOffset != offset) {
                        scaleLastOffset = offset
                        activeScene.viewTarget.tmpModel = selectedObject.applyScale(offset, modelEditor.model)
                    }
                } else {
                    scaleLastOffset = 0f
                }
            }
        }
    }

    private fun getContext(scene: Scene, event: EventMouseDrag): Pair<SceneSpaceContext, SceneSpaceContext> {
        val oldContext = getMouseSpaceContext(scene, event.oldPos)
        val newContext = getMouseSpaceContext(scene, event.newPos)
        return newContext to oldContext
    }

    private fun updateHovered(target: ViewTarget, context: SceneSpaceContext, scene: Scene) {
        target.hoveredObject = getHoveredObject(context, target.getSelectableObjects(scene))
    }

    private fun getHoveredObject(ctx: SceneSpaceContext, objs: List<ISelectable>): ISelectable? {
        val ray = ctx.mouseRay
        val list = mutableListOf<Pair<RayTraceResult, ISelectable>>()

        objs.forEach { obj ->
            val res = obj.rayTrace(ray)
            res?.let { list += it to obj }
        }

        return list.getClosest(ray)?.second
    }

    fun getMouseSpaceContext(scene: Scene, absMousePos: IVector2): SceneSpaceContext {
        val matrix = scene.getMatrixMVP().toJOML()
        val mousePos = absMousePos - scene.absolutePosition
        val viewportSize = scene.size.toIVector()
        val viewport = intArrayOf(0, 0, viewportSize.xi, viewportSize.yi)

        val a = matrix.unproject(vec3Of(mousePos.x, viewportSize.yd - mousePos.yd, 0.0).toJoml3d(),
                viewport, Vector3d()).toIVector()
        val b = matrix.unproject(vec3Of(mousePos.x, viewportSize.yd - mousePos.yd, 1.0).toJoml3d(),
                viewport, Vector3d()).toIVector()

        val mouseRay = Ray(a, b)

        return SceneSpaceContext(mousePos, mouseRay, matrix)
    }
}