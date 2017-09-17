package com.cout970.modeler.gui.react.leguicomp

import com.cout970.modeler.gui.react.IScalable
import org.liquidengine.legui.component.Component as LeguiComponent
import org.liquidengine.legui.component.Panel as LeguiPanel

/**
 * Created by cout970 on 2017/09/07.
 */

class Panel : LeguiPanel<LeguiComponent>() {

    var width: Float
        get() = size.x
        set(x) {
            size.x = x
        }

    var height: Float
        get() = size.y
        set(y) {
            size.y = y
        }

    var scalable: IScalable? = null

    operator fun LeguiComponent.unaryPlus() {
        add(this)
    }
}