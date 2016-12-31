package com.cout970.modeler

import com.cout970.modeler.log.Level
import com.cout970.modeler.log.log
import com.cout970.modeler.log.print

/**
 * Created by cout970 on 2016/11/29.
 */

fun main(args: Array<String>) {
    log(Level.NORMAL) { "Start of log" }
    try {
        Init().run()
    } catch (e: Exception) {
        e.print()
    } finally {
        log(Level.NORMAL) { "Eng of log" }
    }

    System.exit(0)
}