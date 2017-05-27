package com.cout970.modeler

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import javax.swing.JOptionPane
import javax.swing.UIManager

/**
 * Created by cout970 on 2016/11/29.
 */

fun main(args: Array<String>) {
    log(Level.NORMAL) { "Start of log" }
    log(Level.NORMAL) { "Program arguments: '${args.joinToString()}'" }

    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        JOptionPane.getRootFrame().isAlwaysOnTop = true
        log(Level.NORMAL) { "Using default system look and feel" }
    } catch (e: Exception) {
        e.print()
    }

    try {
        val init = Initializer()
        val state = init.init(args.toList())
        init.start(state)
    } catch (e: kotlin.Throwable) {
        e.print()
    } finally {
        log(Level.NORMAL) { "Eng of log" }
    }

    System.exit(0)
}