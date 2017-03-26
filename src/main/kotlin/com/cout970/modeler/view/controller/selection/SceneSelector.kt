package com.cout970.modeler.view.controller.selection

import com.cout970.modeler.config.Config
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.action.ActionModifyModelShape
import com.cout970.modeler.util.*
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.view.controller.SceneSpaceContext
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.modeler.view.controller.TransformationMode
import com.cout970.modeler.view.scene.Scene
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.extensions.*
import org.joml.Vector3d

/**
 * Created by cout970 on 2017/03/26.
 */
class SceneSelector(val sceneController: SceneController, val modelEditor: ModelEditor) {

    fun update(scene: Scene) {

        val context = getMouseSpaceContext(scene)
        val cache = scene.selectorCache

        val selectionManager = sceneController.modelProvider.selectionManager
        cache.currentContext = context

        // No selected elements
        if (selectionManager.hasNoSelection()) {
            cache.hoveredObject = null
            cache.selectedObject = null
            return
        }

        // No hovered element
        if (cache.oldContext == null) {
            cache.hoveredObject = getHoveredObject(context, scene.cursor)

            val click = Config.keyBindings.selectModelControls.check(sceneController.input)
            if (click && cache.hoveredObject != null) {

                cache.selectedObject = cache.hoveredObject
                cache.hoveredObject = null
                // Selecting an element
                cache.oldContext = context
            }
            return
        }

        // No transformation
        if (cache.selectedObject == null) return

        // End of transformation
        val click = Config.keyBindings.selectModelControls.check(sceneController.input)
        if (!click) {
            cache.oldContext = null
            cache.hoveredObject = null
            cache.selectedObject = null
            cache.cursorCache.model = null

            modelEditor.historyRecord.doAction(ActionModifyModelShape(modelEditor, cache.model!!))
            cache.model = null
            return
        }

        // Apply transformation
        val selectionTracker = when (sceneController.transformationMode) {
            TransformationMode.TRANSLATION -> TranslationCursorTracker
            TransformationMode.ROTATION -> RotationCursorTracker
            TransformationMode.SCALE -> ScaleCursorTracker
        }

        if (cache.cursorCache.model == null) {
            cache.cursorCache.model = modelEditor.model
        }

        cache.cursorCache = selectionTracker.updateCache(
                scene = scene,
                obj = cache.selectedObject!!,
                oldCache = cache.cursorCache,
                oldContext = cache.oldContext!!,
                newContext = context
        )
        cache.model = selectionTracker.getPhantomModel(this, cache.selectedObject!!, cache.cursorCache)
        cache.cursorCache.model = cache.model
    }

    fun getHoveredObject(ctx: SceneSpaceContext, cursor: Cursor): ISelectable? {
        val ray = ctx.mouseRay

        val resX: RayTraceResult? = cursor.rayTrace(SelectionAxis.X, ray)
        val resY: RayTraceResult? = cursor.rayTrace(SelectionAxis.Y, ray)
        val resZ: RayTraceResult? = cursor.rayTrace(SelectionAxis.Z, ray)

        val list = mutableListOf<Pair<RayTraceResult, SelectionAxis>>()
        resX?.let { list += it to SelectionAxis.X }
        resY?.let { list += it to SelectionAxis.Y }
        resZ?.let { list += it to SelectionAxis.Z }

        if (list.isNotEmpty()) {
            list.sortBy { it.first.hit.distance(ray.start) }
            return list.first().second
        } else {
            return null
        }
    }

    fun getMouseSpaceContext(scene: Scene): SceneSpaceContext {
        val matrix = scene.getMatrixMVP().toJOML()
        val mousePos = scene.sceneController.input.mouse.getMousePos() - scene.absolutePosition
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