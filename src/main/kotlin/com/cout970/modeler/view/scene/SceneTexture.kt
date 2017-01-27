package com.cout970.modeler.view.scene

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.event.IEventController
import com.cout970.modeler.event.IEventListener
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

    override fun registerListeners(eventHandler: IEventController) {
        eventHandler.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean {
                if (e.keyState == EnumKeyState.PRESS) {

                }
                return false
            }
        })
    }
}