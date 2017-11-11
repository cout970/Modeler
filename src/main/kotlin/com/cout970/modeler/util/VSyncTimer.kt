package com.cout970.modeler.util

import com.cout970.glutilities.structure.Timer

/**
 * Created by cout970 on 2017/09/21.
 */

class VSyncTimer {

    var enabled = false
    var fps = 60

    private var lastTime = Timer.secTime * 1000

    fun waitIfNecessary() {
        if (!enabled) return
        val now = Timer.secTime * 1000
        val current = (now - lastTime).toLong()
        lastTime = now

        val objective = (1000 / fps).toLong()
        val sleep = Math.max(0L, objective - current)
        Thread.sleep(sleep)
    }
}