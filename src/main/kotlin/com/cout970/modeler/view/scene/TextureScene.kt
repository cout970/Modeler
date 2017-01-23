package com.cout970.modeler.view.scene

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.event.IEventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.modeleditor.IModelProvider
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.window.WindowHandler

/**
 * Created by cout970 on 2017/01/02.
 */
class TextureScene(modelEditor: IModelProvider, windowHandler: WindowHandler, controller: SceneController) : Scene(
        modelEditor, windowHandler, controller) {

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