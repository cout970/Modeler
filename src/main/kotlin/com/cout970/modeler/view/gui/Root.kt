package com.cout970.modeler.view.gui

import org.liquidengine.legui.component.Frame

/**
 * Created by cout970 on 2017/05/14.
 */

class Root : Frame(1f, 1f) {

    var mainPanel: MutablePanel? = null
        set(value) {
            field = value
            clearComponents()
            addComponent(value)
        }
}