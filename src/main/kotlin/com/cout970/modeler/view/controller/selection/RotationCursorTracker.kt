package com.cout970.modeler.view.controller.selection

import com.cout970.modeler.config.Config
import com.cout970.modeler.model.Model
import com.cout970.modeler.selection.vertexPosSelection
import com.cout970.modeler.util.getClosestPointOnLineSegment
import com.cout970.modeler.view.controller.SceneSpaceContext
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.modeler.view.scene.Scene
import com.cout970.raytrace.Ray
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/03/26.
 */
object RotationCursorTracker : AbstractCursorTracker() {

    override fun updateCache(scene: Scene, obj: ISelectable, oldCache: CursorTrackerCache,
                             oldContext: SceneSpaceContext, newContext: SceneSpaceContext): CursorTrackerCache {
        val func = { mouseRay: Ray ->

            val closest = getClosestPointOnLineSegment(mouseRay.start, mouseRay.end, scene.sceneController.cursorCenter)
            val dir = (closest - scene.sceneController.cursorCenter).normalize()

            when (obj) {
                SelectionAxis.Z -> dir.run { Math.atan2(yd, zd) }
                SelectionAxis.X -> dir.run { Math.atan2(-xd, zd) }
                SelectionAxis.Y -> dir.run { Math.atan2(xd, yd) }
                else -> 0.0
            }
        }

        val new = with(oldContext.mouseRay, func)
        val old = with(newContext.mouseRay, func)

        val change = new - old

        val move = Math.toDegrees(change) / 360.0 * 32 * Config.cursorRotationSpeed

        if (Config.keyBindings.disableGridMotion.check(scene.sceneController.input)) {
            oldCache.offset = Math.round(move * 16) / 16f
        } else if (Config.keyBindings.disablePixelGridMotion.check(scene.sceneController.input)) {
            oldCache.offset = Math.round(move * 4) / 4f
        } else {
            oldCache.offset = Math.round(move).toFloat()
        }
        oldCache.offset = Math.toRadians(oldCache.offset.toDouble() * 360.0 / 32).toFloat()

        return oldCache
    }

    override fun getPhantomModel(selector: SceneSelector, obj: ISelectable, cache: CursorTrackerCache): Model {
        if (cache.lastOffset != cache.offset) {
            cache.lastOffset = cache.offset

            val model = selector.modelEditor.model
            val selection = selector.modelEditor.selectionManager.vertexPosSelection
            val editTool = selector.modelEditor.editTool
            val center = selector.modelEditor.selectionManager.getSelectionCenter(model)

            val axisDir = when (obj) {
                SelectionAxis.X -> SelectionAxis.Y
                SelectionAxis.Y -> SelectionAxis.Z
                SelectionAxis.Z -> SelectionAxis.X
                else -> SelectionAxis.NONE
            }
            val rot = axisDir.direction.toVector4(cache.offset).fromAxisAngToQuat()
            cache.model = editTool.rotate(model, selection, center, rot)
        }
        return cache.model!!
    }
}