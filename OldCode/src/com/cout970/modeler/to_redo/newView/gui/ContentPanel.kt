package com.cout970.modeler.to_redo.newView.gui

import com.cout970.modeler.to_redo.newView.ControllerState
import com.cout970.modeler.to_redo.newView.SceneHandler
import com.cout970.modeler.to_redo.newView.gui.comp.CPanel

/**
 * Created by cout970 on 2017/04/08.
 */
class ContentPanel : CPanel() {

    val controllerState = ControllerState()
    val sceneHandler = SceneHandler(this)

    // references
    val scenes: List<Scene> get() = sceneHandler.scenes
    val selectedScene: Scene? get() = sceneHandler.selectedScene

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContentPanel) return false
        if (!super.equals(other)) return false

        if (scenes != other.scenes) return false
        if (selectedScene != other.selectedScene) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + scenes.hashCode()
        result = 31 * result + (selectedScene?.hashCode() ?: 0)
        return result
    }
}