package com.cout970.modeler.view.scene

import com.cout970.modeler.event.EventController
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.view.render.RenderManager
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.util.ColorConstants

/**
 * Created by cout970 on 2016/12/27.
 */
abstract class Scene(val sceneController: SceneController) : Panel() {

    var camera = Camera.DEFAULT

    abstract fun render(renderManager: RenderManager)
    abstract fun update()
    abstract fun registerListeners(eventController: EventController)

    init {
        backgroundColor = ColorConstants.transparent()
    }
}

