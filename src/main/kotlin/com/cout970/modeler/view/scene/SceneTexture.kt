package com.cout970.modeler.view.scene

import com.cout970.glutilities.event.EventMouseClick
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.view.controller.TextureSelector
import com.cout970.modeler.window.WindowHandler

/**
 * Created by cout970 on 2017/01/02.
 */
class SceneTexture(modelEditor: ModelEditor, windowHandler: WindowHandler, controller: SceneController) : Scene(
        modelEditor, windowHandler, controller) {

    val textureSelector = TextureSelector(this, controller, modelEditor)

    init {
        camera = Camera.DEFAULT.copy(angleX = 0.0, angleY = 0.0)
    }

    override fun onEvent(e: EventMouseClick) = false
}