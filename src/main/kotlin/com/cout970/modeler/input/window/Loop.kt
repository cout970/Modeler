package com.cout970.modeler.input.window

import com.cout970.glutilities.structure.GameLoop
import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.util.ITickeable

/**
 * Created by cout970 on 2016/11/29.
 */
class Loop(val tickeables: List<ITickeable>, val timer: Timer, val shouldClose: () -> Boolean) {

    fun run() {
        GameLoop(this::tick).start()
    }

    private fun tick(loop: GameLoop) {
        timer.tick()
        tickeables.forEach(ITickeable::preTick)
        tickeables.forEach(ITickeable::tick)
        tickeables.forEach(ITickeable::postTick)
        if (shouldClose()) loop.stop()
    }
}