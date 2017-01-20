package com.cout970.modeler

import com.cout970.modeler.log.Level
import com.cout970.modeler.log.log

/**
 * Created by cout970 on 2017/01/20.
 */
object Debugger {

    lateinit var initializer: Initializer

    fun debug(code: Initializer.() -> Unit) {
        log(Level.DEBUG) { "Debug Start" }
        initializer.code()
        log(Level.DEBUG) { "Debug End" }
    }
}