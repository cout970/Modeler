package com.cout970.modeler.view.controller.selection

import com.cout970.modeler.config.Config
import com.cout970.modeler.model.Model
import com.cout970.modeler.selection.vertexTexSelection
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.controller.SceneSpaceContext
import com.cout970.modeler.view.scene.Scene
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/03/26.
 */

object TranslationCursorTracker : AbstractCursorTracker() {

    override fun updateCache(scene: Scene, obj: ISelectable, oldCache: CursorTrackerCache,
                             oldContext: SceneSpaceContext, newContext: SceneSpaceContext): CursorTrackerCache {

        val diff = projectAxis(newContext.mvpMatrix, obj)
        val direction = (diff.second - diff.first)
        val viewportSize = scene.size.toIVector()

        val oldMousePos = ((oldContext.mousePos / viewportSize) * 2 - 1).run { vec2Of(x, -yd) }
        val newMousePos = ((newContext.mousePos / viewportSize) * 2 - 1).run { vec2Of(x, -yd) }

        val old = direction.project(oldMousePos * viewportSize)
        val new = direction.project(newMousePos * viewportSize)

        val move = (new - old) * scene.camera.zoom / Config.cursorArrowsSpeed

        // Move using increments of 1, 1/4, 1/16
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

            applyModel(selector, obj, cache)
        }
        return cache.model!!
    }

    fun applyModel(selector: SceneSelector, obj: ISelectable, cache: CursorTrackerCache) {
        val model = selector.modelEditor.model
        val selection = selector.modelEditor.selectionManager.getSelectedVertexPos(model)
        val editTool = selector.modelEditor.editTool

        cache.model = editTool.translate(model, selection, obj.translationAxis * cache.offset)
    }

    fun applyTexture(selector: SceneSelector, obj: ISelectable, cache: CursorTrackerCache) {
        val model = selector.modelEditor.model
        val selection = selector.modelEditor.selectionManager.vertexTexSelection
        val editTool = selector.modelEditor.editTool

        cache.model = editTool.translateTexture(model, selection, obj.translationAxis * cache.offset)
    }
}