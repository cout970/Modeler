package com.cout970.modeler.newView

import com.cout970.modeler.newView.selector.Selector

/**
 * Created by cout970 on 2017/04/08.
 */
class ContentPanel {

    val controllerState = ControllerState()
    val selector = Selector()
    val scenes = mutableListOf<Scene>()
    var selectedScene: Scene? = null
}