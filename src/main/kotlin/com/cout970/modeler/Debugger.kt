package com.cout970.modeler

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import java.io.File

/**
 * Created by cout970 on 2017/01/20.
 */
object Debugger {

    private lateinit var state: Program

    val STATIC_DEBUG: Boolean by lazy { System.getProperty("user.name") == "cout970" && File(".").absolutePath.endsWith("run/.") }
    var DYNAMIC_DEBUG: Boolean = false

    var drawVboCount = 0
    var drawRegionsCount = 0
    var drawVaoCount = 0
    var buildVaoCount = 0

    var showProfiling = false

    fun setInit(state: Program) {
        this.state = state
    }

    fun debug(code: Program.() -> Unit) {
        log(Level.DEBUG) { "Debug Start" }
        state.code()
        log(Level.DEBUG) { "Debug End" }
    }

    fun debugLog(a: Any): Boolean {
        when (a) {
            is String -> log(level = Level.DEBUG) { "$a -> ${a.length}" }
            is StringBuffer -> log(level = Level.DEBUG) { a.length.toString() }
            else -> log(level = Level.DEBUG) { a.toString() }
        }
        return false
    }

    fun printStackTrace() {
        val trace = Thread.currentThread().stackTrace
        val usefulTrace = trace.takeLast(trace.size - 2)
        val string = usefulTrace.joinToString("\n") {
            "\tat ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})"
        }

        println("Requested printStackTrace:")
        println(string)
        println()
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

// Marks code that is a hack and needs to be fixed or change in the future
fun hack(code: () -> Unit) = code()
