package com.cout970.modeler.view.controller.selection

import com.cout970.modeler.config.Config
import com.cout970.modeler.model.Model
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.controller.SceneSpaceContext
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.modeler.view.scene.Scene
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/03/26.
 */

object ScaleCursorTracker : AbstractCursorTracker() {

    override fun updateCache(scene: Scene, obj: ISelectable, oldCache: CursorTrackerCache,
                             oldContext: SceneSpaceContext, newContext: SceneSpaceContext): CursorTrackerCache {

        val diff = projectAxis(newContext.mvpMatrix, obj)
        val direction = (diff.second - diff.first)
        val viewportSize = scene.size.toIVector()

        val oldMouse = ((oldContext.mousePos / viewportSize) * 2 - 1).run { vec2Of(x, -yd) }
        val newMouse = ((newContext.mousePos / viewportSize) * 2 - 1).run { vec2Of(x, -yd) }

        val old = direction.project(oldMouse * viewportSize)
        val new = direction.project(newMouse * viewportSize)

        val move = (new - old) * scene.camera.zoom / Config.cursorArrowsSpeed

        if (Config.keyBindings.disableGridMotion.check(scene.sceneController.input)) {
            oldCache.offset = Math.round(move * 16) / 16f
        } else if (Config.keyBindings.disablePixelGridMotion.check(scene.sceneController.input)) {
            oldCache.offset = Math.round(move * 4) / 4f
        } else {
            oldCache.offset = Math.round(move).toFloat()
        }

        return oldCache
    }

    override fun getPhantomModel(selector: SceneSelector, obj: ISelectable, cache: CursorTrackerCache): Model {

        if (cache.offset != cache.lastOffset) {
            cache.lastOffset = cache.offset

            val model = selector.modelEditor.model
            val selection = selector.modelEditor.selectionManager.getSelectedVertexPos(model)
            val editTool = selector.modelEditor.editTool
            //TODO generalize this to ISelectable
            cache.model = editTool.scale(
                    source = model,
                    selection = selection,
                    center = selector.sceneController.cursorCenter,
                    axis = (obj as SelectionAxis).direction,
                    offset = cache.offset
            )
        }
        return cache.model!!
    }
}