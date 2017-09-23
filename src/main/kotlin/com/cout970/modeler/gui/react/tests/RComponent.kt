package com.cout970.modeler.gui.react.tests

import com.cout970.modeler.gui.react.ReactContext
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/23.
 */
abstract class RComponent<out P : Any, S : Any>(val props: P) : Component() {

    lateinit var context: ReactContext

    private lateinit var stateField: S

    var state: S
        get() = stateField
        set(value) {
            stateField = value
        }


    abstract fun render(context: RContext): Component
}