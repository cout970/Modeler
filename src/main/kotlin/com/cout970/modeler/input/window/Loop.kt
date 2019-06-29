package com.cout970.modeler.input.window

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.util.ITickeable

/**
 * Created by cout970 on 2016/11/29.
 */
class Loop(val tickeables: List<ITickeable>, val timer: Timer, val shouldClose: () -> Boolean) {

    companion object {
        var currentTick = 0L
            private set
    }

    fun run() {
        while (!shouldClose()) {
            currentTick++
            timer.tick()
            tickeables.forEach(ITickeable::preTick)
            tickeables.forEach(ITickeable::tick)
            tickeables.forEach(ITickeable::postTick)
        }
    }
}