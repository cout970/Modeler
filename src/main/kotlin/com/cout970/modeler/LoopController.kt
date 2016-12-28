package com.cout970.modeler

import com.cout970.glutilities.structure.GameLoop
import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.util.ITickeable

/**
 * Created by cout970 on 2016/11/29.
 */
class LoopController(val tickeables: List<ITickeable>) {

    val timer = Timer()
    var stop = false
        set(i) {
            field = true
        }

    fun run() {
        GameLoop(this::tick).start()
    }

    private fun tick(loop: GameLoop) {
        timer.tick()
        tickeables.forEach(ITickeable::preTick)
        tickeables.forEach(ITickeable::tick)
        tickeables.forEach(ITickeable::postTick)
        if (stop) loop.stop()
    }
}