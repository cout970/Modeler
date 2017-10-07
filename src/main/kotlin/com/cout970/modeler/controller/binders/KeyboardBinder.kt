package com.cout970.modeler.controller.binders

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.config.Config

/**
 * Created by cout970 on 2017/07/17.
 */
class KeyboardBinder(val dispatcher: Dispatcher) {

    fun onEvent(e: EventKeyUpdate): Boolean {
        Config.keyBindings.apply {
            val key = when {
                saveProject.check(e) -> "project.save"
                saveProjectAs.check(e) -> "project.save.as"
                exportModel.check(e) -> "model.export"
                importModel.check(e) -> "model.import"
                delete.check(e) -> "model.selection.delete"
                copy.check(e) -> "model.selection.copy"
                cut.check(e) -> "model.selection.cut"
                paste.check(e) -> "model.selection.paste"
                undo.check(e) -> "model.undo"
                redo.check(e) -> "model.redo"

                showLeftPanel.check(e) -> "show.left.panel"
                showRightPanel.check(e) -> "show.right.panel"
                showBottomPanel.check(e) -> "show.bottom.panel"

                switchOrthoProjection.check(e) -> "view.switch.ortho"
                setTextureMode.check(e) -> "view.set.texture.mode"
                setModelMode.check(e) -> "view.set.model.mode"
                setTranslationCursorMode.check(e) -> "cursor.set.mode.translate"
                setRotationCursorMode.check(e) -> "cursor.set.mode.rotate"
                setScaleCursorMode.check(e) -> "cursor.set.mode.scale"
                toggleVisibility.check(e) -> "model.toggle.visibility"
                selectAll.check(e) -> "model.select.all"
                e.keycode == Keyboard.KEY_F1 -> "debug"
                else -> null
            }
            if (key != null) {
                dispatcher.onEvent(key, null)
                return true
            }
        }
        return false
    }
}