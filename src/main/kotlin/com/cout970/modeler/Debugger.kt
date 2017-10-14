package com.cout970.modeler

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log

/**
 * Created by cout970 on 2017/01/20.
 */
object Debugger {

    private lateinit var state: Program

    var drawVboCount = 0
    var drawRegionsCount = 0
    var drawVaoCount = 0
    var buildVaoCount = 0

    fun setInit(state: Program) {
        this.state = state
    }

    fun debug(code: Program.() -> Unit) {
        log(Level.DEBUG) { "Debug Start" }
        state.code()
        log(Level.DEBUG) { "Debug End" }
    }

    fun debugLog(a: Any): Boolean {
        if (a is String) {
            log(level = Level.DEBUG) { "$a -> ${a.length}" }
        } else if (a is StringBuffer) {
            log(level = Level.DEBUG) { a.length.toString() }
        } else {
            log(level = Level.DEBUG) { a.toString() }
        }
        return false
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