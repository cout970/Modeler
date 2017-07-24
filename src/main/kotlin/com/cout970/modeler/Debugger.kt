package com.cout970.modeler

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log

/**
 * Created by cout970 on 2017/01/20.
 */
object Debugger {

    private lateinit var state: ProgramState

    var drawVboCount = 0
    var drawRegionsCount = 0
    var drawVaoCount = 0
    var buildVaoCount = 0

    fun setInit(state: ProgramState) {
        this.state = state
    }

    fun debug(code: ProgramState.() -> Unit) {
        log(Level.DEBUG) { "Debug Start" }
        state.code()
        log(Level.DEBUG) { "Debug End" }
    }

    fun postTick() {

//        val count = GL30.glGenVertexArrays()
//        GL30.glDeleteVertexArrays(count)
//
//        println("drawVboCount: $drawVboCount")
//        println("drawRegionsCount: $drawRegionsCount")
//        println("drawVaoCount: $drawVaoCount")
//        println("buildVaoCount: $buildVaoCount")
//        println("totalVaoCount: $count")
//        println("++++++++++++++++++++++++++++++")

        drawVboCount = 0
        drawRegionsCount = 0
        drawVaoCount = 0
        buildVaoCount = 0
    }
}