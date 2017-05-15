package com.cout970.modeler

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log

/**
 * Created by cout970 on 2017/01/20.
 */
object Debugger {

    private lateinit var initializer: Initializer

    fun setInit(initializer: Initializer) {
        this.initializer = initializer
    }

    fun debug(code: Initializer.() -> Unit) {
        log(Level.DEBUG) { "Debug Start" }
        initializer.code()
        log(Level.DEBUG) { "Debug End" }
    }
}