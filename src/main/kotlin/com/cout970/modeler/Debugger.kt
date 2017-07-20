package com.cout970.modeler

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log

/**
 * Created by cout970 on 2017/01/20.
 */
object Debugger {

    private lateinit var state: ProgramState

    fun setInit(state: ProgramState) {
        this.state = state
    }

    fun debug(code: ProgramState.() -> Unit) {
        log(Level.DEBUG) { "Debug Start" }
        state.code()
        log(Level.DEBUG) { "Debug End" }
    }
}