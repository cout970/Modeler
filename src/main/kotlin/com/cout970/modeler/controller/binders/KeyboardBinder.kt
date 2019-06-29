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
                newProject.check(e) -> "project.new"
                loadProject.check(e) -> "project.load"
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
                toggleVisibility.check(e) -> "model.toggle.visibility"

                showLeftPanel.check(e) -> "show.left.panel"
                showRightPanel.check(e) -> "show.right.panel"
                showBottomPanel.check(e) -> "show.bottom.panel"
                showSearchBar.check(e) -> "show.search.panel"

                setObjectSelectionType.check(e) -> "set.selection.type.object"
                setFaceSelectionType.check(e) -> "set.selection.type.face"
                setEdgeSelectionType.check(e) -> "set.selection.type.edge"
                setVertexSelectionType.check(e) -> "set.selection.type.vertex"

                switchOrthoProjection.check(e) -> "view.switch.ortho"
                setTextureMode.check(e) -> "view.set.texture.mode"
                setModelMode.check(e) -> "view.set.model.mode"
                setTranslationCursorMode.check(e) -> "cursor.set.mode.translate"
                setRotationCursorMode.check(e) -> "cursor.set.mode.rotate"
                setScaleCursorMode.check(e) -> "cursor.set.mode.scale"
                toggleVisibility.check(e) -> "model.toggle.visibility"
                selectAll.check(e) -> "model.select.all"
                moveCameraToCursor.check(e) -> "camera.move.to.cursor"
                splitTexture.check(e) -> "model.texture.split"
                scaleTextureUp.check(e) -> "model.texture.scale.up"
                scaleTextureDown.check(e) -> "model.texture.scale.down"

                joinObjects.check(e) -> "model.obj.join"
                splitSelection.check(e) -> "model.obj.split"
                arrangeUvs.check(e) -> "model.obj.arrange.uv"
                extrudeFace.check(e) -> "model.face.extrude"
                setIsometricView.check(e) -> "camera.set.isometric"

                newColoredMaterial.check(e) -> "material.new.colored"
                addAnimation.check(e) -> "animation.add"
                toggleAnimation.check(e) -> "animation.state.toggle"


                e.keycode == Keyboard.KEY_F1 -> "debug"
                e.keycode == Keyboard.KEY_F2 -> "debug.changeColors"
                e.keycode == Keyboard.KEY_F3 -> "debug.show.profiling"
                // wtf F4 doesn't work
                e.keycode == Keyboard.KEY_F5 -> "debug.toggle.dynamic"
                e.keycode == Keyboard.KEY_F6 -> "debug.gc"
                e.keycode == Keyboard.KEY_F7 -> "debug.print.focused"
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