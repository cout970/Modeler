package com.cout970.modeler

import com.cout970.modeler.log.Level
import com.cout970.modeler.log.log
import com.cout970.modeler.log.print

/**
 * Created by cout970 on 2016/11/29.
 */

fun main(args: Array<String>) {
    log(Level.NORMAL) { "Start of log" }
    log(Level.NORMAL) { "Program arguments: '${args.joinToString()}'" }
    try {
        Initializer(args.toList()).start()
    } catch (e: Exception) {
        e.print()
    } finally {
        log(Level.NORMAL) { "Eng of log" }
    }

    System.exit(0)
}