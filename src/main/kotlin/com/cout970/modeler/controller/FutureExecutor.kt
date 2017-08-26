package com.cout970.modeler.controller

import com.cout970.modeler.Program
import com.cout970.modeler.util.ITickeable
import java.util.*

/**
 * Created by cout970 on 2017/07/17.
 */

interface IFutureExecutor {
    fun addToQueue(function: (Program) -> Unit)
}

class FutureExecutor : ITickeable, IFutureExecutor {

    lateinit var programState: Program
    private val queue = LinkedList<(Program) -> Unit>()

    override fun addToQueue(function: (Program) -> Unit) {
        queue.add(function)
    }

    override fun tick() = Unit

    override fun postTick() {
        while (queue.isNotEmpty()) {
            queue.poll().invoke(programState)
        }
    }
}